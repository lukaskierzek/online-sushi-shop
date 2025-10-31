package main

import (
	"context"
	"log/slog"
	"os"

	"github.com/confluentinc/confluent-kafka-go/kafka"
	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/app"
	_ "github.com/kamilszymanski707/online-sushi-shop/basket-service/docs"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/handlers"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/infra"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/middlewares"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/props"
	"github.com/redis/go-redis/v9"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"

	catalog_v1 "github.com/kamilszymanski707/proto-lib/catalog.v1"
)

// @title Online Sushi Shop Basket Service API
// @version 1.0
// @description API for managing baskets in the online sushi shop.
// @termsOfService http://swagger.io/terms/
// @contact.name API Support
// @contact.email support@sushi-shop.com
// @license.name MIT
// @license.url https://opensource.org/licenses/MIT
// @BasePath /api/v1
func main() {
	createLogger()

	prps := createProps()

	db := createDB(prps)
	defer db.Close()

	conn := createGRPCConn(prps)
	defer conn.Close()

	cons := createKafkaConsumer(prps)
	defer cons.Close()

	csc := catalog_v1.NewCatalogServiceClient(conn)

	br := infra.NewBasketRepository(db, prps.CookieCartIDTTL)
	pr := infra.NewProductRepository(db, csc)

	bs := app.NewBasketService(br, pr)
	go startKafkaConsumer(cons, bs, prps)

	h := handlers.NewBasketHandler(bs)

	mw := middlewares.NewCartMiddleware(br, prps.CookieCartIDTTL)

	r := createRouter(h, mw)

	if err := r.Run(prps.AppPort); err != nil {
		slog.Error("failed to run server", slog.String("error", err.Error()))
		os.Exit(1)
	}
}

func createLogger() {
	handler := slog.NewTextHandler(os.Stdout, &slog.HandlerOptions{
		Level: slog.LevelDebug,
	})

	logger := slog.New(handler)
	slog.SetDefault(logger)
}

func createProps() *props.Props {
	prps, err := props.LoadProps(".")
	if err != nil {
		slog.Error("failed to load properties", slog.String("error", err.Error()))
		os.Exit(1)
	}
	return prps
}

func createDB(prps *props.Props) *redis.Client {
	db := redis.NewClient(&redis.Options{
		Addr:     prps.RedisAddress,
		Password: prps.RedisPassword,
		DB:       prps.RedisDB,
	})

	if err := db.Ping(context.Background()).Err(); err != nil {
		slog.Error("failed to connect to Redis", slog.String("error", err.Error()))
		os.Exit(1)
	}

	return db
}

func createGRPCConn(prps *props.Props) *grpc.ClientConn {
	conn, err := grpc.NewClient(prps.GrpcCatalogTarget, grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		slog.Error("failed to connect to catalog service", slog.String("error", err.Error()))
		os.Exit(1)
	}
	return conn
}

func createKafkaConsumer(prps *props.Props) *kafka.Consumer {
	consumer, err := kafka.NewConsumer(&kafka.ConfigMap{
		"bootstrap.servers":               prps.KafkaBootstrapServers,
		"group.id":                        prps.KafkaConsumerGroupID,
		"go.application.rebalance.enable": true,
		"auto.offset.reset":               "earliest"})

	if err != nil {
		slog.Error("failed to create kafka consumer", slog.String("error", err.Error()))
		os.Exit(1)
	}

	return consumer
}

func createRouter(h *handlers.BasketHandler, mw *middlewares.CartMiddleware) *gin.Engine {
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

	return r
}

func startKafkaConsumer(consumer *kafka.Consumer, bs *app.BasketService, prps *props.Props) {
	ctx := context.Background()

	if err := consumer.SubscribeTopics([]string{prps.KafkaOrderTopic}, nil); err != nil {
		slog.Error("failed to subscribe to kafka topic", slog.String("error", err.Error()))
		os.Exit(1)
	}

	slog.Info("Kafka consumer started", slog.String("topic", prps.KafkaOrderTopic))

	go func() {
		defer consumer.Close()

		for {
			msg, err := consumer.ReadMessage(-1)
			if err != nil {
				slog.Error("Kafka read error", slog.String("error", err.Error()))
				continue
			}

			userID := string(msg.Value)
			if userID == "" {
				slog.Warn("Received Kafka message with empty userID")
				continue
			}

			slog.Info("Received Kafka message",
				slog.String("topic", *msg.TopicPartition.Topic),
				slog.String("userID", userID))

			if err := bs.HandleOrderEvent(ctx, userID); err != nil {
				slog.Error("failed to handle order event",
					slog.String("userID", userID),
					slog.String("error", err.Error()))
			}
		}
	}()
}
