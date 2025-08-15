package handlers

import (
	"context"
	"fmt"
	"net/http"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/shopspring/decimal"
)

type CartHandler struct {
	r             *repositories.CartRepository
	catalogClient catalogpb.CatalogServiceClient
}

func NewCartHandler(r *repositories.CartRepository, catalogClient catalogpb.CatalogServiceClient) *CartHandler {
	return &CartHandler{r: r, catalogClient: catalogClient}
}

// @Summary get user's cart
// @ID get-users-cart
// @Produce json
// @Success 200 {object} models.Cart
// @Router / [get]
func (h *CartHandler) GetCart(c *gin.Context) {
	c.JSON(http.StatusOK, gin.H{
		"cart": c.MustGet("cart").(models.Cart),
	})
}

// @Summary update user's cart
// @ID update-users-cart
// @Produce json
// @Param data body putCartRequest true "put cart data"
// @Success 200 {object} models.Cart
// @Router / [put]
func (h *CartHandler) PutCart(c *gin.Context) {
	cart := c.MustGet("cart").(models.Cart)

	input, err := bindPutCartInput(c)
	if err != nil {
		fmt.Println(err)
		c.JSON(http.StatusBadRequest, &models.ErrorResponse{Key: "request", Value: "read"})
		return
	}

	for _, item := range input.Items {
		ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
		defer cancel()

		cart, err = updateCartItems(cart, item.ProductID, item.Quantity, h.catalogClient, ctx)
		if err != nil {
			fmt.Println(err)
			c.JSON(http.StatusBadRequest, &models.ErrorResponse{Key: "request", Value: "update"})
			return
		}
	}

	if len(input.Items) == 0 {
		cart = models.Cart{
			ID:         cart.ID,
			OwnerID:    cart.OwnerID,
			CartItems:  []models.CartItem{},
			TotalPrice: decimal.NewFromInt(0),
		}
	}

	cart.TotalPrice = calculateTotal(cart.CartItems)

	if _, err := h.r.SaveCart(context.Background(), cart); err != nil {
		fmt.Println(err)
		c.JSON(http.StatusBadRequest, &models.ErrorResponse{Key: "request", Value: "save"})
		return
	}

	c.JSON(http.StatusOK, gin.H{"cart": cart})
}

// @Description PUT cart data
type putCartRequest struct {
	Items []putCartInput `json:"items" binding:"required"`
}

// @Description PUT cart input
type putCartInput struct {
	ProductID string `json:"product_id" binding:"required"`
	Quantity  int32  `json:"quantity" binding:"required"`
}

func bindPutCartInput(c *gin.Context) (putCartRequest, error) {
	var input putCartRequest
	if err := c.ShouldBindJSON(&input); err != nil {
		return input, err
	}
	return input, nil
}

func updateCartItems(cart models.Cart, productID string, quantity int32, catalogClient catalogpb.CatalogServiceClient, ctx context.Context) (models.Cart, error) {
	price, err := resolveProductPrice(productID, catalogClient, ctx)
	if err != nil {
		return models.Cart{}, err
	}

	for i, item := range cart.CartItems {
		if item.ProductID == productID {
			cart.CartItems[i].Quantity = quantity
			cart.CartItems[i].UnitPrice = price
			return cart, nil
		}
	}
	cart.CartItems = append(cart.CartItems, models.CartItem{
		ID:        uuid.New().String(),
		ProductID: productID,
		Quantity:  quantity,
		UnitPrice: price,
	})
	return cart, nil
}

func calculateTotal(items []models.CartItem) decimal.Decimal {
	total := decimal.NewFromInt(0)
	for _, item := range items {
		total = total.Add(item.UnitPrice.Mul(decimal.NewFromInt(int64(item.Quantity))))
	}
	return total
}

func resolveProductPrice(productID string, catalogClient catalogpb.CatalogServiceClient, ctx context.Context) (decimal.Decimal, error) {
	tCtx, cancel := context.WithTimeout(ctx, 5*time.Second)
	defer cancel()

	resp, err := catalogClient.GetProduct(tCtx, &catalogpb.GetProductRequest{
		Id: productID,
	})
	if err != nil {
		return decimal.Decimal{}, err
	}

	price, err := decimal.NewFromString(resp.Price)
	if err != nil {
		return decimal.Decimal{}, err
	}

	return price, nil
}
