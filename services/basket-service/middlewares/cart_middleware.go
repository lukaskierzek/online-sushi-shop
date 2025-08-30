package middlewares

import (
	"log/slog"
	"net/http"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/infra"
	"github.com/redis/go-redis/v9"
)

var (
	err_message = gin.H{"error": "An internal error occurred"}
)

type CartMiddleware struct {
	br        infra.BasketRepository
	cookieTTL int
}

func NewCartMiddleware(br infra.BasketRepository, cookieTTL int) *CartMiddleware {
	return &CartMiddleware{br: br, cookieTTL: cookieTTL}
}

func (m *CartMiddleware) CartHandlerFunc() gin.HandlerFunc {
	return func(c *gin.Context) {
		host := c.Request.Host
		if strings.Contains(host, ":") {
			host = strings.Split(host, ":")[0]
		}

		if host == "" {
			slog.Error("Could not retrieve host from request")
			c.JSON(http.StatusInternalServerError, err_message)
			c.Abort()
			return
		}

		cartID, err := m.ensureCartIDCookie(host, c)
		if err != nil {
			slog.Error("Could not ensure cart ID cookie", slog.String("error", err.Error()))
			c.JSON(http.StatusInternalServerError, err_message)
			c.Abort()
			return
		}

		cart, err := m.br.GetBasketByID(c, cartID)
		if err != nil && err != redis.Nil {
			slog.Error("Could not retrieve basket", slog.String("error", err.Error()))
			c.JSON(http.StatusInternalServerError, err_message)
			c.Abort()
			return
		}

		if cart == nil || cart.ID == "" {
			newCart, err := m.br.CreateEmptyBasket(c)
			if err != nil {
				slog.Error("Could not create new basket", slog.String("error", err.Error()))
				c.JSON(http.StatusInternalServerError, err_message)
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
		c.SetCookie("cart_id", cartID, m.cookieTTL, "/", host, false, true)
	}
	return cartID, nil
}
