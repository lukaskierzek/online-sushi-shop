package app

import (
	"context"
	"testing"

	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"
)

type MockBasketRepository struct {
	mock.Mock
}

func (m *MockBasketRepository) SaveBasket(ctx context.Context, basket *domain.Basket) error {
	args := m.Called(ctx, basket)
	return args.Error(0)
}
func (m *MockBasketRepository) GetBasketByID(ctx context.Context, id string) (*domain.Basket, error) {
	args := m.Called(ctx, id)
	return args.Get(0).(*domain.Basket), args.Error(1)
}
func (m *MockBasketRepository) CreateEmptyBasket(ctx context.Context) (*domain.Basket, error) {
	args := m.Called(ctx)
	return args.Get(0).(*domain.Basket), args.Error(1)
}
func (m *MockBasketRepository) DeleteBasket(ctx context.Context, id string) error {
	args := m.Called(ctx, id)
	return args.Error(0)
}

type MockProductRepository struct {
	mock.Mock
}

func (m *MockProductRepository) GetProductDetails(ctx context.Context, id string) (*domain.BasketItemDetails, error) {
	args := m.Called(ctx, id)
	if args.Get(0) == nil {
		return nil, args.Error(1)
	}
	return args.Get(0).(*domain.BasketItemDetails), args.Error(1)
}

type BasketServiceTestSuite struct {
	suite.Suite
	brepo  *MockBasketRepository
	prepo  *MockProductRepository
	svc    *BasketService
	basket *domain.Basket
}

func (suite *BasketServiceTestSuite) SetupTest() {
	suite.brepo = new(MockBasketRepository)
	suite.prepo = new(MockProductRepository)
	suite.svc = NewBasketService(suite.brepo, suite.prepo)
	suite.basket = &domain.Basket{ID: uuid.New().String()}
}

func (suite *BasketServiceTestSuite) TestAddItem_Success() {
	ctx := context.Background()
	details := &domain.BasketItemDetails{Name: "Sushi", Price: decimal.NewFromInt(10), ImageURL: "img.jpg", Link: "link"}
	item := domain.BasketItem{ProductID: "prod1", Quantity: 2, ProductDetails: *details}

	suite.prepo.On("GetProductDetails", ctx, "prod1").Return(details, nil)
	suite.brepo.On("SaveBasket", ctx, mock.AnythingOfType("*domain.Basket")).Return(nil)

	basket, err := suite.svc.AddItem(ctx, suite.basket, item)
	suite.NoError(err)
	suite.NotNil(basket)
	suite.Equal("Sushi", basket.Items[0].ProductDetails.Name)
	suite.prepo.AssertExpectations(suite.T())
	suite.brepo.AssertExpectations(suite.T())
}

func (suite *BasketServiceTestSuite) TestAddItem_ProductNotFound() {
	ctx := context.Background()
	item := domain.BasketItem{ProductID: "prod2", Quantity: 1}
	suite.prepo.On("GetProductDetails", ctx, "prod2").Return(nil, nil)

	basket, err := suite.svc.AddItem(ctx, suite.basket, item)
	suite.Error(err)
	suite.Nil(basket)
}

func (suite *BasketServiceTestSuite) TestRemoveItem() {
	ctx := context.Background()
	suite.brepo.On("SaveBasket", ctx, suite.basket).Return(nil)
	suite.basket.Items = append(suite.basket.Items, domain.BasketItem{ProductID: "prod1", Quantity: 1})

	basket, err := suite.svc.RemoveItem(ctx, suite.basket, "prod1")
	suite.NoError(err)
	suite.NotNil(basket)
	suite.brepo.AssertExpectations(suite.T())
}

func (suite *BasketServiceTestSuite) TestChangeQuantity() {
	ctx := context.Background()
	suite.brepo.On("SaveBasket", ctx, suite.basket).Return(nil)
	suite.basket.Items = append(suite.basket.Items, domain.BasketItem{ProductID: "prod1", Quantity: 1})

	basket, err := suite.svc.ChangeQuantity(ctx, suite.basket, "prod1", 3)
	suite.NoError(err)
	suite.NotNil(basket)
	suite.Equal(3, basket.Items[0].Quantity)
	suite.brepo.AssertExpectations(suite.T())
}

func (suite *BasketServiceTestSuite) TestClear() {
	ctx := context.Background()
	suite.brepo.On("SaveBasket", ctx, suite.basket).Return(nil)
	suite.basket.Items = append(suite.basket.Items, domain.BasketItem{ProductID: "prod1", Quantity: 1})

	basket, err := suite.svc.Clear(ctx, suite.basket)
	suite.NoError(err)
	suite.NotNil(basket)
	suite.Equal(0, len(basket.Items))
	suite.brepo.AssertExpectations(suite.T())
}

func (suite *BasketServiceTestSuite) TestComplete() {
	ctx := context.Background()
	suite.brepo.On("SaveBasket", ctx, suite.basket).Return(nil)

	basket, err := suite.svc.Complete(ctx, suite.basket)
	suite.NoError(err)
	suite.NotNil(basket)
	suite.NotNil(basket.CompleteDate)
	suite.brepo.AssertExpectations(suite.T())
}

func TestBasketServiceTestSuite(t *testing.T) {
	suite.Run(t, new(BasketServiceTestSuite))
}
