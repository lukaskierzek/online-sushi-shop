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
	"github.com/google/uuid"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/redis/go-redis/v9"
	"github.com/shopspring/decimal"
	"github.com/stretchr/testify/assert"
	"google.golang.org/grpc"
)

func TestGetCart(t *testing.T) {
	s := miniredis.RunT(t)
	defer s.Close()

	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	router := createRouter(rdb, nil)

	w := httptest.NewRecorder()
	req, _ := http.NewRequest("GET", "/api/v1/cart/", nil)
	req.RemoteAddr = "127.0.0.1:8080"
	router.ServeHTTP(w, req)

	assertCartResponse(t, w, rdb)
}

func TestPutCart(t *testing.T) {
	s := miniredis.RunT(t)
	defer s.Close()

	rdb := redis.NewClient(&redis.Options{Addr: s.Addr(), DB: 0})
	defer rdb.Close()

	cartID := uuid.NewString()
	cart := models.Cart{ID: cartID, OwnerID: "", CartItems: []models.CartItem{}, TotalPrice: decimal.NewFromInt(0)}

	data, err := json.Marshal(cart)
	if err != nil {
		log.Fatal(err)
	}

	err = rdb.Set(t.Context(), "carts::"+cartID, data, 0).Err()
	if err != nil {
		log.Fatal(err)
	}

	router := createRouter(rdb, createCatalogGrpcClientMock())

	w := httptest.NewRecorder()

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
	req, _ := http.NewRequest("PUT", "/api/v1/cart/", bytes.NewBuffer(body))
	req.Header.Set("Content-Type", "application/json")
	req.RemoteAddr = "127.0.0.1:8080"

	router.ServeHTTP(w, req)

	assertCartResponse(t, w, rdb)
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

	assert.NotNil(t, dbCart)
	assert.Equal(t, cartResponse.Cart, dbCart)
	assert.Equal(t, dbCart.ID, cookies[0].Value)
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

func (m *mockCatalogClient) GetProduct(ctx context.Context, req *catalogpb.GetProductRequest, opts ...grpc.CallOption) (*catalogpb.GetProductResponse, error) {
	return &catalogpb.GetProductResponse{
		Price: "999.99",
	}, nil
}

func createCatalogGrpcClientMock() catalogpb.CatalogServiceClient {
	return &mockCatalogClient{}
}
