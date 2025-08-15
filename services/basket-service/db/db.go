package db

import "github.com/redis/go-redis/v9"

func NewDB() *redis.Client {
	return redis.NewClient(&redis.Options{
		Addr: "localhost:6379",
		DB:   0,
	})
}
