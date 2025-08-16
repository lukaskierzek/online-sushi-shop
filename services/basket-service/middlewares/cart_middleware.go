package middlewares

import (
	"context"
	"errors"
	"fmt"
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

func NewCartMiddleware(r *repositories.CartRepository, applicationProperties utils.ApplicationProperties) gin.HandlerFunc {
	return func(c *gin.Context) {
		ctx := c.Request.Context()

		cartID, err := ensureCartIDCookie(c, r, ctx, applicationProperties.CartIDCookieTtlMs)
		if err != nil {
			fmt.Printf("ERROR|%v", err)
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		userID, err := extractUserID(c, applicationProperties.JwtSecret)
		if err != nil {
			fmt.Printf("ERROR|%v", err)
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		cart, err := loadOrMergeCart(ctx, r, cartID, userID)
		if err != nil && err != redis.Nil {
			fmt.Printf("ERROR|%v", err)
			c.AbortWithError(500, errors.New("an internal server error occurred"))
			return
		}

		updateCartIDCookieIfNeeded(c, cart, userID, applicationProperties.CartIDCookieTtlMs)

		c.Set("cart", cart)

		c.Next()
	}
}

func ensureCartIDCookie(c *gin.Context, r *repositories.CartRepository, ctx context.Context, cartIDCookieTtl int) (string, error) {
	cartID, err := c.Cookie("cart_id")
	if err != nil || cartID == "" {
		newCart, err := createNewCart(r, ctx)
		if err != nil {
			return "", err
		}
		cartID = newCart.ID
		c.SetCookie("cart_id", cartID, cartIDCookieTtl, "/", "localhost", false, true)
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

func loadOrMergeCart(ctx context.Context, r *repositories.CartRepository, cartID, userID string) (models.Cart, error) {
	return r.GetCart(ctx, repositories.GetCartQuery{
		ID:      cartID,
		OwnerID: userID,
	})
}

func updateCartIDCookieIfNeeded(c *gin.Context, cart models.Cart, userID string, cartIDCookieTtl int) {
	if userID != "" && cart.OwnerID == userID && cart.ID != userID {
		cart.ID = userID
		c.SetCookie("cart_id", userID, cartIDCookieTtl, "/", "localhost", false, true)
	}
}

func createNewCart(r *repositories.CartRepository, ctx context.Context) (models.Cart, error) {
	newCart := models.Cart{
		ID:         uuid.New().String(),
		OwnerID:    "",
		CartItems:  []models.CartItem{},
		TotalPrice: decimal.NewFromInt(0),
	}
	return r.SaveCart(ctx, newCart)
}
