package handlers

import (
	"context"
	"errors"
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/clients"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/shopspring/decimal"
)

type CartHandler struct {
	cartRepository *repositories.CartRepository
	catalogClient  *clients.CatalogClient
}

func NewCartHandler(cartRepository *repositories.CartRepository, catalogClient *clients.CatalogClient) *CartHandler {
	return &CartHandler{cartRepository: cartRepository, catalogClient: catalogClient}
}

// @Summary get user's cart
// @ID get-users-cart
// @Produce json
// @Success 200 {object} models.Cart
// @Param Authorization header string false "Optional bearer JWT"
// @Router / [get]
func (handler *CartHandler) GetCart(c *gin.Context) {
	cart := c.MustGet("cart").(models.Cart)
	err := fetchCartItemsDetails(cart, handler.catalogClient, c)
	if err != nil {
		fmt.Printf("ERROR|%v", err)
		c.AbortWithError(500, errors.New("an internal server error occurred"))
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"cart": cart,
	})
}

// @Summary update user's cart
// @ID update-users-cart
// @Produce json
// @Param data body putCartRequest true "put cart data"
// @Param Authorization header string false "Optional bearer JWT"
// @Success 200 {object} models.Cart
// @Router / [put]
func (handler *CartHandler) PutCart(c *gin.Context) {
	cart := c.MustGet("cart").(models.Cart)

	input, err := bindPutCartInput(c)
	if err != nil {
		fmt.Printf("ERROR|%v", err)
		c.AbortWithError(400, err)
		return
	}

	for _, item := range input.Items {
		cart, err = updateCartItems(cart, item.ProductID, item.Quantity, handler.catalogClient, c)
		if err != nil {
			fmt.Printf("ERROR|%v", err)
			c.AbortWithError(500, errors.New("an internal server error occurred"))
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

	if _, err := handler.cartRepository.SaveCart(cart, c); err != nil {
		fmt.Printf("ERROR|%v", err)
		c.AbortWithError(500, errors.New("an internal server error occurred"))
		return
	}

	erro := fetchCartItemsDetails(cart, handler.catalogClient, c)
	if erro != nil {
		fmt.Printf("ERROR|%v", erro)
		c.AbortWithError(500, errors.New("an internal server error occurred"))
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
	Quantity  int    `json:"quantity" binding:"required"`
}

func bindPutCartInput(c *gin.Context) (putCartRequest, error) {
	var input putCartRequest
	if err := c.ShouldBindJSON(&input); err != nil {
		return input, err
	}
	return input, nil
}

func updateCartItems(cart models.Cart, productID string, quantity int, catalogClient *clients.CatalogClient, ctx context.Context) (models.Cart, error) {
	price, err := catalogClient.GetProductPrice(productID, ctx)
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

func fetchCartItemsDetails(cart models.Cart, catalogClient *clients.CatalogClient, ctx context.Context) error {
	for i := range cart.CartItems {
		item := cart.CartItems[i]
		details, err := catalogClient.GetProduct(item.ProductID, ctx)
		if err != nil {
			return err
		}

		cart.CartItems[i].Details = details
	}

	return nil
}
