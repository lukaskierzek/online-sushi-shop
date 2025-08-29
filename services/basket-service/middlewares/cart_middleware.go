package middlewares

import (
	"errors"
	"log/slog"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
)

type Middleware struct {
	r      *repositories.CartRepository
	p      *utils.ApplicationProperties
	logger *slog.Logger
	gin.HandlerFunc
}

func NewCartMiddleware(r *repositories.CartRepository, p *utils.ApplicationProperties, logger *slog.Logger) *Middleware {
	return &Middleware{
		r:      r,
		p:      p,
		logger: logger,
	}
}

func (m *Middleware) CartHandlerFunc() gin.HandlerFunc {
	return func(c *gin.Context) {
		host := c.Request.Host
		if strings.Contains(host, ":") {
			host = strings.Split(host, ":")[0]
		}
		if host == "" {
			m.logger.Error("IP not found in the request")
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		cartID, err := m.ensureCartIDCookie(host, c)
		if err != nil {
			m.logger.Error(err.Error())
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		userID, err := m.extractUserID(c)
		if err != nil {
			m.logger.Error(err.Error())
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		cart, err := m.r.GetCart(repositories.GetCartQuery{
			ID:      cartID,
			OwnerID: userID,
		}, c)

		if err != nil && err != redis.Nil {
			m.logger.Error(err.Error())
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		if cart.ID == "" {
			m.logger.Error("Cookie not found")
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		c.Set("cart", cart)

		c.Next()
	}
}

func (m *Middleware) ensureCartIDCookie(host string, c *gin.Context) (string, error) {
	cartID, err := c.Cookie("cart_id")
	if err != nil || cartID == "" {
		newCart, err := m.r.CreateEmptyCart(c)
		if err != nil {
			return "", err
		}
		cartID = newCart.ID
		c.SetCookie("cart_id", cartID, int(m.p.CartIDCookieTtl.Seconds()), "/", host, false, true)
	}
	return cartID, nil
}

func (m *Middleware) extractUserID(c *gin.Context) (string, error) {
	auth := c.GetHeader("Authorization")
	if after, ok := strings.CutPrefix(auth, "Bearer "); ok {
		tokenString := after
		token, err := jwt.Parse(tokenString, func(t *jwt.Token) (any, error) {
			if _, ok := t.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, errors.New("unexpected signing method")
			}
			return []byte(m.p.JwtSecret), nil
		})
		if err == nil && token.Valid {
			if claims, ok := token.Claims.(jwt.MapClaims); ok {
				if sub, ok := claims["sub"].(string); ok {
					return sub, nil
				}
			}
		}
	}
	return "", nil
}
