package grpc

import (
	"context"
	"strconv"

	"github.com/kamilszymanski707/online-sushi-shop/basket-service/infra"
	basket_v1 "github.com/kamilszymanski707/proto-lib/basket.v1"
)

type basketServer struct {
	br infra.BasketRepository
	basket_v1.UnimplementedBasketServiceServer
}

func NewBasketServer(br infra.BasketRepository) basket_v1.BasketServiceServer {
	return &basketServer{br: br}
}

func (s *basketServer) GetBasket(ctx context.Context, in *basket_v1.GetBasketRequest) (*basket_v1.GetBasketResponse, error) {
	basket, err := s.br.GetBasketByID(ctx, in.GetId())
	if err != nil {
		return nil, err
	}

	if basket != nil {
		items := make([]*basket_v1.BasketItem, 0, len(basket.Items))
		for _, item := range basket.Items {
			items = append(items, &basket_v1.BasketItem{
				ProductId: item.ProductID,
				Quantity:  strconv.Itoa(item.Quantity),
				Name:      item.ProductDetails.Name,
				Price:     item.ProductDetails.Price.String(),
				ImageUrl:  item.ProductDetails.ImageURL,
			})
		}

		return &basket_v1.GetBasketResponse{
			TotalPrice: basket.TotalPrice.String(),
			Items:      items,
		}, nil
	}

	return nil, nil
}
