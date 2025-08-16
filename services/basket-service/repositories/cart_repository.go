package repositories

import (
	"context"
	"encoding/json"

	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
)

type cartEntity struct {
	ID         string           `json:"id"`
	OwnerID    string           `json:"owner_id"`
	CartItems  []cartItemEntity `json:"cart_items"`
	TotalPrice decimal.Decimal  `json:"total_price"`
}

type cartItemEntity struct {
	ID        string          `json:"id"`
	ProductID string          `json:"product_id"`
	UnitPrice decimal.Decimal `json:"price"`
	Quantity  int             `json:"quantity"`
}

type CartRepository struct {
	db *redis.Client
	p  *utils.ApplicationProperties
}

func NewCartRepository(db *redis.Client, p *utils.ApplicationProperties) *CartRepository {
	return &CartRepository{
		db: db,
		p:  p,
	}
}

func (r *CartRepository) SaveCart(cart models.Cart, ctx context.Context) (*models.Cart, error) {
	data, err := json.Marshal(toCartEntity(cart))
	if err != nil {
		return nil, err
	}

	err = r.db.SetEx(ctx, "carts::"+cart.ID, data, r.p.CartIDCookieTtl).Err()
	if err != nil {
		return nil, err
	}

	return &cart, nil
}

type GetCartQuery struct {
	ID      string
	OwnerID string
}

func (r *CartRepository) GetCart(query GetCartQuery, ctx context.Context) (*models.Cart, error) {
	guestCart, err := r.loadCartByID(query.ID, ctx)
	if err != nil {
		return nil, err
	}
	userCart, err := r.loadCartByID(query.OwnerID, ctx)
	if err != nil {
		return nil, err
	}

	switch {
	case userCart != nil && guestCart != nil:
		return r.mergeAndSaveCarts(*userCart, *guestCart, query.OwnerID, ctx)
	case userCart != nil:
		return r.ensureOwnerID(*userCart, query.OwnerID, ctx)
	case guestCart != nil && query.OwnerID != "":
		return r.transferGuestToUser(*guestCart, query.OwnerID, ctx)
	case guestCart != nil:
		return guestCart, nil
	default:
		return nil, nil
	}
}

func (r *CartRepository) loadCartByID(id string, ctx context.Context) (*models.Cart, error) {
	return utils.FromRedis[models.Cart](r.db.Get(ctx, "carts::"+id).Result)
}

func (r *CartRepository) saveCart(id string, cart models.Cart, ctx context.Context) error {
	data, err := json.Marshal(toCartEntity(cart))
	if err != nil {
		return err
	}

	return r.db.SetEx(ctx, "carts::"+id, data, r.p.CartIDCookieTtl).Err()
}

func (r *CartRepository) deleteCart(ctx context.Context, id string) error {
	return r.db.Del(ctx, "carts::"+id).Err()
}

func (r *CartRepository) mergeAndSaveCarts(userCart, guestCart models.Cart, ownerID string, ctx context.Context) (*models.Cart, error) {
	merged := r.MergeCarts(userCart, guestCart)
	merged.OwnerID = ownerID
	if err := r.saveCart(ownerID, merged, ctx); err != nil {
		return nil, err
	}
	if err := r.deleteCart(ctx, guestCart.ID); err != nil {
		return nil, err
	}
	return &merged, nil
}

func (r *CartRepository) ensureOwnerID(cart models.Cart, ownerID string, ctx context.Context) (*models.Cart, error) {
	if cart.OwnerID == "" && ownerID != "" {
		cart.OwnerID = ownerID
		if err := r.saveCart(ownerID, cart, ctx); err != nil {
			return nil, err
		}
	}
	return &cart, nil
}

func (r *CartRepository) transferGuestToUser(guestCart models.Cart, ownerID string, ctx context.Context) (*models.Cart, error) {
	guestCart.OwnerID = ownerID
	if err := r.saveCart(ownerID, guestCart, ctx); err != nil {
		return nil, err
	}
	if err := r.deleteCart(ctx, guestCart.ID); err != nil {
		return nil, err
	}
	return &guestCart, nil
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

func toCartEntity(cart models.Cart) cartEntity {
	return cartEntity{
		ID:         cart.ID,
		OwnerID:    cart.OwnerID,
		TotalPrice: cart.TotalPrice,
		CartItems:  toCartItemsEntities(cart.CartItems),
	}
}

func toCartItemsEntities(cartItems []models.CartItem) []cartItemEntity {
	var result []cartItemEntity

	for _, cartItem := range cartItems {
		result = append(result, toCartItemEntity(cartItem))
	}

	return result
}

func toCartItemEntity(cartItem models.CartItem) cartItemEntity {
	return cartItemEntity{
		ID:        cartItem.ID,
		ProductID: cartItem.ProductID,
		UnitPrice: cartItem.UnitPrice,
		Quantity:  cartItem.Quantity,
	}
}
