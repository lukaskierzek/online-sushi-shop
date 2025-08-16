package models

import "github.com/shopspring/decimal"

// Shopping cart item info
// @Description Shopping cart item information
type CartItem struct {
	ID        string          `json:"id"`
	ProductID string          `json:"product_id"`
	UnitPrice decimal.Decimal `json:"price"`
	Quantity  int             `json:"quantity"`
	Details   ProductDetails  `json:"details"`
}

// Shopping cart info
// @Description Shopping cart information
type Cart struct {
	ID         string          `json:"id"`
	OwnerID    string          `json:"owner_id"`
	CartItems  []CartItem      `json:"cart_items"`
	TotalPrice decimal.Decimal `json:"total_price"`
}

// Shopping cart item product details
// @Description Shopping cart item product details
type ProductDetails struct {
	Name     string `json:"name"`
	Link     string `json:"link"`
	ImageURL string `json:"image_url"`
}
