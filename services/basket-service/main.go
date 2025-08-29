package main

import (
	"context"
	"log"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/app"
	_ "github.com/kamilszymanski707/online-sushi-shop/basket-service/docs"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/handlers"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/infra"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/middlewares"
	"github.com/redis/go-redis/v9"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

// @title Online Sushi Shop Basket Service API
// @version 1.0
// @description API for managing baskets in the online sushi shop.
// @termsOfService http://swagger.io/terms/
// @contact.name API Support
// @contact.email support@sushi-shop.com
// @license.name MIT
// @license.url https://opensource.org/licenses/MIT
// @host localhost:8080
// @BasePath /api/v1
func main() {
	db := redis.NewClient(&redis.Options{
		Addr:     "localhost:6379",
		Password: "",
		DB:       0,
	})

	if err := db.Ping(context.Background()).Err(); err != nil {
		log.Fatalf("failed to connect to Redis: %v", err)
	}

	conn, err := grpc.NewClient("localhost:4770", grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("failed to connect to catalog service: %v", err)
	}

	defer db.Close()
	defer conn.Close()

	csc := catalogpb.NewCatalogServiceClient(conn)

	br := infra.NewBasketRepository(db)
	pr := infra.NewProductRepository(db, csc)

	bs := app.NewBasketService(br, pr)

	h := handlers.NewBasketHandler(bs)

	mw := middlewares.NewCartMiddleware(br)

	r := gin.Default()
	r.Use(mw.CartHandlerFunc())
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	apiV1 := r.Group("/api/v1")
	{
		basket := apiV1.Group("/basket")
		{
			basket.GET("/", h.GetBasket)
			basket.POST("/items", h.AddItem)
			basket.DELETE("/items/:productID", h.RemoveItem)
			basket.PUT("/items/:productID", h.ChangeQuantity)
			basket.DELETE("/clear", h.Clear)
		}
	}

	if err := r.Run(":8080"); err != nil {
		log.Fatalf("failed to run server: %v", err)
	}
}
