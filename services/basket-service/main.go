package main

import (
	"log"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/clients"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/db"
	_ "github.com/kamilszymanski707/online-sushi-shop/basket-service/docs"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/handlers"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/middlewares"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"

	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

var (
	p = utils.ResolveApplicationProperties(".")
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

// @BasePath /api/v1/cart
func main() {
	csc, conn, err := clients.NewCatalogServiceClient(p)
	if err != nil {
		log.Fatal(err)
	}

	rdb := db.NewRedisClient(p)

	cr := repositories.NewCatalogRepository(rdb, p)

	cc, err := clients.NewCatalogClient(csc, conn, cr)
	if err != nil {
		log.Fatal(err)
	}

	cartr := repositories.NewCartRepository(rdb, p)

	router := createRouter(cartr, cc, gin.New())

	defer cc.Close()
	defer rdb.Close()

	router.Run(":" + p.ServerPort)
}

func createRouter(cr *repositories.CartRepository, cc *clients.CatalogClient, router *gin.Engine) *gin.Engine {
	h := handlers.NewCartHandler(cr, cc)

	router.Use(middlewares.NewErrorMiddleware())

	if os.Getenv("APP_ENV") != "prod" {
		cartV1 := router.Group("/api/v1/cart")
		cartV1.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	}

	cartV1Protected := router.Group("/api/v1/cart")

	m := middlewares.NewCartMiddleware(cr, p)
	cartV1Protected.Use(m.CartHandlerFunc())

	cartV1Protected.GET("/", h.GetCart)
	cartV1Protected.PUT("/", h.PutCart)

	return router
}
