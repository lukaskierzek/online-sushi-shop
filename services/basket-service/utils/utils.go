package utils

import (
	"encoding/json"
	"errors"
	"os"
	"strconv"
	"time"

	"github.com/joho/godotenv"
	"github.com/redis/go-redis/v9"
)

func ResolveApplicationProperties(basePath string) (*ApplicationProperties, error) {
	if err := loadLocalEnv(basePath); err != nil {
		return nil, err
	}

	dbIndex, err := getEnv("DB_INDEX")
	if err != nil {
		return nil, err
	}

	dbi, err := strconv.Atoi(*dbIndex)
	if err != nil {
		return nil, err
	}

	ttl, err := getEnv("CART_ID_COOKIE_TTL")
	if err != nil {
		return nil, err
	}

	cictm, err := strconv.Atoi(*ttl)
	if err != nil {
		return nil, err
	}

	port, err := getEnv("SERVER_PORT")
	if err != nil {
		return nil, err
	}

	secret, err := getEnv("JWT_SECRET")
	if err != nil {
		return nil, err
	}

	url, err := getEnv("DB_URL")
	if err != nil {
		return nil, err
	}

	target, err := getEnv("CATALOG_GRPC_TARGET")
	if err != nil {
		return nil, err
	}

	return &ApplicationProperties{
		ServerPort:        *port,
		JwtSecret:         *secret,
		DBUrl:             *url,
		DBIndex:           dbi,
		CatalogGrpcTarget: *target,
		CartIDCookieTtl:   time.Duration(cictm) * time.Second,
	}, nil
}

func loadLocalEnv(basePath string) error {
	environment := os.Getenv("APP_ENV")
	if environment == "" {
		environment = "local"
	}

	if _, runningInContainer := os.LookupEnv("CONTAINER"); !runningInContainer {
		if err := godotenv.Load(basePath + "/env/.env." + environment); err != nil {
			return err
		}
	}
	return nil
}

func getEnv(key string) (*string, error) {
	value, ok := os.LookupEnv(key)
	if !ok {
		return nil, errors.New("Environment variable not found: " + key)
	}
	return &value, nil
}

type ApplicationProperties struct {
	ServerPort        string
	JwtSecret         string
	DBUrl             string
	DBIndex           int
	CatalogGrpcTarget string
	CartIDCookieTtl   time.Duration
}

func FromRedis[T any](cmd *redis.StringCmd) (*T, error) {
	jsonData, err := cmd.Result()
	if err == redis.Nil {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	var result T
	if err := json.Unmarshal([]byte(jsonData), &result); err != nil {
		return nil, err
	}
	return &result, nil
}
