package props

import (
	"fmt"
	"os"
	"strconv"

	"github.com/joho/godotenv"
)

type Props struct {
	AppPort           string
	CookieCartIDTTL   int // in seconds
	RedisAddress      string
	RedisPassword     string
	RedisDB           int
	GrpcCatalogTarget string
}

func LoadProps(basePath string) (*Props, error) {
	if err := loadLocalEnv(basePath); err != nil {
		return nil, fmt.Errorf("failed to load env file: %w", err)
	}

	cookieCartIDTTL, err := getEnvInt("COOKIE_CART_ID_TTL", 604800)
	if err != nil {
		return nil, err
	}

	redisDB, err := getEnvInt("REDIS_DB", 0)
	if err != nil {
		return nil, err
	}

	return &Props{
		AppPort:           getEnvStr("APP_PORT", ":8080"),
		CookieCartIDTTL:   cookieCartIDTTL,
		RedisAddress:      getEnvStr("REDIS_ADDRESS", "localhost:6379"),
		RedisPassword:     getEnvStr("REDIS_PASSWORD", ""),
		RedisDB:           redisDB,
		GrpcCatalogTarget: getEnvStr("GRPC_CATALOG_TARGET", "localhost:4770"),
	}, nil
}

func loadLocalEnv(basePath string) error {
	environment := os.Getenv("APP_ENV")
	if environment == "" {
		environment = "local"
	}
	if _, runningInContainer := os.LookupEnv("CONTAINER"); !runningInContainer {
		return godotenv.Load(basePath + "/env/.env." + environment)
	}
	return nil
}

func getEnvStr(key, def string) string {
	if value, ok := os.LookupEnv(key); ok {
		return value
	}
	return def
}

func getEnvInt(key string, def int) (int, error) {
	if value, ok := os.LookupEnv(key); ok {
		i, err := strconv.Atoi(value)
		if err != nil {
			return 0, fmt.Errorf("invalid int for %s: %w", key, err)
		}
		return i, nil
	}
	return def, nil
}
