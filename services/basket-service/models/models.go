package models

import "github.com/shopspring/decimal"

// Shopping Cart Item
type CartItem struct {
	ID        string          `json:"id"`
	ProductID string          `json:"product_id"`
	UnitPrice decimal.Decimal `json:"price"`
	Quantity  int32           `json:"quantity"`
}

// Shopping Cart
type Cart struct {
	ID         string          `json:"id"`
	OwnerID    string          `json:"owner_id"`
	CartItems  []CartItem      `json:"cart_items"`
	TotalPrice decimal.Decimal `json:"total_price"`
}
