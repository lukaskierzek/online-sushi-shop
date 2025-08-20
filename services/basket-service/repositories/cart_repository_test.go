package repositories

import (
	"encoding/json"
	"log"
	"testing"

	"github.com/alicebob/miniredis/v2"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/suite"
)

type CartRepositoryTestSuite struct {
	suite.Suite
	p   *utils.ApplicationProperties
	r   *CartRepository
	rdb *redis.Client
}

func (suite *CartRepositoryTestSuite) SetupTest() {
	t := suite.T()
	t.Setenv("APP_ENV", "unit")

	p, err := utils.ResolveApplicationProperties("..")
	if err != nil {
		log.Fatal(err)
	}

	suite.p = p
	s := miniredis.RunT(t)
	suite.rdb = redis.NewClient(&redis.Options{Addr: s.Addr(), DB: p.DBIndex})
	suite.r = &CartRepository{db: suite.rdb, p: p}
}

func TestCartRepositoryTestSuite(t *testing.T) {
	suite.Run(t, new(CartRepositoryTestSuite))
}

func (suite *CartRepositoryTestSuite) TestSaveCart() {
	t := suite.T()

	items := []models.CartItem{
		{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 2},
		{ID: "2", ProductID: "P2", UnitPrice: decimal.NewFromInt(5), Quantity: 3},
	}
	expectedCart := suite.createTestCart("CART-1", "OWNER-1", items)

	cart, err := suite.r.SaveCart(expectedCart, t.Context())
	assert.NoError(t, err)
	assert.Equal(t, expectedCart, *cart)

	savedCart, err := utils.FromRedis[models.Cart](suite.rdb.Get(t.Context(), "carts::"+cart.ID))
	if err != nil {
		log.Fatal(err)
	}

	assert.Equal(t, expectedCart, *savedCart)
}

func (suite *CartRepositoryTestSuite) TestGetEmptyCart() {
	t := suite.T()

	cart, err := suite.r.GetCart(GetCartQuery{}, t.Context())
	if err != nil {
		assert.ErrorIs(t, err, redis.Nil)
		assert.Nil(t, cart)
		return
	}

	assert.NoError(t, err)
	assert.Nil(t, cart)
}

func (suite *CartRepositoryTestSuite) TestGetOwnersCart() {
	t := suite.T()

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(12), Quantity: 1}}
	cart := suite.createTestCart("CART-2", "OWNER-2", items)
	suite.saveCartToRedis(cart.OwnerID, cart)

	result, err := suite.r.GetCart(GetCartQuery{OwnerID: cart.OwnerID}, t.Context())
	assert.NoError(t, err)
	assert.Equal(t, cart, *result)
}

func (suite *CartRepositoryTestSuite) TestGetGuestCart() {
	t := suite.T()

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(8), Quantity: 2}}
	cart := suite.createTestCart("CART-3", "", items)
	suite.saveCartToRedis(cart.ID, cart)

	result, err := suite.r.GetCart(GetCartQuery{ID: cart.ID}, t.Context())
	assert.NoError(t, err)
	assert.Equal(t, cart, *result)
}

func (suite *CartRepositoryTestSuite) TestGetTransferedCart() {
	t := suite.T()

	items := []models.CartItem{{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 1}}
	guestCart := suite.createTestCart("CART-4", "", items)
	suite.saveCartToRedis(guestCart.ID, guestCart)

	newOwnerID := "OWNER-4"
	result, err := suite.r.GetCart(GetCartQuery{ID: guestCart.ID, OwnerID: newOwnerID}, t.Context())
	assert.NoError(t, err)

	expected := guestCart
	expected.OwnerID = newOwnerID
	assert.Equal(t, expected, *result)
}

func (suite *CartRepositoryTestSuite) TestGetMergedCart() {
	t := suite.T()

	ownerID := "OWNER-5"

	guestItems := []models.CartItem{
		{ID: "1", ProductID: "P1", UnitPrice: decimal.NewFromInt(10), Quantity: 1},
		{ID: "2", ProductID: "P2", UnitPrice: decimal.NewFromInt(20), Quantity: 2},
	}
	guestCart := suite.createTestCart("CART-5", "", guestItems)
	suite.saveCartToRedis(guestCart.ID, guestCart)

	ownerItems := []models.CartItem{
		{ID: "3", ProductID: "P3", UnitPrice: decimal.NewFromInt(15), Quantity: 1},
		{ID: "4", ProductID: "P4", UnitPrice: decimal.NewFromInt(25), Quantity: 1},
	}
	ownersCart := suite.createTestCart("CART-6", ownerID, ownerItems)
	suite.saveCartToRedis(ownersCart.OwnerID, ownersCart)

	result, err := suite.r.GetCart(GetCartQuery{
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

func (suite *CartRepositoryTestSuite) saveCartToRedis(key string, cart models.Cart) {
	t := suite.T()
	t.Helper()

	data, err := json.Marshal(toCartEntity(cart))
	assert.NoError(t, err)
	assert.NoError(t, suite.rdb.SetEx(t.Context(), "carts::"+key, data, suite.p.CartIDCookieTtl).Err())
}

func (suite *CartRepositoryTestSuite) createTestCart(id, ownerID string, items []models.CartItem) models.Cart {
	suite.T().Helper()

	return models.Cart{
		ID:         id,
		OwnerID:    ownerID,
		CartItems:  items,
		TotalPrice: suite.calculateTotal(items),
	}
}

func (suite *CartRepositoryTestSuite) calculateTotal(items []models.CartItem) decimal.Decimal {
	suite.T().Helper()

	total := decimal.Zero
	for _, item := range items {
		total = total.Add(item.UnitPrice.Mul(decimal.NewFromInt(int64(item.Quantity))))
	}
	return total
}
