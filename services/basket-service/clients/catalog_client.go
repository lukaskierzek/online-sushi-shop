package clients

import (
	"context"

	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/shopspring/decimal"
)

type CatalogClient struct {
	catalogClient catalogpb.CatalogServiceClient
}

func NewCatalogClient(catalogClient catalogpb.CatalogServiceClient) *CatalogClient {
	return &CatalogClient{catalogClient: catalogClient}
}

func (client *CatalogClient) GetProductPrice(id string, ctx context.Context) (decimal.Decimal, error) {
	resp, err := client.catalogClient.GetProductPrice(ctx, &catalogpb.GetProductPriceRequest{
		Id: id,
	})

	if err != nil {
		return decimal.Decimal{}, err
	}

	price, err := decimal.NewFromString(resp.Price)
	if err != nil {
		return decimal.Decimal{}, err
	}

	return price, nil
}

func (client *CatalogClient) GetProduct(id string, ctx context.Context) (models.ProductDetails, error) {
	resp, err := client.catalogClient.GetProduct(ctx, &catalogpb.GetProductRequest{
		Id: id,
	})

	if err != nil {
		return models.ProductDetails{}, err
	}

	return models.ProductDetails{
		Name:     resp.Name,
		Link:     resp.Link,
		ImageURL: resp.ImageUrl,
	}, nil
}
