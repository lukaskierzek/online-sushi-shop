package handlers

import (
	"net/http"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/app"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
)

type BasketHandler struct {
	service *app.BasketService
}

func NewBasketHandler(service *app.BasketService) *BasketHandler {
	return &BasketHandler{service: service}
}

func (h *BasketHandler) GetBasket(c *gin.Context) {
	basket := c.MustGet("cart").(domain.Basket)
	c.JSON(http.StatusOK, basket)
}

func (h *BasketHandler) AddItem(c *gin.Context) {
	b := c.MustGet("cart").(domain.Basket)

	var req struct {
		ProductID string `json:"product_id"`
		Quantity  int32  `json:"quantity"`
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	item := domain.BasketItem{
		ProductID: req.ProductID,
		Quantity:  req.Quantity,
	}

	basket, err := h.service.AddItem(c, b, item)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, basket)
}

func (h *BasketHandler) RemoveItem(c *gin.Context) {
	b := c.MustGet("cart").(domain.Basket)
	productID := c.Param("productID")

	basket, err := h.service.RemoveItem(c, b, productID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, basket)
}

func (h *BasketHandler) ChangeQuantity(c *gin.Context) {
	b := c.MustGet("cart").(domain.Basket)
	productID := c.Param("productID")
	if productID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "No product ID in the request"})
		return
	}

	var req struct {
		Quantity int32 `json:"quantity"`
	}

	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": err.Error()})
		return
	}

	basket, err := h.service.ChangeQuantity(c, b, productID, req.Quantity)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, basket)
}

func (h *BasketHandler) Clear(c *gin.Context) {
	b := c.MustGet("cart").(domain.Basket)

	basket, err := h.service.Clear(c, b)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, basket)
}
