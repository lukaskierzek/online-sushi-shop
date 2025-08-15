package middlewares

import (
	"context"
	"fmt"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
)

func CartMiddleware(r *repositories.Repository, jwtSecret string) gin.HandlerFunc {
	return func(c *gin.Context) {
		ctx := c.Request.Context()

		cartID, err := ensureCartIDCookie(c, r, ctx)
		if err != nil {
			c.AbortWithError(500, err)
			return
		}

		userID, err := extractUserID(c, jwtSecret)
		if err != nil {
			c.AbortWithError(500, err)
			return
		}

		cart, err := loadOrMergeCart(ctx, r, cartID, userID)
		if err != nil && err != redis.Nil {
			c.AbortWithError(500, err)
			return
		}

		updateCartIDCookieIfNeeded(c, cart, userID)

		c.Set("cart", cart)

		c.Next()
	}
}

func ensureCartIDCookie(c *gin.Context, r *repositories.Repository, ctx context.Context) (string, error) {
	cartID, err := c.Cookie("cart_id")
	if err != nil || cartID == "" {
		newCart, err := createNewCart(r, ctx)
		if err != nil {
			return "", err
		}
		cartID = newCart.ID
		c.SetCookie("cart_id", cartID, 3600*24*7, "/", "localhost", false, true)
	}
	return cartID, nil
}

func extractUserID(c *gin.Context, jwtSecret string) (string, error) {
	auth := c.GetHeader("Authorization")
	if after, ok := strings.CutPrefix(auth, "Bearer "); ok {
		tokenString := after
		token, err := jwt.Parse(tokenString, func(t *jwt.Token) (any, error) {
			if _, ok := t.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, fmt.Errorf("unexpected signing method")
			}
			return []byte(jwtSecret), nil
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

func loadOrMergeCart(ctx context.Context, r *repositories.Repository, cartID, userID string) (models.Cart, error) {
	return r.GetCart(ctx, repositories.GetCartQuery{
		ID:      cartID,
		OwnerID: userID,
	})
}

func updateCartIDCookieIfNeeded(c *gin.Context, cart models.Cart, userID string) {
	if userID != "" && cart.OwnerID == userID && cart.ID != userID {
		cart.ID = userID
		c.SetCookie("cart_id", userID, 3600*24*7, "/", "localhost", false, true)
	}
}

func createNewCart(r *repositories.Repository, ctx context.Context) (models.Cart, error) {
	newCart := models.Cart{
		ID:         uuid.New().String(),
		OwnerID:    "",
		CartItems:  []models.CartItem{},
		TotalPrice: decimal.NewFromInt(0),
	}
	return r.SaveCart(ctx, newCart)
}
