package handlers

import (
	"bytes"
	"context"
	"encoding/json"
	"net/http"
	"net/http/httptest"
	"testing"

	"github.com/alicebob/miniredis/v2"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/app"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/infra"
	"github.com/redis/go-redis/v9"
	"github.com/stretchr/testify/mock"
	"github.com/stretchr/testify/suite"
	"google.golang.org/grpc"

	catalog_v1 "github.com/kamilszymanski707/proto-lib/catalog.v1"
)

type MockCatalogServiceClient struct {
	mock.Mock
}

func (m *MockCatalogServiceClient) GetProduct(ctx context.Context, in *catalog_v1.GetProductRequest, opts ...grpc.CallOption) (*catalog_v1.GetProductResponse, error) {
	args := m.Called(ctx, in)
	return args.Get(0).(*catalog_v1.GetProductResponse), args.Error(1)
}

type HandlerIntegrationSuite struct {
	suite.Suite
	server *miniredis.Miniredis
	client *redis.Client
	gRPC   *MockCatalogServiceClient
	router *gin.Engine
	brepo  infra.BasketRepository
	prepo  infra.ProductRepository
	svc    *app.BasketService
	basket *domain.Basket
}

func (suite *HandlerIntegrationSuite) SetupSuite() {
	gin.SetMode(gin.TestMode)
	suite.server = miniredis.RunT(suite.T())
	suite.client = redis.NewClient(&redis.Options{Addr: suite.server.Addr()})
	suite.gRPC = new(MockCatalogServiceClient)
	suite.brepo = infra.NewBasketRepository(suite.client)
	suite.prepo = infra.NewProductRepository(suite.client, suite.gRPC)
	suite.svc = app.NewBasketService(suite.brepo, suite.prepo)
	suite.router = gin.New()
	handler := NewBasketHandler(suite.svc)

	suite.router.GET("/basket/", handler.GetBasket)
	suite.router.POST("/basket/items", handler.AddItem)
	suite.router.DELETE("/basket/items/:productID", handler.RemoveItem)
	suite.router.PUT("/basket/items/:productID", handler.ChangeQuantity)
	suite.router.DELETE("/basket/clear", handler.Clear)
}

func (suite *HandlerIntegrationSuite) SetupTest() {
	suite.basket = &domain.Basket{ID: uuid.New().String()}
}

func (suite *HandlerIntegrationSuite) injectBasket(c *gin.Context) {
	c.Set("cart", suite.basket)
}

func (suite *HandlerIntegrationSuite) TestGetBasket() {
	req, _ := http.NewRequest("GET", "/basket/", nil)
	w := httptest.NewRecorder()
	c, _ := gin.CreateTestContext(w)
	c.Request = req
	suite.injectBasket(c)

	handler := NewBasketHandler(suite.svc)
	handler.GetBasket(c)

	suite.Equal(http.StatusOK, w.Code)
	var resp domain.Basket
	json.NewDecoder(w.Body).Decode(&resp)
	suite.Equal(suite.basket.ID, resp.ID)
}

func (suite *HandlerIntegrationSuite) TestRemoveItem() {
	suite.basket.Items = append(suite.basket.Items, domain.BasketItem{ProductID: "prod1", Quantity: 1})

	req, _ := http.NewRequest("DELETE", "/basket/items/prod1", nil)
	w := httptest.NewRecorder()
	c, _ := gin.CreateTestContext(w)
	c.Params = gin.Params{{Key: "productID", Value: "prod1"}}
	c.Request = req
	suite.injectBasket(c)

	handler := NewBasketHandler(suite.svc)
	handler.RemoveItem(c)

	suite.Equal(http.StatusOK, w.Code)
	var resp domain.Basket
	json.NewDecoder(w.Body).Decode(&resp)
	suite.Equal(0, len(resp.Items))
}

func (suite *HandlerIntegrationSuite) TestChangeQuantity() {
	suite.basket.Items = append(suite.basket.Items, domain.BasketItem{ProductID: "prod1", Quantity: 1})

	body := changeQuantityRequest{Quantity: 5}
	jsonBody, _ := json.Marshal(body)
	req, _ := http.NewRequest("PUT", "/basket/items/prod1", bytes.NewBuffer(jsonBody))
	req.Header.Set("Content-Type", "application/json")
	w := httptest.NewRecorder()
	c, _ := gin.CreateTestContext(w)
	c.Params = gin.Params{{Key: "productID", Value: "prod1"}}
	c.Request = req
	suite.injectBasket(c)

	handler := NewBasketHandler(suite.svc)
	handler.ChangeQuantity(c)

	suite.Equal(http.StatusOK, w.Code)
	var resp domain.Basket
	json.NewDecoder(w.Body).Decode(&resp)
	suite.Equal(int32(5), resp.Items[0].Quantity)
}

func (suite *HandlerIntegrationSuite) TestClear() {
	suite.basket.Items = append(suite.basket.Items, domain.BasketItem{ProductID: "prod1", Quantity: 1})

	req, _ := http.NewRequest("DELETE", "/basket/clear", nil)
	w := httptest.NewRecorder()
	c, _ := gin.CreateTestContext(w)
	c.Request = req
	suite.injectBasket(c)

	handler := NewBasketHandler(suite.svc)
	handler.Clear(c)

	suite.Equal(http.StatusOK, w.Code)
	var resp domain.Basket
	json.NewDecoder(w.Body).Decode(&resp)
	suite.Equal(0, len(resp.Items))
}

func (suite *HandlerIntegrationSuite) TestAddItem() {
	suite.gRPC.On("GetProduct", mock.Anything, mock.Anything).Return(&catalog_v1.GetProductResponse{
		Name:     "Sushi",
		ImageUrl: "img.jpg",
		Link:     "link",
		Price:    "10",
	}, nil)

	body := addItemRequest{ProductID: "prod1", Quantity: 2}
	jsonBody, _ := json.Marshal(body)
	req, _ := http.NewRequest("POST", "/basket/items", bytes.NewBuffer(jsonBody))
	req.Header.Set("Content-Type", "application/json")

	w := httptest.NewRecorder()
	c, _ := gin.CreateTestContext(w)
	c.Request = req
	suite.injectBasket(c)

	handler := NewBasketHandler(suite.svc)
	handler.AddItem(c)

	suite.Equal(http.StatusOK, w.Code)
	var resp domain.Basket
	json.NewDecoder(w.Body).Decode(&resp)
	suite.Equal("Sushi", resp.Items[0].ProductDetails.Name)
}

func TestHandlerIntegrationSuite(t *testing.T) {
	suite.Run(t, new(HandlerIntegrationSuite))
}
