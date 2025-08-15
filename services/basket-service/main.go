package main

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/db"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/handlers"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/middlewares"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"

	_ "github.com/kamilszymanski707/online-sushi-shop/basket-service/docs"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

var (
	_         = utils.LoadLocalEnv()
	jwtSecret = utils.GetEnv("JWT_SECRET")
	dbURL     = utils.GetEnv("DB_URL")
	dbIndex   = utils.GetEnv("DB_INDEX")
)

// @title Shopping Cart API
// @version 1.0
// @description Shopping Cart API.
// @termsOfService http://swagger.io/terms/

// @contact.name API Support
// @contact.url http://www.swagger.io/support
// @contact.email support@swagger.io

// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html

// @host localhost:8080
// @BasePath /api/v1/cart
func main() {
	idx, err := strconv.Atoi(dbIndex)
	if err != nil {
		panic(err)
	}

	rdb := db.NewDB(dbURL, idx)

	r := repositories.NewRepository(rdb)

	h := handlers.NewHandler(r)

	router := gin.New()

	cartV1 := router.Group("/api/v1/cart")
	cartV1.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	cartV1Protected := router.Group("/api/v1/cart")
	cartV1Protected.Use(middlewares.CartMiddleware(r, jwtSecret))

	cartV1Protected.GET("/", h.GetCart)
	cartV1Protected.PATCH("/", h.PatchCart)

	router.Run(":8080")
}
