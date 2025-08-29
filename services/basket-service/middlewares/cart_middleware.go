package middlewares

import (
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/infra"
	"github.com/redis/go-redis/v9"
)

type CartMiddleware struct {
	br infra.BasketRepository
}

func NewCartMiddleware(br infra.BasketRepository) *CartMiddleware {
	return &CartMiddleware{br: br}
}

func (m *CartMiddleware) CartHandlerFunc() gin.HandlerFunc {
	return func(c *gin.Context) {
		host := c.Request.Host
		if strings.Contains(host, ":") {
			host = strings.Split(host, ":")[0]
		}

		cartID, err := m.ensureCartIDCookie(host, c)
		if err != nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Could not create or retrieve cart ID"})
			c.Abort()
			return
		}

		cart, err := m.br.GetBasketByID(c, cartID)
		if err != nil && err != redis.Nil {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "Could not retrieve basket"})
			c.Abort()
			return
		}

		if cart == nil || cart.ID == "" {
			newCart, err := m.br.CreateEmptyBasket(c)
			if err != nil {
				c.JSON(http.StatusInternalServerError, gin.H{"error": "Could not create new basket"})
				c.Abort()
				return
			}
			cart = newCart
		}

		c.Set("cart", cart)
		c.Next()
	}
}

func (m *CartMiddleware) ensureCartIDCookie(host string, c *gin.Context) (string, error) {
	cartID, err := c.Cookie("cart_id")
	if err != nil || cartID == "" {
		newCart, err := m.br.CreateEmptyBasket(c)
		if err != nil {
			return "", err
		}
		cartID = newCart.ID
		c.SetCookie("cart_id", cartID, 604800, "/", host, false, true)
	}
	return cartID, nil
}
