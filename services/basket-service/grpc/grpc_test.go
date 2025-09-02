package grpc

import (
	"context"
	"errors"
	"testing"

	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
	basket_v1 "github.com/kamilszymanski707/proto-lib/basket.v1"
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
	if basket, ok := args.Get(0).(*domain.Basket); ok {
		return basket, args.Error(1)
	}
	return nil, args.Error(1)
}

func (m *MockBasketRepository) CreateEmptyBasket(ctx context.Context) (*domain.Basket, error) {
	args := m.Called(ctx)
	if basket, ok := args.Get(0).(*domain.Basket); ok {
		return basket, args.Error(1)
	}
	return nil, args.Error(1)
}

func (m *MockBasketRepository) DeleteBasket(ctx context.Context, id string) error {
	args := m.Called(ctx, id)
	return args.Error(0)
}

type BasketServerTestSuite struct {
	suite.Suite
	repo   *MockBasketRepository
	server basket_v1.BasketServiceServer
	ctx    context.Context
}

func (s *BasketServerTestSuite) SetupTest() {
	s.repo = new(MockBasketRepository)
	s.server = NewBasketServer(s.repo)
	s.ctx = context.Background()
}

func TestBasketServerTestSuite(t *testing.T) {
	suite.Run(t, new(BasketServerTestSuite))
}

func (s *BasketServerTestSuite) TestGetBasket_Success() {
	basket := &domain.Basket{
		TotalPrice: decimal.NewFromFloat(29.99),
		Items: []domain.BasketItem{
			{
				ProductID: "p1",
				Quantity:  2,
				ProductDetails: domain.BasketItemDetails{
					Name:     "Sushi Roll",
					Price:    decimal.NewFromFloat(15.00),
					ImageURL: "http://img/sushi.png",
					Link:     "http://link/sushi",
				},
			},
		},
	}

	s.repo.On("GetBasketByID", s.ctx, "basket-123").Return(basket, nil)

	resp, err := s.server.GetBasket(s.ctx, &basket_v1.GetBasketRequest{Id: "basket-123"})

	s.NoError(err)
	s.NotNil(resp)
	s.Equal("29.99", resp.TotalPrice)
	s.Len(resp.Items, 1)
	s.Equal("p1", resp.Items[0].ProductId)
	s.Equal("2", resp.Items[0].Quantity)
	s.Equal("Sushi Roll", resp.Items[0].Name)
	s.Equal("15", resp.Items[0].Price)
	s.Equal("http://img/sushi.png", resp.Items[0].ImageUrl)

	s.repo.AssertExpectations(s.T())
}

func (s *BasketServerTestSuite) TestGetBasket_NotFound() {
	s.repo.On("GetBasketByID", s.ctx, "missing").Return(nil, nil)

	resp, err := s.server.GetBasket(s.ctx, &basket_v1.GetBasketRequest{Id: "missing"})

	s.NoError(err)
	s.Nil(resp)

	s.repo.AssertExpectations(s.T())
}

func (s *BasketServerTestSuite) TestGetBasket_Error() {
	s.repo.On("GetBasketByID", s.ctx, "err-basket").Return(nil, errors.New("db error"))

	resp, err := s.server.GetBasket(s.ctx, &basket_v1.GetBasketRequest{Id: "err-basket"})

	s.Error(err)
	s.Nil(resp)
	s.EqualError(err, "db error")

	s.repo.AssertExpectations(s.T())
}
