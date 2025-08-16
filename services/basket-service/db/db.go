package db

import (
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
)

func NewRedisClient(p *utils.ApplicationProperties) *redis.Client {
	return redis.NewClient(&redis.Options{
		Addr: p.DBUrl,
		DB:   p.DBIndex,
	})
}
