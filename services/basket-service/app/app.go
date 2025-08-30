package app

import (
	"context"

	"github.com/kamilszymanski707/online-sushi-shop/basket-service/domain"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/infra"
)

type BasketService struct {
	brepo infra.BasketRepository
	prepo infra.ProductRepository
}

func NewBasketService(brepo infra.BasketRepository, prepo infra.ProductRepository) *BasketService {
	return &BasketService{brepo: brepo, prepo: prepo}
}

func (s *BasketService) AddItem(ctx context.Context, b *domain.Basket, item domain.BasketItem) (*domain.Basket, error) {
	details, err := s.prepo.GetProductDetails(ctx, item.ProductID)
	if err != nil {
		return nil, err
	}

	if err := b.AddItemDetails(item.ProductID, *details); err != nil {
		return nil, err
	}

	if err := b.AddItem(item); err != nil {
		return nil, err
	}

	if err := s.brepo.SaveBasket(ctx, b); err != nil {
		return nil, err
	}

	return b, nil
}

func (s *BasketService) RemoveItem(ctx context.Context, b *domain.Basket, productID string) (*domain.Basket, error) {
	b.RemoveItem(productID)
	if err := s.brepo.SaveBasket(ctx, b); err != nil {
		return nil, err
	}
	return b, nil
}

func (s *BasketService) ChangeQuantity(ctx context.Context, b *domain.Basket, productID string, qty int32) (*domain.Basket, error) {
	b.ChangeItemQuantity(productID, qty)
	if err := s.brepo.SaveBasket(ctx, b); err != nil {
		return nil, err
	}
	return b, nil
}

func (s *BasketService) Clear(ctx context.Context, b *domain.Basket) (*domain.Basket, error) {
	b.Clear()
	if err := s.brepo.SaveBasket(ctx, b); err != nil {
		return nil, err
	}
	return b, nil
}

func (s *BasketService) Complete(ctx context.Context, b *domain.Basket) (*domain.Basket, error) {
	b.Complete()
	if err := s.brepo.SaveBasket(ctx, b); err != nil {
		return nil, err
	}
	return b, nil
}
