package main

import (
	"bytes"
	"context"
	"encoding/json"
	"log"
	"net/http"
	"net/http/httptest"
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
	"google.golang.org/grpc"
)

type testContext struct {
	c   *gin.Context
	r   *gin.Engine
	s   *miniredis.Miniredis
	p   *utils.ApplicationProperties
	rdb *redis.Client
	cr  *repositories.CatalogRepository
	cc  *clients.CatalogClient
	w   *httptest.ResponseRecorder
}

func createTestContext(t *testing.T) (*testContext, error) {
	w := httptest.NewRecorder()
	c, r := gin.CreateTestContext(w)

	t.Setenv("APP_ENV", "unit")
	p := utils.ResolveApplicationProperties(".")

	s := miniredis.RunT(t)
	p.DBUrl = s.Addr()

	rdb := db.NewRedisClient(p)

	cr := repositories.NewCatalogRepository(rdb, p)

	cc, err := clients.NewCatalogClient(createCatalogGrpcClientMock(), nil, cr)
	if err != nil {
		return nil, err
	}

	createRouter(repositories.NewCartRepository(rdb, p), cc, r)

	return &testContext{
		c:   c,
		r:   r,
		s:   s,
		p:   p,
		rdb: rdb,
		cr:  cr,
		cc:  cc,
		w:   w,
	}, nil
}

func TestGetCart(t *testing.T) {
	ctx, err := createTestContext(t)
	if err != nil {
		log.Fatal(err)
	}

	defer ctx.s.Close()
	defer ctx.rdb.Close()
	defer ctx.cc.Close()

	ctx.c.Request, err = http.NewRequest(http.MethodGet, "/api/v1/cart/", bytes.NewBuffer([]byte("{}")))
	if err != nil {
		log.Fatal(err)
	}

	ctx.c.Request.Host = "localhost"
	ctx.r.ServeHTTP(ctx.w, ctx.c.Request)

	assertCartResponse(t, ctx.w, ctx.rdb)
}

func TestPutCart(t *testing.T) {
	ctx, err := createTestContext(t)
	if err != nil {
		log.Fatal(err)
	}

	defer ctx.s.Close()
	defer ctx.rdb.Close()
	defer ctx.cc.Close()

	cartID := uuid.NewString()
	cart := models.Cart{ID: cartID, OwnerID: "", CartItems: []models.CartItem{}, TotalPrice: decimal.NewFromInt(0)}

	data, err := json.Marshal(cart)
	if err != nil {
		log.Fatal(err)
	}

	err = ctx.rdb.Set(t.Context(), "carts::"+cartID, data, 0).Err()
	if err != nil {
		log.Fatal(err)
	}

	request := putCartRequest{Items: []putCartInput{
		{
			ProductID: uuid.NewString(),
			Quantity:  2,
		},
		{
			ProductID: uuid.NewString(),
			Quantity:  3,
		},
		{
			ProductID: uuid.NewString(),
			Quantity:  4,
		},
		{
			ProductID: uuid.NewString(),
			Quantity:  5,
		},
		{
			ProductID: uuid.NewString(),
			Quantity:  6,
		},
	}}

	body, _ := json.Marshal(request)
	ctx.c.Request, err = http.NewRequest(http.MethodPut, "/api/v1/cart/", bytes.NewBuffer(body))
	if err != nil {
		log.Fatal(err)
	}

	ctx.c.Request.Host = "localhost"
	ctx.r.ServeHTTP(ctx.w, ctx.c.Request)

	assertCartResponse(t, ctx.w, ctx.rdb)
}

func assertCartResponse(t *testing.T, w *httptest.ResponseRecorder, rdb *redis.Client) {
	assert.Equal(t, http.StatusOK, w.Code)

	cookies := w.Result().Cookies()
	assert.Equal(t, "cart_id", cookies[0].Name)

	var cartResponse cartResponse
	err := json.Unmarshal(w.Body.Bytes(), &cartResponse)
	if err != nil {
		log.Fatal(err)
	}

	assert.NotNil(t, cartResponse)

	jsonData, err := rdb.Get(t.Context(), "carts::"+cartResponse.Cart.ID).Result()
	if err == redis.Nil {
		log.Fatal(err)
	}
	if err != nil {
		log.Fatal(err)
	}

	var dbCart models.Cart
	if err := json.Unmarshal([]byte(jsonData), &dbCart); err != nil {
		log.Fatal(err)
	}

	fetchCartItemsDetails(dbCart)

	assert.NotNil(t, dbCart)
	assert.Equal(t, cartResponse.Cart, dbCart)
	assert.Equal(t, dbCart.ID, cookies[0].Value)
}

func fetchCartItemsDetails(cart models.Cart) {
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
