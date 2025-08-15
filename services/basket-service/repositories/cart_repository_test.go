package repositories

import (
	"encoding/json"
	"testing"

	"github.com/alicebob/miniredis/v2"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/assert"
)

func saveCartToRedis(t *testing.T, rdb *redis.Client, key string, cart models.Cart) {
	t.Helper()
	data, err := json.Marshal(cart)
	assert.NoError(t, err)
	assert.NoError(t, rdb.Set(t.Context(), "carts::"+key, data, 0).Err())
}

func createTestCart(id, ownerID string, items []models.CartItem) models.Cart {
	return models.Cart{
		ID:         id,
		OwnerID:    ownerID,
		CartItems:  items,
		TotalPrice: calculateTotal(items),
	}
}

func calculateTotal(items []models.CartItem) decimal.Decimal {
	total := decimal.Zero
	for _, item := range items {
		total = total.Add(item.UnitPrice.Mul(decimal.NewFromInt(int64(item.Quantity))))
	}
	return total
}

func TestSaveCart(t *testing.T) {
	s := miniredis.RunT(t)
	defer s.Close()

	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb}

	items := []models.CartItem{
		{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 2},
		{ID: "2", ProductID: "P2", UnitPrice: decimal.NewFromInt(5), Quantity: 3},
	}
	expectedCart := createTestCart("CART-1", "OWNER-1", items)

	cart, err := r.SaveCart(t.Context(), expectedCart)
	assert.NoError(t, err)
	assert.Equal(t, expectedCart, cart)

	var savedCart models.Cart
	data, _ := rdb.Get(t.Context(), "carts::"+cart.ID).Result()
	assert.NoError(t, json.Unmarshal([]byte(data), &savedCart))
	assert.Equal(t, expectedCart, savedCart)
}

func TestGetEmptyCart(t *testing.T) {
	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb}
	cart, err := r.GetCart(t.Context(), GetCartQuery{})
	if err != nil {
		assert.ErrorIs(t, err, redis.Nil)
		assert.Equal(t, models.Cart{}, cart)
		return
	}

	assert.NoError(t, err)
	assert.Equal(t, models.Cart{}, cart)
}

func TestGetOwnersCart(t *testing.T) {
	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb}

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(12), Quantity: 1}}
	cart := createTestCart("CART-2", "OWNER-2", items)
	saveCartToRedis(t, rdb, cart.OwnerID, cart)

	result, err := r.GetCart(t.Context(), GetCartQuery{OwnerID: cart.OwnerID})
	assert.NoError(t, err)
	assert.Equal(t, cart, result)
}

func TestGetGuestCart(t *testing.T) {
	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb}

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(8), Quantity: 2}}
	cart := createTestCart("CART-3", "", items)
	saveCartToRedis(t, rdb, cart.ID, cart)

	result, err := r.GetCart(t.Context(), GetCartQuery{ID: cart.ID})
	assert.NoError(t, err)
	assert.Equal(t, cart, result)
}

func TestGetTransferedCart(t *testing.T) {
	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb}

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 1}}
	guestCart := createTestCart("CART-4", "", items)
	saveCartToRedis(t, rdb, guestCart.ID, guestCart)

	newOwnerID := "OWNER-4"
	result, err := r.GetCart(t.Context(), GetCartQuery{ID: guestCart.ID, OwnerID: newOwnerID})
	assert.NoError(t, err)

	expected := guestCart
	expected.OwnerID = newOwnerID
	assert.Equal(t, expected, result)
}

func TestGetMergedCart(t *testing.T) {
	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb}

	ownerID := "OWNER-5"

	guestItems := []models.CartItem{
		{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 1},
		{ID: "2", ProductID: "P2", UnitPrice: decimal.NewFromInt(20), Quantity: 2},
	}
	guestCart := createTestCart("CART-5", "", guestItems)
	saveCartToRedis(t, rdb, guestCart.ID, guestCart)

	ownerItems := []models.CartItem{
		{ID: "3", ProductID: "P3", UnitPrice: decimal.NewFromInt(15), Quantity: 1},
		{ID: "4", ProductID: "P4", UnitPrice: decimal.NewFromInt(25), Quantity: 1},
	}
	ownersCart := createTestCart("CART-6", ownerID, ownerItems)
	saveCartToRedis(t, rdb, ownersCart.OwnerID, ownersCart)

	result, err := r.GetCart(t.Context(), GetCartQuery{
		ID:      guestCart.ID,
		OwnerID: ownerID,
	})
	assert.NoError(t, err)

	expectedItems := append(guestItems, ownerItems...)
	expectedTotal := guestCart.TotalPrice.Add(ownersCart.TotalPrice)

	assert.Equal(t, ownerID, result.OwnerID)
	assert.True(t, expectedTotal.Equal(result.TotalPrice))
	assert.ElementsMatch(t, expectedItems, result.CartItems)
}
