package domain

import (
	"time"

	"github.com/shopspring/decimal"
)

type BasketItem struct {
	ProductID      string
	Quantity       int32
	ProductDetails BasketItemDetails
}

type Basket struct {
	ID           string
	Items        []BasketItem
	TotalPrice   decimal.Decimal
	CompleteDate *time.Time
}

type BasketItemDetails struct {
	Name     string
	ImageURL string
	Link     string
	Price    decimal.Decimal
}

func (b *Basket) AddItem(item BasketItem) {
	for i := range b.Items {
		if b.Items[i].ProductID == item.ProductID {
			b.Items[i].Quantity += item.Quantity
			b.recalculateTotal()
			return
		}
	}
	b.Items = append(b.Items, item)
	b.recalculateTotal()
}

func (b *Basket) RemoveItem(productID string) {
	for i := range b.Items {
		if b.Items[i].ProductID == productID {
			b.Items = append(b.Items[:i], b.Items[i+1:]...)
			b.recalculateTotal()
			return
		}
	}
}

func (b *Basket) ChangeItemQuantity(productID string, qty int32) {
	for i := range b.Items {
		if b.Items[i].ProductID == productID {
			if qty <= 0 {
				b.RemoveItem(productID)
			} else {
				b.Items[i].Quantity = qty
			}
			b.recalculateTotal()
			return
		}
	}
}

func (b *Basket) Clear() {
	b.Items = []BasketItem{}
	b.TotalPrice = decimal.NewFromInt(0)
}

func (b *Basket) Complete() {
	now := time.Now()
	b.CompleteDate = &now
}

func (b *Basket) recalculateTotal() {
	total := decimal.NewFromInt(0)
	for _, item := range b.Items {
		total = total.Add(item.ProductDetails.Price.Mul(decimal.NewFromInt(int64(item.Quantity))))
	}
	b.TotalPrice = total
}
