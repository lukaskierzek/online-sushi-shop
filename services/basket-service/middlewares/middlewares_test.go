package middlewares

import (
	"context"
	"net/http"
	"net/http/httptest"
	"testing"
	"time"

	"github.com/gin-gonic/gin"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"
)

type CartMiddlewareTestSuite struct {
	suite.Suite
	router *gin.Engine
	mockBr *MockBasketRepository
	mw     *CartMiddleware
}

func (suite *CartMiddlewareTestSuite) SetupTest() {
	gin.SetMode(gin.TestMode)

	suite.mockBr = new(MockBasketRepository)
	suite.mw = NewCartMiddleware(suite.mockBr, 3600)

	suite.router = gin.New()
	suite.router.Use(suite.mw.CartHandlerFunc())
	suite.router.GET("/test", func(c *gin.Context) {
		cart, exists := c.Get("cart")
		if !exists {
			c.JSON(http.StatusInternalServerError, gin.H{"error": "cart not set"})
			return
		}
		c.JSON(http.StatusOK, gin.H{"cart_id": cart.(*domain.Basket).ID})
	})
}

func (suite *CartMiddlewareTestSuite) Test_NoCookie_CreatesNewCart() {
	suite.mockBr.On("CreateEmptyBasket", mock.Anything).Return(&domain.Basket{
		ID: "new-id",
	}, nil).Once()

	suite.mockBr.On("GetBasketByID", mock.Anything, "new-id").
		Return(nil, redis.Nil).Once()

	suite.mockBr.On("CreateEmptyBasket", mock.Anything).Return(&domain.Basket{
		ID: "another-id",
	}, nil).Once()

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/test", nil)
	req.Host = "localhost"

	suite.router.ServeHTTP(w, req)

	suite.Equal(http.StatusOK, w.Code)
	suite.Contains(w.Body.String(), "{\"cart_id\":\"another-id\"}")

	cookies := w.Result().Cookies()
	found := false
	for _, c := range cookies {
		if c.Name == "cart_id" {
			found = true
			break
		}
	}
	suite.True(found)
}

func (suite *CartMiddlewareTestSuite) Test_WithCookie_FindsBasket() {
	basket := &domain.Basket{ID: "cart123", Items: []domain.BasketItem{}}
	suite.mockBr.On("GetBasketByID", mock.Anything, "cart123").Return(basket, nil)

	req := httptest.NewRequest("GET", "/test", nil)
	req.Host = "localhost"
	req.AddCookie(&http.Cookie{Name: "cart_id", Value: "cart123", Expires: time.Now().Add(time.Hour)})
	w := httptest.NewRecorder()

	suite.router.ServeHTTP(w, req)

	suite.Equal(http.StatusOK, w.Code)
	suite.Contains(w.Body.String(), "cart123")
	suite.mockBr.AssertCalled(suite.T(), "GetBasketByID", mock.Anything, "cart123")
}

func (suite *CartMiddlewareTestSuite) Test_BasketComplete_CreatesNewCart() {
	now := time.Now()
	oldCart := &domain.Basket{ID: "old", CompleteDate: &now}
	newCart := &domain.Basket{ID: "newCart"}

	suite.mockBr.On("GetBasketByID", mock.Anything, "old").Return(oldCart, nil)
	suite.mockBr.On("CreateEmptyBasket", mock.Anything).Return(newCart, nil)

	req := httptest.NewRequest("GET", "/test", nil)
	req.Host = "localhost"
	req.AddCookie(&http.Cookie{Name: "cart_id", Value: "old"})
	w := httptest.NewRecorder()

	suite.router.ServeHTTP(w, req)

	suite.Equal(http.StatusOK, w.Code)
	suite.Contains(w.Body.String(), "newCart")
}

func (suite *CartMiddlewareTestSuite) Test_GetBasketError_Returns500() {
	suite.mockBr.On("GetBasketByID", mock.Anything, "broken").Return(nil, assert.AnError)

	req := httptest.NewRequest("GET", "/test", nil)
	req.Host = "localhost"
	req.AddCookie(&http.Cookie{Name: "cart_id", Value: "broken"})
	w := httptest.NewRecorder()

	suite.router.ServeHTTP(w, req)

	suite.Equal(http.StatusInternalServerError, w.Code)
}

func TestCartMiddlewareTestSuite(t *testing.T) {
	suite.Run(t, new(CartMiddlewareTestSuite))
}

type MockBasketRepository struct {
	mock.Mock
}

func (m *MockBasketRepository) CreateEmptyBasket(ctx context.Context) (*domain.Basket, error) {
	args := m.Called(ctx)
	return args.Get(0).(*domain.Basket), args.Error(1)
}

func (m *MockBasketRepository) GetBasketByID(ctx context.Context, id string) (*domain.Basket, error) {
	args := m.Called(ctx, id)
	if args.Get(0) == nil {
		return nil, args.Error(1)
	}
	return args.Get(0).(*domain.Basket), args.Error(1)
}

func (m *MockBasketRepository) SaveBasket(ctx context.Context, b *domain.Basket) error {
	args := m.Called(ctx, b)
	return args.Error(0)
}

func (m *MockBasketRepository) DeleteBasket(ctx context.Context, id string) error {
	args := m.Called(ctx, id)
	return args.Error(0)
}
