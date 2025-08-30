package infra

import (
	"context"
	"encoding/json"
	"testing"
	"time"

	"github.com/alicebob/miniredis/v2"
	"github.com/redis/go-redis/v9"
	"google.golang.org/grpc"

	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
	"github.com/shopspring/decimal"

	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"

	catalog_v1 "github.com/kamilszymanski707/proto-lib/catalog.v1"
)

type InfraTestSuite struct {
	suite.Suite
	server *miniredis.Miniredis
	client *redis.Client
	repo   BasketRepository
}

func (suite *InfraTestSuite) SetupSuite() {
	suite.server = miniredis.RunT(suite.T())
	suite.client = redis.NewClient(&redis.Options{
		Addr: suite.server.Addr(),
	})
	suite.repo = NewBasketRepository(suite.client, 3600)
}

func (suite *InfraTestSuite) TearDownSuite() {
	suite.client.Close()
	suite.server.Close()
}

func (suite *InfraTestSuite) TestCreateEmptyBasket() {
	ctx := context.Background()
	basket, err := suite.repo.CreateEmptyBasket(ctx)
	suite.NoError(err)
	suite.NotEmpty(basket.ID)
	suite.Equal(0, len(basket.Items))
	suite.True(basket.TotalPrice.IsZero())
}

func (suite *InfraTestSuite) TestSaveAndGetBasket() {
	ctx := context.Background()
	basket := &domain.Basket{
		ID:         uuid.New().String(),
		Items:      []domain.BasketItem{{ProductID: "prod1", Quantity: 2}},
		TotalPrice: decimal.NewFromInt(20),
	}
	err := suite.repo.SaveBasket(ctx, basket)
	suite.NoError(err)

	got, err := suite.repo.GetBasketByID(ctx, basket.ID)
	suite.NoError(err)
	suite.NotNil(got)
	suite.Equal(basket.ID, got.ID)
	suite.Equal(int32(2), got.Items[0].Quantity)
}

func (suite *InfraTestSuite) TestGetBasketByID_NotFound() {
	ctx := context.Background()
	got, err := suite.repo.GetBasketByID(ctx, "nonexistent")
	suite.NoError(err)
	suite.Nil(got)
}

func (suite *InfraTestSuite) TestDeleteBasket() {
	ctx := context.Background()
	basket, _ := suite.repo.CreateEmptyBasket(ctx)
	err := suite.repo.DeleteBasket(ctx, basket.ID)
	suite.NoError(err)

	got, err := suite.repo.GetBasketByID(ctx, basket.ID)
	suite.NoError(err)
	suite.Nil(got)
}

func (suite *InfraTestSuite) TestProductRepository_GetProductDetails_FromGRPC() {
	ctx := context.Background()
	mockClient := new(MockCatalogServiceClient)

	expectedDetails := &domain.BasketItemDetails{
		Name:     "Mock Sushi",
		ImageURL: "mock.jpg",
		Link:     "mocklink",
		Price:    decimal.NewFromInt(15),
	}
	expectedResp := &catalog_v1.GetProductResponse{
		Name:     expectedDetails.Name,
		ImageUrl: expectedDetails.ImageURL,
		Link:     expectedDetails.Link,
		Price:    expectedDetails.Price.String(),
	}

	mockClient.On("GetProduct", ctx, mock.Anything).Return(expectedResp, nil)

	repo := &productRepositoryImpl{db: suite.client, cc: mockClient}
	got, err := repo.GetProductDetails(ctx, "prod1")
	suite.NoError(err)
	suite.NotNil(got)
	suite.Equal("Mock Sushi", got.Name)

	mockClient.AssertExpectations(suite.T())
}

func (suite *InfraTestSuite) TestProductRepository_GetProductDetails_FromRedis() {
	ctx := context.Background()
	details := &domain.BasketItemDetails{
		Name:     "Sushi",
		ImageURL: "img.jpg",
		Link:     "link",
		Price:    decimal.NewFromInt(10),
	}
	data, _ := json.Marshal(details)
	suite.client.Set(ctx, productPrefix+"prod1", data, time.Hour)

	repo := &productRepositoryImpl{db: suite.client}
	got, err := repo.GetProductDetails(ctx, "prod1")
	suite.NoError(err)
	suite.NotNil(got)
	suite.Equal("Sushi", got.Name)
}

func TestInfraTestSuite(t *testing.T) {
	suite.Run(t, new(InfraTestSuite))
}

type MockCatalogServiceClient struct {
	mock.Mock
}

func (m *MockCatalogServiceClient) GetProduct(ctx context.Context, in *catalog_v1.GetProductRequest, opts ...grpc.CallOption) (*catalog_v1.GetProductResponse, error) {
	args := m.Called(ctx, in)
	return args.Get(0).(*catalog_v1.GetProductResponse), args.Error(1)
}
