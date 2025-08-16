package utils

import (
	"log"
	"os"
	"strconv"
	"time"

	"github.com/joho/godotenv"
)

func ResolveApplicationProperties(basePath string) ApplicationProperties {
	loadLocalEnv(basePath)

	dbi, err := strconv.Atoi(getEnv("DB_INDEX"))
	if err != nil {
		log.Fatal(err)
	}

	cictm, err := strconv.Atoi(getEnv("CART_ID_COOKIE_TTL"))
	if err != nil {
		log.Fatal(err)
	}

	return ApplicationProperties{
		ServerPort:        getEnv("SERVER_PORT"),
		JwtSecret:         getEnv("JWT_SECRET"),
		DBUrl:             getEnv("DB_URL"),
		DBIndex:           dbi,
		CatalogGrpcTarget: getEnv("CATALOG_GRPC_TARGET"),
		CartIDCookieTtl:   time.Duration(cictm) * time.Second,
	}
}

func loadLocalEnv(basePath string) interface{} {
	environment := os.Getenv("APP_ENV")
	if environment == "" {
		environment = "local"
	}

	if _, runningInContainer := os.LookupEnv("CONTAINER"); !runningInContainer {
		err := godotenv.Load(basePath + "/env/.env." + environment)
		if err != nil {
			log.Fatal(err)
		}
	}
	return nil
}

func getEnv(key string) string {
	value, ok := os.LookupEnv(key)
	if !ok {
		log.Fatal("Environment variable not found: ", key)
	}
	return value
}

type ApplicationProperties struct {
	ServerPort        string
	JwtSecret         string
	DBUrl             string
	DBIndex           int
	CatalogGrpcTarget string
	CartIDCookieTtl   time.Duration
}
