package pl.lukaskierzek.sushi.shop.service.basket.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.lukaskierzek.sushi.shop.service.*;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceImplBase;

@GrpcService
class TestProductGrpcService extends ProductServiceImplBase {

    @Override
    public void getProduct(GetProductRequest request, StreamObserver<GetProductResponse> responseObserver) {
        responseObserver.onNext(GetProductResponse.newBuilder()
            .setPrice(Money.newBuilder()
                .setAmount("2137")
                .setCurrency(Currency.PLN)
                .build())
            .build());
        responseObserver.onCompleted();
    }
}

@ImportAutoConfiguration({
    GrpcServerAutoConfiguration.class,
    GrpcServerFactoryAutoConfiguration.class,
    GrpcClientAutoConfiguration.class})
@TestConfiguration
class GrpcTestConfiguration {

    @Bean
    TestProductGrpcService testProductGrpcService() {
        return new TestProductGrpcService();
    }
}
