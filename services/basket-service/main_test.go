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
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/catalogpb"
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
	router.ServeHTTP(w, req)

	assertCartResponse(t, w, rdb, true)
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
	req.AddCookie(&http.Cookie{Name: "cart_id", Value: cart.ID})
	router.ServeHTTP(w, req)

	assertCartResponse(t, w, rdb, false)
}

func assertCartResponse(t *testing.T, w *httptest.ResponseRecorder, rdb *redis.Client, cartIDCookieRequired bool) {
	assert.Equal(t, http.StatusOK, w.Code)

	var cartIDCookie string

	if cartIDCookieRequired {
		cookies := w.Result().Cookies()
		for _, cookie := range cookies {
			if cookie.Name == "cart_id" {
				cartIDCookie = cookie.Value
			}
		}

		assert.NotEqual(t, "", cartIDCookie)
	}

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

	if cartIDCookieRequired {
		assert.Equal(t, cartIDCookie, dbCart.ID)
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

func (m *mockCatalogClient) GetProduct(ctx context.Context, req *catalogpb.GetProductRequest, opts ...grpc.CallOption) (*catalogpb.GetProductResponse, error) {
	return &catalogpb.GetProductResponse{
		Price: "999.99",
	}, nil
}

func createCatalogGrpcClientMock() catalogpb.CatalogServiceClient {
	return &mockCatalogClient{}
}
