package infra

import (
	"context"
	"encoding/json"
	"time"

	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"

	catalog_v1 "github.com/kamilszymanski707/proto-lib/catalog.v1"
)

const (
	basketPrefix  = "basket::"
	productPrefix = "product::"
	defaultTTL    = 1800 // in seconds
)

type BasketRepository interface {
	SaveBasket(ctx context.Context, basket *domain.Basket) error
	GetBasketByID(ctx context.Context, id string) (*domain.Basket, error)
	CreateEmptyBasket(ctx context.Context) (*domain.Basket, error)
	DeleteBasket(ctx context.Context, id string) error
}

type basketRepositoryImpl struct {
	db        *redis.Client
	cookieTTL int
}

type productRepositoryImpl struct {
	db *redis.Client
	cc catalog_v1.CatalogServiceClient
}

type ProductRepository interface {
	GetProductDetails(ctx context.Context, id string) (*domain.BasketItemDetails, error)
}

func NewBasketRepository(db *redis.Client, cookieTTL int) BasketRepository {
	return &basketRepositoryImpl{db: db, cookieTTL: cookieTTL}
}

func NewProductRepository(db *redis.Client, cc catalog_v1.CatalogServiceClient) ProductRepository {
	return &productRepositoryImpl{db: db, cc: cc}
}

func (r *basketRepositoryImpl) SaveBasket(ctx context.Context, basket *domain.Basket) error {
	data, err := json.Marshal(basket)
	if err != nil {
		return err
	}
	return r.db.SetEx(ctx, basketPrefix+basket.ID, data, time.Duration(r.cookieTTL)*time.Second).Err()
}

func (r *basketRepositoryImpl) GetBasketByID(ctx context.Context, id string) (*domain.Basket, error) {
	val, err := r.db.Get(ctx, basketPrefix+id).Result()
	if err == redis.Nil {
		return nil, nil
	}

	if err != nil {
		return nil, err
	}

	var basket domain.Basket
	if err := json.Unmarshal([]byte(val), &basket); err != nil {
		return nil, err
	}

	return &basket, nil
}

func (r *basketRepositoryImpl) CreateEmptyBasket(ctx context.Context) (*domain.Basket, error) {
	b := &domain.Basket{
		ID:         uuid.New().String(),
		Items:      []domain.BasketItem{},
		TotalPrice: decimal.NewFromInt(0),
	}

	err := r.SaveBasket(ctx, b)
	return b, err
}

func (r *basketRepositoryImpl) DeleteBasket(ctx context.Context, id string) error {
	return r.db.Del(ctx, basketPrefix+id).Err()
}

func (r *productRepositoryImpl) GetProductDetails(ctx context.Context, id string) (*domain.BasketItemDetails, error) {
	val, err := r.db.Get(ctx, productPrefix+id).Result()
	if err != nil && err != redis.Nil {
		return nil, err
	}

	if val != "" {
		var product domain.BasketItemDetails
		if err := json.Unmarshal([]byte(val), &product); err != nil {
			return nil, err
		}

		return &product, nil
	}

	clntPrdc, err := r.cc.GetProduct(ctx, &catalog_v1.GetProductRequest{Id: id})
	if err != nil {
		return nil, err
	}

	price, err := decimal.NewFromString(clntPrdc.Price)
	if err != nil {
		return nil, err
	}

	product := &domain.BasketItemDetails{
		Name:     clntPrdc.Name,
		ImageURL: clntPrdc.ImageUrl,
		Link:     clntPrdc.Link,
		Price:    price,
	}

	data, err := json.Marshal(product)
	if err != nil {
		return nil, err
	}

	err = r.db.SetEx(ctx, productPrefix+id, data, defaultTTL*time.Second).Err()
	if err != nil && err != redis.Nil {
		return nil, err
	}

	return product, nil
}
