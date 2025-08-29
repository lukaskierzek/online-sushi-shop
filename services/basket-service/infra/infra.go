package infra

import (
	"context"
	"encoding/json"
	"time"

	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
)

const (
	basketPrefix  = "basket::"
	productPrefix = "product::"
)

type BasketRepository interface {
	SaveBasket(ctx context.Context, basket domain.Basket) error
	GetBasketByID(ctx context.Context, id string) (*domain.Basket, error)
	CreateEmptyBasket(ctx context.Context) (*domain.Basket, error)
	DeleteBasket(ctx context.Context, id string) error
}

type basketRepositoryImpl struct {
	db *redis.Client
}

type productRepositoryImpl struct {
	db *redis.Client
	cc catalogpb.CatalogServiceClient
}

type ProductRepository interface {
	GetProductDetails(ctx context.Context, id string) (*domain.BasketItemDetails, error)
}

func NewBasketRepository(db *redis.Client) BasketRepository {
	return &basketRepositoryImpl{db: db}
}

func NewProductRepository(db *redis.Client, cc catalogpb.CatalogServiceClient) ProductRepository {
	return &productRepositoryImpl{db: db, cc: cc}
}

func (r *basketRepositoryImpl) SaveBasket(ctx context.Context, basket domain.Basket) error {
	data, err := json.Marshal(basket)
	if err != nil {
		return err
	}
	return r.db.Set(ctx, basketPrefix+basket.ID, data, time.Hour*24).Err()
}

func (r *basketRepositoryImpl) GetBasketByID(ctx context.Context, id string) (*domain.Basket, error) {
	val, err := r.db.Get(ctx, basketPrefix+id).Result()
	if err != nil && err != redis.Nil {
		return nil, err
	}

	var basket domain.Basket
	if err := json.Unmarshal([]byte(val), &basket); err != nil {
		return nil, err
	}

	return &basket, nil
}

func (r *basketRepositoryImpl) CreateEmptyBasket(ctx context.Context) (*domain.Basket, error) {
	b := domain.Basket{
		ID:         uuid.New().String(),
		Items:      []domain.BasketItem{},
		TotalPrice: decimal.NewFromInt(0),
	}

	err := r.SaveBasket(ctx, b)
	return &b, err
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

	clntPrdc, err := r.cc.GetProduct(ctx, &catalogpb.GetProductRequest{Id: id})
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

	err = r.db.SetEx(ctx, productPrefix+id, product, time.Hour*12).Err()
	if err != nil && err != redis.Nil {
		return nil, err
	}

	return product, nil
}
