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

// addItemRequest represents the request to add an item to the basket.
// swagger:model addItemRequest
type addItemRequest struct {
	ProductID string `json:"product_id"`
	Quantity  int32  `json:"quantity"`
}

// changeQuantityRequest represents the request to change the quantity of an item in the basket.
// swagger:model changeQuantityRequest
type changeQuantityRequest struct {
	Quantity int32 `json:"quantity"`
}

func NewBasketHandler(service *app.BasketService) *BasketHandler {
	return &BasketHandler{service: service}
}

// @Summary Get basket
// @Description Get the current basket
// @Tags basket
// @Produce json
// @Success 200 {object} domain.Basket
// @Failure 400 {object} map[string]string
// @Router /basket/ [get]
func (h *BasketHandler) GetBasket(c *gin.Context) {
	b := h.resolveBasket(c)
	if b == nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Basket not found in context"})
		return
	}

	c.JSON(http.StatusOK, b)
}

// @Summary Add item to basket
// @Description Add a product to the user's basket
// @Tags basket
// @Accept json
// @Produce json
// @Param addItemRequest body addItemRequest true "Add Item"
// @Success 200 {object} domain.Basket
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /basket/items [post]
func (h *BasketHandler) AddItem(c *gin.Context) {
	b := h.resolveBasket(c)
	if b == nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Basket not found in context"})
		return
	}

	var req addItemRequest

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

// @Summary Remove item from basket
// @Description Remove a product from the user's basket
// @Tags basket
// @Produce json
// @Param productID path string true "Product ID"
// @Success 200 {object} domain.Basket
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /basket/items/{productID} [delete]
func (h *BasketHandler) RemoveItem(c *gin.Context) {
	b := h.resolveBasket(c)
	if b == nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Basket not found in context"})
		return
	}

	productID := c.Param("productID")

	basket, err := h.service.RemoveItem(c, b, productID)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, basket)
}

// @Summary Change item quantity
// @Description Change the quantity of a product in the user's basket
// @Tags basket
// @Accept json
// @Produce json
// @Param productID path string true "Product ID"
// @Param changeQuantityRequest body changeQuantityRequest true "Change quantity request"
// @Success 200 {object} domain.Basket
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /basket/items/{productID} [put]
func (h *BasketHandler) ChangeQuantity(c *gin.Context) {
	b := h.resolveBasket(c)
	if b == nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Basket not found in context"})
		return
	}

	productID := c.Param("productID")
	if productID == "" {
		c.JSON(http.StatusBadRequest, gin.H{"error": "No product ID in the request"})
		return
	}

	var req changeQuantityRequest

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

// @Summary Clear basket
// @Description Remove all items from the user's basket
// @Tags basket
// @Produce json
// @Success 200 {object} domain.Basket
// @Failure 400 {object} map[string]string
// @Failure 500 {object} map[string]string
// @Router /basket/clear [delete]
func (h *BasketHandler) Clear(c *gin.Context) {
	b := h.resolveBasket(c)
	if b == nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "Basket not found in context"})
		return
	}

	basket, err := h.service.Clear(c, b)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"error": err.Error()})
		return
	}

	c.JSON(http.StatusOK, basket)
}

func (h *BasketHandler) resolveBasket(c *gin.Context) *domain.Basket {
	basketVal, exists := c.Get("cart")
	if !exists || basketVal == nil {
		return nil
	}

	basket, ok := basketVal.(*domain.Basket)
	if ok {
		return basket
	}

	return nil
}
