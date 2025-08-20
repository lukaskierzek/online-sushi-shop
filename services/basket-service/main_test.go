package main

import (
	"bytes"
	"context"
	"encoding/json"
	"log/slog"
	"net/http"
	"net/http/httptest"
	"os"
	"testing"

	"github.com/alicebob/miniredis/v2"
	"github.com/gin-gonic/gin"
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/clients"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/db"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/assert"
	"github.com/stretchr/testify/require"
	"github.com/stretchr/testify/suite"
	"google.golang.org/grpc"
)

type TestSuite struct {
	suite.Suite
	p      *utils.ApplicationProperties
	r      *gin.Engine
	rdb    *redis.Client
	logger *slog.Logger
}

func (suite *TestSuite) SetupTest() {
	suite.logger = slog.New(slog.NewTextHandler(os.Stdout, nil))

	t := suite.T()
	t.Setenv("APP_ENV", "unit")

	p, err := utils.ResolveApplicationProperties(".")
	require.NoError(t, err)

	suite.p = p
	s := miniredis.RunT(t)
	p.DBUrl = s.Addr()

	suite.rdb = db.NewRedisClient(p)

	cr := repositories.NewCatalogRepository(suite.rdb, p)

	cc, err := clients.NewCatalogClient(createCatalogGrpcClientMock(), nil, cr)
	require.NoError(t, err)

	suite.r = gin.Default()
	createRouter(repositories.NewCartRepository(suite.rdb, p), cc, p, suite.r)
}

func TestSuiteRun(t *testing.T) {
	suite.Run(t, new(TestSuite))
}

func (suite *TestSuite) TestGetCart() {
	w := httptest.NewRecorder()
	req, err := http.NewRequest(http.MethodGet, "/api/v1/cart/", nil)
	require.NoError(suite.T(), err)

	req.Host = "localhost"
	suite.r.ServeHTTP(w, req)

	suite.assertCartResponse(w)
}

func (suite *TestSuite) TestPutCart() {
	t := suite.T()
	cartID := uuid.NewString()
	cart := models.Cart{ID: cartID, OwnerID: "", CartItems: []models.CartItem{}, TotalPrice: decimal.NewFromInt(0)}

	data, err := json.Marshal(cart)
	require.NoError(t, err)

	err = suite.rdb.Set(t.Context(), "carts::"+cartID, data, 0).Err()
	require.NoError(t, err)

	request := putCartRequest{Items: []putCartInput{
		{ProductID: uuid.NewString(), Quantity: 2},
		{ProductID: uuid.NewString(), Quantity: 3},
		{ProductID: uuid.NewString(), Quantity: 4},
		{ProductID: uuid.NewString(), Quantity: 5},
		{ProductID: uuid.NewString(), Quantity: 6},
	}}

	body, err := json.Marshal(request)
	require.NoError(t, err)

	w := httptest.NewRecorder()
	req, err := http.NewRequest(http.MethodPut, "/api/v1/cart/", bytes.NewBuffer(body))
	require.NoError(t, err)

	req.Host = "localhost"
	suite.r.ServeHTTP(w, req)

	suite.assertCartResponse(w)
}

func (suite *TestSuite) assertCartResponse(w *httptest.ResponseRecorder) {
	t := suite.T()
	t.Helper()

	assert.Equal(t, http.StatusOK, w.Code)

	cookies := w.Result().Cookies()
	require.NotEmpty(t, cookies)
	assert.Equal(t, "cart_id", cookies[0].Name)

	var cartResponse cartResponse
	err := json.Unmarshal(w.Body.Bytes(), &cartResponse)
	require.NoError(t, err)

	assert.NotNil(t, cartResponse)

	dbCart, err := utils.FromRedis[models.Cart](suite.rdb.Get(t.Context(), "carts::"+cartResponse.Cart.ID))
	require.NoError(t, err)

	suite.fetchCartItemsDetails(dbCart)

	assert.NotNil(t, dbCart)
	assert.Equal(t, cartResponse.Cart, *dbCart)
	assert.Equal(t, dbCart.ID, cookies[0].Value)
}

func (suite *TestSuite) fetchCartItemsDetails(cart *models.Cart) {
	t := suite.T()
	t.Helper()

	for i := range cart.CartItems {
		id := cart.CartItems[i].ProductID
		cart.CartItems[i].Details = models.ProductDetails{
			Name:     "Product name: " + id,
			Link:     "https://localhost:8080/products/" + id,
			ImageURL: "https://localhost:8080/products/images/" + id,
		}
	}
}

type cartResponse struct {
	Cart models.Cart `json:"cart"`
}

type putCartRequest struct {
	Items []putCartInput `json:"items" binding:"required"`
}

type putCartInput struct {
	ProductID string `json:"product_id" binding:"required"`
	Quantity  int32  `json:"quantity" binding:"required"`
}

type mockCatalogClient struct{}

func (m *mockCatalogClient) GetProductPrice(ctx context.Context, req *catalogpb.GetProductPriceRequest, opts ...grpc.CallOption) (*catalogpb.GetProductPriceResponse, error) {
	return &catalogpb.GetProductPriceResponse{
		Price: "999.99",
	}, nil
}

func (m *mockCatalogClient) GetProduct(ctx context.Context, req *catalogpb.GetProductRequest, opts ...grpc.CallOption) (*catalogpb.GetProductResponse, error) {
	return &catalogpb.GetProductResponse{
		Name:     "Product name: " + req.Id,
		Link:     "https://localhost:8080/products/" + req.Id,
		ImageUrl: "https://localhost:8080/products/images/" + req.Id,
	}, nil
}

func createCatalogGrpcClientMock() catalogpb.CatalogServiceClient {
	return &mockCatalogClient{}
}
