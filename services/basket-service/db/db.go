package db

import "github.com/redis/go-redis/v9"

func NewDB(dbURL string, dbIndex int) *redis.Client {
	return redis.NewClient(&redis.Options{
		Addr: dbURL,
		DB:   dbIndex,
	})
}
