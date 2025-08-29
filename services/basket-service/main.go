package main

import (
	"log"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/app"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/handlers"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/infra"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/middlewares"
	"github.com/redis/go-redis/v9"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

func main() {
	db := redis.NewClient(&redis.Options{
		Addr:     "localhost:6379",
		Password: "",
		DB:       0,
	})

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
