package pl.lukaskierzek.sushi.shop.service.basket.service;

import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import pl.lukaskierzek.sushi.shop.service.*;
import pl.lukaskierzek.sushi.shop.service.ProductServiceGrpc.ProductServiceImplBase;

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

@TestConfiguration
class GrpcTestConfiguration {

    public static final String IN_PROCESS_SERVER_NAME = "test-grpc";

    @Bean(initMethod = "start", destroyMethod = "shutdownNow")
    Server grpcTestServer(TestProductGrpcService service) {
        return InProcessServerBuilder
            .forName(IN_PROCESS_SERVER_NAME)
            .addService(service)
            .directExecutor()
            .build();
    }

    @Bean
    ManagedChannel grpcTestChannel() {
        return InProcessChannelBuilder
            .forName(IN_PROCESS_SERVER_NAME)
            .directExecutor()
            .build();
    }

    @Bean
    TestProductGrpcService testProductGrpcService() {
        return new TestProductGrpcService();
    }

    @Bean
    ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub(ManagedChannel channel) {
        return ProductServiceGrpc.newBlockingStub(channel);
    }
}
