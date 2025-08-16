package repositories

import (
	"encoding/json"
	"log"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/assert"
)

func TestSaveCart(t *testing.T) {
	t.Setenv("APP_ENV", "unit")
	p := utils.ResolveApplicationProperties("..")

	s := miniredis.RunT(t)
	defer s.Close()

	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: p.DBIndex})
	defer rdb.Close()

	r := &CartRepository{db: rdb, p: p}

	items := []models.CartItem{
		{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 2},
		{ID: "2", ProductID: "P2", UnitPrice: decimal.NewFromInt(5), Quantity: 3},
	}
	expectedCart := createTestCart("CART-1", "OWNER-1", items)

	cart, err := r.SaveCart(expectedCart, t.Context())
	assert.NoError(t, err)
	assert.Equal(t, expectedCart, *cart)

	savedCart, err := utils.FromRedis[models.Cart](rdb.Get(t.Context(), "carts::"+cart.ID).Result)
	if err != nil {
		log.Fatal(err)
	}

	assert.Equal(t, expectedCart, *savedCart)
}

func TestGetEmptyCart(t *testing.T) {
	t.Setenv("APP_ENV", "unit")
	p := utils.ResolveApplicationProperties("..")

	s := miniredis.RunT(t)
	defer s.Close()

	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb, p: p}
	cart, err := r.GetCart(GetCartQuery{}, t.Context())
	if err != nil {
		assert.ErrorIs(t, err, redis.Nil)
		assert.Nil(t, cart)
		return
	}

	assert.NoError(t, err)
	assert.Nil(t, cart)
}

func TestGetOwnersCart(t *testing.T) {
	t.Setenv("APP_ENV", "unit")
	p := utils.ResolveApplicationProperties("..")

	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb, p: p}

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(12), Quantity: 1}}
	cart := createTestCart("CART-2", "OWNER-2", items)
	saveCartToRedis(t, rdb, cart.OwnerID, cart, p.CartIDCookieTtl)

	result, err := r.GetCart(GetCartQuery{OwnerID: cart.OwnerID}, t.Context())
	assert.NoError(t, err)
	assert.Equal(t, cart, *result)
}

func TestGetGuestCart(t *testing.T) {
	t.Setenv("APP_ENV", "unit")
	applicationProperties := utils.ResolveApplicationProperties("..")

	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb, p: applicationProperties}

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(8), Quantity: 2}}
	cart := createTestCart("CART-3", "", items)
	saveCartToRedis(t, rdb, cart.ID, cart, applicationProperties.CartIDCookieTtl)

	result, err := r.GetCart(GetCartQuery{ID: cart.ID}, t.Context())
	assert.NoError(t, err)
	assert.Equal(t, cart, *result)
}

func TestGetTransferedCart(t *testing.T) {
	t.Setenv("APP_ENV", "unit")
	p := utils.ResolveApplicationProperties("..")

	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb, p: p}

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 1}}
	guestCart := createTestCart("CART-4", "", items)
	saveCartToRedis(t, rdb, guestCart.ID, guestCart, p.CartIDCookieTtl)

	newOwnerID := "OWNER-4"
	result, err := r.GetCart(GetCartQuery{ID: guestCart.ID, OwnerID: newOwnerID}, t.Context())
	assert.NoError(t, err)

	expected := guestCart
	expected.OwnerID = newOwnerID
	assert.Equal(t, expected, *result)
}

func TestGetMergedCart(t *testing.T) {
	t.Setenv("APP_ENV", "unit")
	p := utils.ResolveApplicationProperties("..")

	s := miniredis.RunT(t)
	defer s.Close()
	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	r := &CartRepository{db: rdb, p: p}

	ownerID := "OWNER-5"

	guestItems := []models.CartItem{
		{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 1},
		{ID: "2", ProductID: "P2", UnitPrice: decimal.NewFromInt(20), Quantity: 2},
	}
	guestCart := createTestCart("CART-5", "", guestItems)
	saveCartToRedis(t, rdb, guestCart.ID, guestCart, p.CartIDCookieTtl)

	ownerItems := []models.CartItem{
		{ID: "3", ProductID: "P3", UnitPrice: decimal.NewFromInt(15), Quantity: 1},
		{ID: "4", ProductID: "P4", UnitPrice: decimal.NewFromInt(25), Quantity: 1},
	}
	ownersCart := createTestCart("CART-6", ownerID, ownerItems)
	saveCartToRedis(t, rdb, ownersCart.OwnerID, ownersCart, p.CartIDCookieTtl)

	result, err := r.GetCart(GetCartQuery{
		ID:      guestCart.ID,
		OwnerID: ownerID,
	}, t.Context())
	assert.NoError(t, err)

	expectedItems := append(guestItems, ownerItems...)
	expectedTotal := guestCart.TotalPrice.Add(ownersCart.TotalPrice)

	assert.Equal(t, ownerID, result.OwnerID)
	assert.True(t, expectedTotal.Equal(result.TotalPrice))
	assert.ElementsMatch(t, expectedItems, result.CartItems)
}

func saveCartToRedis(t *testing.T, rdb *redis.Client, key string, cart models.Cart, ttl time.Duration) {
	t.Helper()
	data, err := json.Marshal(toCartEntity(cart))
	assert.NoError(t, err)
	assert.NoError(t, rdb.SetEx(t.Context(), "carts::"+key, data, ttl).Err())
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
