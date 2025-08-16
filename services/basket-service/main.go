package main

import (
	"log"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/handlers"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/middlewares"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"

	_ "github.com/kamilszymanski707/online-sushi-shop/basket-service/docs"
	swaggerFiles "github.com/swaggo/files"
	ginSwagger "github.com/swaggo/gin-swagger"
)

var (
	applicationProperties = utils.ResolveApplicationProperties(".")
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
	catalogClient, conn := createCatalogGrpcClient()

	redisClient := createRedisClient()
	router := createRouter(redisClient, catalogClient)

	defer redisClient.Close()
	defer conn.Close()

	router.Run(":" + applicationProperties.ServerPort)
}

func createRedisClient() *redis.Client {
	return redis.NewClient(&redis.Options{
		Addr: applicationProperties.DBUrl,
		DB:   applicationProperties.DBIndex,
	})
}

func createRouter(redisClient *redis.Client, catalogClient catalogpb.CatalogServiceClient) *gin.Engine {
	r := repositories.NewCartRepository(redisClient, applicationProperties)

	h := handlers.NewCartHandler(r, catalogClient)

	router := gin.New()

	router.Use(middlewares.NewErrorMiddleware())

	if os.Getenv("APP_ENV") != "prod" {
		cartV1 := router.Group("/api/v1/cart")
		cartV1.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))
	}

	cartV1Protected := router.Group("/api/v1/cart")

	cartV1Protected.Use(middlewares.NewCartMiddleware(r, applicationProperties))

	cartV1Protected.GET("/", h.GetCart)
	cartV1Protected.PUT("/", h.PutCart)

	return router
}

func createCatalogGrpcClient() (catalogpb.CatalogServiceClient, *grpc.ClientConn) {
	conn, err := grpc.NewClient(
		applicationProperties.CatalogGrpcTarget,
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)

	if err != nil {
		log.Fatal(err)
	}

	result := catalogpb.NewCatalogServiceClient(conn)
	return result, conn
}
