package repositories

import (
	"context"
	"encoding/json"

	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
)

type CartRepository struct {
	db    *redis.Client
	props utils.ApplicationProperties
}

func NewCartRepository(db *redis.Client, props utils.ApplicationProperties) *CartRepository {
	return &CartRepository{
		db:    db,
		props: props,
	}
}

func (r *CartRepository) SaveCart(ctx context.Context, cart models.Cart) (models.Cart, error) {
	data, err := json.Marshal(cart)
	if err != nil {
		return models.Cart{}, err
	}

	err = r.db.SetEx(ctx, "carts::"+cart.ID, data, r.props.CartIDCookieTtl).Err()
	if err != nil {
		return models.Cart{}, err
	}

	return cart, nil
}

type GetCartQuery struct {
	ID      string
	OwnerID string
}

func (r *CartRepository) GetCart(ctx context.Context, query GetCartQuery) (models.Cart, error) {
	guestCart, err := r.loadCartByID(ctx, query.ID)
	if err != nil {
		return models.Cart{}, err
	}
	userCart, err := r.loadCartByID(ctx, query.OwnerID)
	if err != nil {
		return models.Cart{}, err
	}

	switch {
	case userCart != nil && guestCart != nil:
		return r.mergeAndSaveCarts(ctx, *userCart, *guestCart, query.OwnerID)
	case userCart != nil:
		return r.ensureOwnerID(ctx, *userCart, query.OwnerID)
	case guestCart != nil && query.OwnerID != "":
		return r.transferGuestToUser(ctx, *guestCart, query.OwnerID)
	case guestCart != nil:
		return *guestCart, nil
	default:
		return models.Cart{}, nil
	}
}

func (r *CartRepository) loadCartByID(ctx context.Context, id string) (*models.Cart, error) {
	if id == "" {
		return nil, nil
	}
	jsonData, err := r.db.Get(ctx, "carts::"+id).Result()
	if err == redis.Nil {
		return nil, nil
	}
	if err != nil {
		return nil, err
	}
	var cart models.Cart
	if err := json.Unmarshal([]byte(jsonData), &cart); err != nil {
		return nil, err
	}
	return &cart, nil
}

func (r *CartRepository) saveCart(ctx context.Context, id string, cart models.Cart) error {
	data, _ := json.Marshal(cart)
	return r.db.SetEx(ctx, "carts::"+id, data, r.props.CartIDCookieTtl).Err()
}

func (r *CartRepository) deleteCart(ctx context.Context, id string) error {
	return r.db.Del(ctx, "carts::"+id).Err()
}

func (r *CartRepository) mergeAndSaveCarts(ctx context.Context, userCart, guestCart models.Cart, ownerID string) (models.Cart, error) {
	merged := r.MergeCarts(userCart, guestCart)
	merged.OwnerID = ownerID
	if err := r.saveCart(ctx, ownerID, merged); err != nil {
		return models.Cart{}, err
	}
	if err := r.deleteCart(ctx, guestCart.ID); err != nil {
		return models.Cart{}, err
	}
	return merged, nil
}

func (r *CartRepository) ensureOwnerID(ctx context.Context, cart models.Cart, ownerID string) (models.Cart, error) {
	if cart.OwnerID == "" && ownerID != "" {
		cart.OwnerID = ownerID
		if err := r.saveCart(ctx, ownerID, cart); err != nil {
			return models.Cart{}, err
		}
	}
	return cart, nil
}

func (r *CartRepository) transferGuestToUser(ctx context.Context, guestCart models.Cart, ownerID string) (models.Cart, error) {
	guestCart.OwnerID = ownerID
	if err := r.saveCart(ctx, ownerID, guestCart); err != nil {
		return models.Cart{}, err
	}
	if err := r.deleteCart(ctx, guestCart.ID); err != nil {
		return models.Cart{}, err
	}
	return guestCart, nil
}

func (r *CartRepository) MergeCarts(cart1 models.Cart, cart2 models.Cart) models.Cart {
	itemMap := make(map[string]models.CartItem)

	for _, item := range cart1.CartItems {
		itemMap[item.ProductID] = item
	}

	for _, item := range cart2.CartItems {
		if existing, ok := itemMap[item.ProductID]; ok {
			existing.Quantity += item.Quantity
			itemMap[item.ProductID] = existing
		} else {
			itemMap[item.ProductID] = item
		}
	}

	var mergedItems []models.CartItem
	total := decimal.NewFromInt(0)
	for _, item := range itemMap {
		lineTotal := item.UnitPrice.Mul(decimal.NewFromInt(int64(item.Quantity)))
		total = total.Add(lineTotal)
		mergedItems = append(mergedItems, item)
	}

	return models.Cart{
		ID:         cart1.ID,
		OwnerID:    cart1.OwnerID,
		CartItems:  mergedItems,
		TotalPrice: total,
	}
}
