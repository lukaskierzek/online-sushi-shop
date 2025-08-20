package clients

import (
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/gRPC/catalogpb"
	"github.com/kamilszymanski707/online-sushi-shop/basket-service/utils"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

func NewCatalogServiceClient(p *utils.ApplicationProperties) (catalogpb.CatalogServiceClient, *grpc.ClientConn, error) {
	conn, err := grpc.NewClient(
		p.CatalogGrpcTarget,
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)

	if err != nil {
		return nil, nil, err
	}

	csc := catalogpb.NewCatalogServiceClient(conn)
	return csc, conn, nil
}
