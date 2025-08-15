package handlers

import (
	"context"
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/shopspring/decimal"
)

type Handler struct {
	r *repositories.Repository
}

func NewHandler(r *repositories.Repository) *Handler {
	return &Handler{r: r}
}

func (h *Handler) GetCart(c *gin.Context) {
	c.JSON(http.StatusOK, gin.H{
		"cart": c.MustGet("cart").(models.Cart),
	})
}

func (h *Handler) PatchCart(c *gin.Context) {
	cart := c.MustGet("cart").(models.Cart)

	input, err := bindPatchCartInput(c)
	if err != nil {
		return
	}

	cart = updateCartItems(cart, input.ProductID, input.Quantity)
	cart.TotalPrice = calculateTotal(cart.CartItems)

	if _, err := h.r.SaveCart(context.Background(), cart); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, gin.H{"cart": cart})
}

type patchCartInput struct {
	ProductID string `json:"product_id" binding:"required"`
	Quantity  int32  `json:"quantity" binding:"required"`
}

func bindPatchCartInput(c *gin.Context) (patchCartInput, error) {
	var input patchCartInput
	if err := c.ShouldBindJSON(&input); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return input, err
	}
	return input, nil
}

func updateCartItems(cart models.Cart, productID string, quantity int32) models.Cart {
	price := resolveProductPrice(productID)
	for i, item := range cart.CartItems {
		if item.ProductID == productID {
			cart.CartItems[i].Quantity = quantity
			cart.CartItems[i].UnitPrice = price
			return cart
		}
	}
	cart.CartItems = append(cart.CartItems, models.CartItem{
		ID:        uuid.New().String(),
		ProductID: productID,
		Quantity:  quantity,
		UnitPrice: price,
	})
	return cart
}

func calculateTotal(items []models.CartItem) decimal.Decimal {
	total := decimal.NewFromInt(0)
	for _, item := range items {
		total = total.Add(item.UnitPrice.Mul(decimal.NewFromInt(int64(item.Quantity))))
	}
	return total
}

func resolveProductPrice(productID string) decimal.Decimal {
	panic("not implemented yet")
}
