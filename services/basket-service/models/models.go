package models

import "github.com/shopspring/decimal"

// Shopping cart item info
// @Description Shopping cart item information
type CartItem struct {
	ID        string          `json:"id"`
	ProductID string          `json:"product_id"`
	UnitPrice decimal.Decimal `json:"price"`
	Quantity  int32           `json:"quantity"`
}

// Shopping cart info
// @Description Shopping cart information
type Cart struct {
	ID         string          `json:"id"`
	OwnerID    string          `json:"owner_id"`
	CartItems  []CartItem      `json:"cart_items"`
	TotalPrice decimal.Decimal `json:"total_price"`
}

// Error response
// @Description Error response
type ErrorResponse struct {
	Key   string `json:"key"`
	Value string `json:"value"`
}
