package main

import (
	"log/slog"
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
	logger = slog.New(slog.NewTextHandler(os.Stdout, nil))
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
	p, err := utils.ResolveApplicationProperties(".")
	checkErr(err, "cannot resolve application properties")

	csc, conn, err := clients.NewCatalogServiceClient(p)
	checkErr(err, "cannot resolve CatalogServiceClient")

	rdb := db.NewRedisClient(p)

	cr := repositories.NewCatalogRepository(rdb, p)

	cc, err := clients.NewCatalogClient(csc, conn, cr)
	checkErr(err, "cannot resolve CatalogClient")

	cartr := repositories.NewCartRepository(rdb, p)

	router := createRouter(cartr, cc, p, gin.New())

	defer cc.Close()
	defer rdb.Close()

	if err := router.Run(":" + p.ServerPort); err != nil {
		logger.Error("failed to start server", "error", err)
		os.Exit(1)
	}
}

func createRouter(cr *repositories.CartRepository, cc *clients.CatalogClient, p *utils.ApplicationProperties, router *gin.Engine) *gin.Engine {
	h := handlers.NewCartHandler(cr, cc, logger)

	router.Use(middlewares.NewErrorMiddleware())

	if os.Getenv("APP_ENV") != "prod" {
		cartV1 := router.Group("/api/v1/cart")
		cartV1.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	}

	cartV1Protected := router.Group("/api/v1/cart")

	m := middlewares.NewCartMiddleware(cr, p, logger)
	cartV1Protected.Use(m.CartHandlerFunc())

	cartV1Protected.GET("/", h.GetCart)
	cartV1Protected.PUT("/", h.PutCart)

	return router
}

func checkErr(err error, msg string) {
	if err != nil {
		logger.Error(msg, "error", err)
		os.Exit(1)
	}
}
