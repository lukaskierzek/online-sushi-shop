package repositories

import (
	"context"
	"encoding/json"
	"time"

	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
)

type CatalogRepository struct {
	db *redis.Client
	p  *utils.ApplicationProperties
}

func NewCatalogRepository(db *redis.Client, p *utils.ApplicationProperties) *CatalogRepository {
	return &CatalogRepository{db: db, p: p}
}

func (cr *CatalogRepository) GetProductPrice(id string, ctx context.Context) (*decimal.Decimal, error) {
	return utils.FromRedis[decimal.Decimal](cr.db.Get(ctx, "products:prices::"+id).Result)
}

func (r *CatalogRepository) SaveProductPrice(id string, price decimal.Decimal, ctx context.Context) error {
	data, err := json.Marshal(price)
	if err != nil {
		return err
	}
	return r.db.SetEx(ctx, "products:prices::"+id, data, time.Duration(7200)*time.Second).Err()
}

func (cr *CatalogRepository) GetProduct(id string, ctx context.Context) (*models.ProductDetails, error) {
	return utils.FromRedis[models.ProductDetails](cr.db.Get(ctx, "products:details::"+id).Result)
}

func (r *CatalogRepository) SaveProduct(id string, details models.ProductDetails, ctx context.Context) error {
	data, err := json.Marshal(details)
	if err != nil {
		return err
	}
	return r.db.SetEx(ctx, "products:details::"+id, data, time.Duration(7200)*time.Second).Err()
}
