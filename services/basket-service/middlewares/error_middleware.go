package middlewares

import (
	"net/http"
	"slices"

	"github.com/gin-gonic/gin"
)

func NewErrorMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		c.Next()

		if len(c.Errors) > 0 {
			var errors []string

			for _, err := range c.Errors {
				message := err.Error()
				if !slices.Contains(errors, message) {
					errors = append(errors, message)
				}
			}

			c.JSON(http.StatusInternalServerError, map[string]any{
				"success": false,
				"errors":  errors,
			})
		}
	}
}
