package clients

import (
	"context"

	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/models"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/repositories"
	"github.com/shopspring/decimal"
	"google.golang.org/grpc"
)

type CatalogClient struct {
	conn *grpc.ClientConn
	csc  catalogpb.CatalogServiceClient
	cr   *repositories.CatalogRepository
}

func NewCatalogClient(csc catalogpb.CatalogServiceClient, conn *grpc.ClientConn, cr *repositories.CatalogRepository) (*CatalogClient, error) {
	return &CatalogClient{
		conn: conn,
		csc:  csc,
		cr:   cr,
	}, nil
}

func (c *CatalogClient) Close() error {
	if c.conn == nil {
		return nil
	}
	return c.conn.Close()
}

func (cc *CatalogClient) GetProductPrice(id string, ctx context.Context) (*decimal.Decimal, error) {
	p, err := cc.cr.GetProductPrice(id, ctx)
	if err != nil {
		return nil, err
	}

	if p != nil {
		return p, nil
	}

	resp, err := cc.csc.GetProductPrice(ctx, &catalogpb.GetProductPriceRequest{
		Id: id,
	})

	if err != nil {
		return nil, err
	}

	price, err := decimal.NewFromString(resp.Price)
	if err != nil {
		return nil, err
	}

	if err := cc.cr.SaveProductPrice(id, price, ctx); err != nil {
		return nil, err
	}

	return &price, nil
}

func (cc *CatalogClient) GetProduct(id string, ctx context.Context) (*models.ProductDetails, error) {
	p, err := cc.cr.GetProduct(id, ctx)
	if err != nil {
		return nil, err
	}

	if p != nil {
		return p, nil
	}

	resp, err := cc.csc.GetProduct(ctx, &catalogpb.GetProductRequest{
		Id: id,
	})

	if err != nil {
		return nil, err
	}

	product := models.ProductDetails{
		Name:     resp.Name,
		Link:     resp.Link,
		ImageURL: resp.ImageUrl,
	}

	if cc.cr.SaveProduct(id, product, ctx) != nil {
		return nil, err
	}

	return &product, nil
}
