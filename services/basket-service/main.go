package main

import (
	"strconv"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/catalogpb"
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
	_                 = utils.LoadLocalEnv()
	serverPort        = utils.GetEnv("SERVER_PORT")
	jwtSecret         = utils.GetEnv("JWT_SECRET")
	dbURL             = utils.GetEnv("DB_URL")
	dbIndex           = utils.GetEnv("DB_INDEX")
	catalogGrpcTarget = utils.GetEnv("CATALOG_GRPC_TARGET")
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

	router.Run(":" + serverPort)
}

func createRedisClient() *redis.Client {
	idx, err := strconv.Atoi(dbIndex)
	if err != nil {
		panic(err)
	}

	return redis.NewClient(&redis.Options{
		Addr: dbURL,
		DB:   idx,
	})
}

func createRouter(redisClient *redis.Client, catalogClient catalogpb.CatalogServiceClient) *gin.Engine {
	r := repositories.NewCartRepository(redisClient)

	h := handlers.NewCartHandler(r, catalogClient)

	router := gin.New()

	cartV1 := router.Group("/api/v1/cart")
	cartV1.GET("/docs/*any", ginSwagger.WrapHandler(swaggerFiles.Handler))

	cartV1Protected := router.Group("/api/v1/cart")
	cartV1Protected.Use(middlewares.NewCartMiddleware(r, jwtSecret))

	cartV1Protected.GET("/", h.GetCart)
	cartV1Protected.PUT("/", h.PutCart)

	return router
}

func createCatalogGrpcClient() (catalogpb.CatalogServiceClient, *grpc.ClientConn) {
	conn, err := grpc.NewClient(
		catalogGrpcTarget,
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		panic(err)
	}

	result := catalogpb.NewCatalogServiceClient(conn)
	return result, conn
}
