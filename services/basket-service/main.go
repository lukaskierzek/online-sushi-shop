package main

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/db"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/handlers"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/middlewares"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
)

var (
	_         = utils.LoadLocalEnv()
	jwtSecret = utils.GetEnv("JWT_SECRET")
	dbURL     = utils.GetEnv("DB_URL")
	dbIndex   = utils.GetEnv("DB_INDEX")
)

func main() {
	idx, err := strconv.Atoi(dbIndex)
	if err != nil {
		panic(err)
	}
	rdb := db.NewDB(dbURL, idx)

	r := repositories.NewRepository(rdb)

	h := handlers.NewHandler(r)

	router := gin.Default()

	router.Use(middlewares.CartMiddleware(r, jwtSecret))

	{
		v1 := router.Group("/v1")
		cartGroupV1 := v1.Group("/cart")
		cartGroupV1.GET("/", h.GetCart)
		cartGroupV1.PATCH("/", h.PatchCart)
	}

	router.Run(":8080")
}
