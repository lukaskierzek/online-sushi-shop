package handlers

import (
	"context"
	"errors"
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/clients"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/shopspring/decimal"
)

type CartHandler struct {
	cr *repositories.CartRepository
	cc *clients.CatalogClient
}

func NewCartHandler(cr *repositories.CartRepository, cc *clients.CatalogClient) *CartHandler {
	return &CartHandler{cr: cr, cc: cc}
}

// @Summary get user's cart
// @ID get-users-cart
// @Produce json
// @Success 200 {object} models.Cart
// @Param Authorization header string false "Optional bearer JWT"
// @Router / [get]
func (h *CartHandler) GetCart(c *gin.Context) {
	cart := *c.MustGet("cart").(*models.Cart)
	err := h.fetchCartItemsDetails(cart, c)
	if err != nil {
		log.Println("ERROR|", err.Error())
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
func (h *CartHandler) PutCart(c *gin.Context) {
	cart := *c.MustGet("cart").(*models.Cart)

	input, err := h.bindPutCartInput(c)
	if err != nil {
		log.Println("ERROR|", err.Error())
		c.AbortWithError(400, err)
		return
	}

	for _, item := range input.Items {
		cart, err = h.updateCartItems(cart, item.ProductID, item.Quantity, c)
		if err != nil {
			log.Println("ERROR|", err.Error())
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

	cart.TotalPrice = h.calculateTotal(cart.CartItems)

	if _, err := h.cr.SaveCart(cart, c); err != nil {
		log.Println("ERROR|", err.Error())
		c.AbortWithError(500, errors.New("an internal server error occurred"))
		return
	}

	erro := h.fetchCartItemsDetails(cart, c)
	if erro != nil {
		log.Println("ERROR|", err.Error())
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

func (h *CartHandler) bindPutCartInput(c *gin.Context) (putCartRequest, error) {
	var i putCartRequest
	if err := c.ShouldBindJSON(&i); err != nil {
		return i, err
	}
	return i, nil
}

func (h *CartHandler) updateCartItems(cart models.Cart, id string, quantity int, ctx context.Context) (models.Cart, error) {
	price, err := h.cc.GetProductPrice(id, ctx)
	if err != nil {
		return models.Cart{}, err
	}

	if price == nil {
		return models.Cart{}, errors.New("product price not found")
	}

	for i, item := range cart.CartItems {
		if item.ProductID == id {
			cart.CartItems[i].Quantity = quantity
			cart.CartItems[i].UnitPrice = *price
			return cart, nil
		}
	}
	cart.CartItems = append(cart.CartItems, models.CartItem{
		ID:        uuid.New().String(),
		ProductID: id,
		Quantity:  quantity,
		UnitPrice: *price,
	})
	return cart, nil
}

func (h *CartHandler) calculateTotal(items []models.CartItem) decimal.Decimal {
	total := decimal.NewFromInt(0)
	for _, item := range items {
		total = total.Add(item.UnitPrice.Mul(decimal.NewFromInt(int64(item.Quantity))))
	}
	return total
}

func (h *CartHandler) fetchCartItemsDetails(cart models.Cart, ctx context.Context) error {
	for i := range cart.CartItems {
		item := cart.CartItems[i]
		details, err := h.cc.GetProduct(item.ProductID, ctx)
		if err != nil {
			return err
		}

		cart.CartItems[i].Details = *details
	}

	return nil
}
