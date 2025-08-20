package repositories

import (
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

type CatalogRepositoryTestSuite struct {
	suite.Suite
	p   *utils.ApplicationProperties
	r   *CatalogRepository
	rdb *redis.Client
}

func (suite *CatalogRepositoryTestSuite) SetupTest() {
	t := suite.T()
	t.Setenv("APP_ENV", "unit")

	p, err := utils.ResolveApplicationProperties("..")
	if err != nil {
		log.Fatal(err)
	}

	suite.p = p
	s := miniredis.RunT(t)
	suite.rdb = redis.NewClient(&redis.Options{Addr: s.Addr(), DB: p.DBIndex})
	suite.r = NewCatalogRepository(suite.rdb, p)
}

func TestCatalogRepositorySuite(t *testing.T) {
	suite.Run(t, new(CatalogRepositoryTestSuite))
}

func (suite *CatalogRepositoryTestSuite) TestSaveAndGetProductPrice() {
	t := suite.T()

	productID := "P1"
	price := decimal.NewFromFloat(12.50)

	err := suite.r.SaveProductPrice(productID, price, t.Context())
	assert.NoError(t, err)

	// Check directly in Redis
	saved, err := utils.FromRedis[decimal.Decimal](
		suite.rdb.Get(t.Context(), "products:prices::"+productID),
	)
	assert.NoError(t, err)
	assert.True(t, price.Equal(*saved))

	// Use repository Get
	result, err := suite.r.GetProductPrice(productID, t.Context())
	assert.NoError(t, err)
	assert.True(t, price.Equal(*result))
}

func (suite *CatalogRepositoryTestSuite) TestGetNonExistingProductPrice() {
	t := suite.T()

	p, err := suite.r.GetProductPrice("NOT_FOUND", t.Context())
	assert.NoError(t, err)
	assert.Nil(t, p)
}

func (suite *CatalogRepositoryTestSuite) TestSaveAndGetProductDetails() {
	t := suite.T()

	productID := "P2"
	details := models.ProductDetails{
		Name:     "Sushi Set",
		Link:     "http://shop/sushi-set",
		ImageURL: "http://img/sushi.png",
	}

	err := suite.r.SaveProduct(productID, details, t.Context())
	assert.NoError(t, err)

	saved, err := utils.FromRedis[models.ProductDetails](
		suite.rdb.Get(t.Context(), "products:details::"+productID),
	)
	assert.NoError(t, err)
	assert.Equal(t, details, *saved)

	result, err := suite.r.GetProduct(productID, t.Context())
	assert.NoError(t, err)
	assert.Equal(t, details, *result)
}

func (suite *CatalogRepositoryTestSuite) TestGetNonExistingProductDetails() {
	t := suite.T()

	d, err := suite.r.GetProduct("NOT_FOUND", t.Context())
	assert.NoError(t, err)
	assert.Nil(t, d)
}
