package domain

import (
	"errors"
	"sync"
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
	mu           sync.Mutex
}

type BasketItemDetails struct {
	Name     string
	ImageURL string
	Link     string
	Price    decimal.Decimal
}

func (b *Basket) AddItem(item BasketItem) error {
	b.mu.Lock()
	defer b.mu.Unlock()

	if item.Quantity <= 0 {
		return errors.New("quantity must be positive")
	}
	for i := range b.Items {
		if b.Items[i].ProductID == item.ProductID {
			b.Items[i].Quantity += item.Quantity
			b.recalculateTotal()
			return nil
		}
	}
	b.Items = append(b.Items, item)
	b.recalculateTotal()
	return nil
}

func (b *Basket) AddItemDetails(itemID string, details BasketItemDetails) error {
	b.mu.Lock()
	defer b.mu.Unlock()

	if details.Price.LessThanOrEqual(decimal.Zero) {
		return errors.New("product price cannot be nil")
	}

	if details.Name == "" {
		return errors.New("product name cannot be empty")
	}

	if details.ImageURL == "" {
		return errors.New("product image URL cannot be empty")
	}

	if details.Link == "" {
		return errors.New("product link cannot be empty")
	}

	for i := range b.Items {
		if b.Items[i].ProductID == itemID {
			b.Items[i].ProductDetails = details
			b.recalculateTotal()
			return nil
		}
	}

	return errors.New("item not found in basket")
}

func (b *Basket) RemoveItem(productID string) error {
	b.mu.Lock()
	defer b.mu.Unlock()

	for i := range b.Items {
		if b.Items[i].ProductID == productID {
			b.Items = append(b.Items[:i], b.Items[i+1:]...)
			b.recalculateTotal()
			return nil
		}
	}
	return errors.New("item not found")
}

func (b *Basket) ChangeItemQuantity(productID string, qty int32) error {
	b.mu.Lock()
	defer b.mu.Unlock()

	if qty < 0 {
		return errors.New("quantity cannot be negative")
	}
	for i := range b.Items {
		if b.Items[i].ProductID == productID {
			if qty == 0 {
				b.Items = append(b.Items[:i], b.Items[i+1:]...)
			} else {
				b.Items[i].Quantity = qty
			}
			b.recalculateTotal()
			return nil
		}
	}
	return errors.New("item not found")
}

func (b *Basket) Clear() {
	b.mu.Lock()
	defer b.mu.Unlock()

	b.Items = []BasketItem{}
	b.TotalPrice = decimal.NewFromInt(0)
}

func (b *Basket) Complete() {
	b.mu.Lock()
	defer b.mu.Unlock()

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
