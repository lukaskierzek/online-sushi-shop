package repositories

import (
	"encoding/json"
	"log"
	"testing"

	"github.com/alicebob/miniredis/v2"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"

	"github.com/stretchr/testify/assert"
)

func TestSaveCart(t *testing.T) {
	s := miniredis.RunT(t)

	rdb := redis.NewClient(&redis.Options{
		Addr: s.Addr(),
		DB:   0,
	})

	defer s.Close()
	defer rdb.Close()

	r := &CartRepository{db: rdb}

	testCartPrice, err := decimal.NewFromString("20")
	if err != nil {
		log.Fatal(err)
	}

	expectedQuantity := int32(3)

	expectedTotalPrice := testCartPrice.Mul(decimal.NewFromInt(int64(expectedQuantity)))

	expectedCartItems := []models.CartItem{
		{
			ID:        uuid.NewString(),
			ProductID: uuid.NewString(),
			UnitPrice: testCartPrice,
			Quantity:  expectedQuantity,
		},
	}

	testCart := models.Cart{
		ID:         uuid.NewString(),
		OwnerID:    uuid.NewString(),
		CartItems:  expectedCartItems,
		TotalPrice: expectedTotalPrice,
	}

	cart, err := r.SaveCart(t.Context(), testCart)
	if err != nil {
		log.Fatal(err)
	}

	assert.NotEqual(t, &models.Cart{}, cart)
	assert.Equal(t, cart.ID, testCart.ID)
	assert.Equal(t, cart.OwnerID, testCart.OwnerID)
	assert.Equal(t, cart.TotalPrice, expectedTotalPrice)
	assert.Equal(t, cart.CartItems, expectedCartItems)

	savedJson, err := rdb.Get(t.Context(), "carts::"+cart.ID).Result()
	if err != nil {
		log.Fatal(err)
	}

	var savedCart models.Cart
	if err := json.Unmarshal([]byte(savedJson), &savedCart); err != nil {
		log.Fatal(err)
	}

	assert.Equal(t, testCart, savedCart)
}
