package middlewares

import (
	"context"
	"errors"
	"log"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
)

const cart_id = "cart_id"

type Middleware struct {
	r *repositories.CartRepository
	p *utils.ApplicationProperties
	gin.HandlerFunc
}

func NewCartMiddleware(r *repositories.CartRepository, p *utils.ApplicationProperties) *Middleware {
	return &Middleware{
		r: r,
		p: p,
	}
}

func (m *Middleware) CartHandlerFunc() gin.HandlerFunc {
	return func(c *gin.Context) {
		host := c.Request.Host
		if strings.Contains(host, ":") {
			host = strings.Split(host, ":")[0]
		}
		if host == "" {
			log.Println("ERROR|", "IP not found in the request")
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		cartID, err := m.ensureCartIDCookie(host, c)
		if err != nil {
			log.Println("ERROR|", err.Error())
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		userID, err := m.extractUserID(c)
		if err != nil {
			log.Println("ERROR|", err.Error())
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		cart, err := m.loadOrMergeCart(cartID, userID, c)
		if err != nil && err != redis.Nil {
			log.Println("ERROR|", err.Error())
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		if cart.ID == "" {
			log.Println("ERROR|", "Cookie not found")
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		m.updateCartIDCookieIfNeeded(cart, userID, host, c)

		c.Set("cart", cart)

		c.Next()
	}
}

func (m *Middleware) ensureCartIDCookie(host string, c *gin.Context) (string, error) {
	cartID, err := c.Cookie(cart_id)
	if err != nil || cartID == "" {
		newCart, err := m.createNewCart(c)
		if err != nil {
			return "", err
		}
		cartID = newCart.ID
		c.SetCookie(cart_id, cartID, int(m.p.CartIDCookieTtl.Seconds()), "/", host, false, true)
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

func (m *Middleware) loadOrMergeCart(cartID, userID string, ctx context.Context) (*models.Cart, error) {
	return m.r.GetCart(repositories.GetCartQuery{
		ID:      cartID,
		OwnerID: userID,
	}, ctx)
}

func (m *Middleware) updateCartIDCookieIfNeeded(cart *models.Cart, userID string, host string, c *gin.Context) {
	if userID != "" && cart.OwnerID == userID && cart.ID != userID {
		cart.ID = userID
		c.SetCookie(cart_id, userID, int(m.p.CartIDCookieTtl.Seconds()), "/", host, false, true)
	}
}

func (m *Middleware) createNewCart(ctx context.Context) (*models.Cart, error) {
	newCart := models.Cart{
		ID:         uuid.New().String(),
		OwnerID:    "",
		CartItems:  []models.CartItem{},
		TotalPrice: decimal.NewFromInt(0),
	}
	return m.r.SaveCart(newCart, ctx)
}
