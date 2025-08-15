package main

import (
	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/pkg/db"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/pkg/handlers"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/pkg/middlewares"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/pkg/repositories"
)

func main() {
	rdb := db.NewDB()

	r := repositories.NewRepository(rdb)

	h := handlers.NewHandler(r)

	router := gin.Default()

	router.Use(middlewares.CartMiddleware(r, "jwt-secret")) //TODO: Extract from goENV

	router.GET("/cart", h.GetCart)
	router.PATCH("/cart", h.PatchCart)

	router.Run(":8080")
}
