package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import pl.lukaskierzek.sushi.shop.service.catalog.application.CatalogServiceApplication;
import pl.lukaskierzek.sushi.shop.service.catalog.infrastructure.product.ProductEntity;
import pl.lukaskierzek.sushi.shop.service.catalog.infrastructure.product.ProductJpaRepository;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static pl.lukaskierzek.sushi.shop.service.catalog.api.product.ProductFixture.EXPECTED_VALIDATION_ERROR_RESULT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = CatalogServiceApplication.class)
class ProductControllerTests {

    static final String BASE_URL = "/products";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProductJpaRepository productRepository;

    @BeforeEach
    void beforeEach() {
        productRepository.deleteAll();
    }

    @Test
    void should_create_product_and_return_201() {
        // given

        var givenRequest = new ProductRequest("Valid Name", "A valid description longer than 10");

        var givenHeaders = new HttpHeaders();
        givenHeaders.setContentType(APPLICATION_JSON);
        var givenHttpEntity = new HttpEntity<>(givenRequest, givenHeaders);

        // when

        var result = restTemplate.postForEntity(BASE_URL, givenHttpEntity, ProductIdResponse.class);

        // then

        assertThat(result.getStatusCode()).isEqualTo(CREATED);
        assertNotNull(result.getBody());
        assertThat(result.getBody().id()).isNotNull();

        var maybeActualProduct = productRepository.findById(result.getBody().id());
        Assertions.assertTrue(maybeActualProduct.isPresent());

        var actualProduct = maybeActualProduct.get();
        assertThat(actualProduct.getName()).isEqualTo(givenRequest.name());
        assertThat(actualProduct.getDescription()).isEqualTo(givenRequest.description());
    }

    @Test
    void should_not_create_product_and_return_400() throws JSONException {
        // given

        var givenRequest = new ProductRequest("x", "short");

        var givenHeaders = new HttpHeaders();
        givenHeaders.setContentType(APPLICATION_JSON);
        var givenHttpEntity = new HttpEntity<>(givenRequest, givenHeaders);

        // when

        var result = restTemplate.postForEntity(BASE_URL, givenHttpEntity, String.class);

        // then

        assertThat(result.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertEquals(EXPECTED_VALIDATION_ERROR_RESULT, result.getBody(), false);
    }

    @Test
    void should_get_product_details_by_id_and_return_200() {
        // given

        var givenProduct = productRepository.save(ProductEntity.builder()
                .name("Name")
                .description("Description")
                .build());

        // when

        var result = restTemplate.getForEntity(BASE_URL + "/" + givenProduct.getId(), ProductDetailsResponse.class);

        // then

        assertThat(result.getStatusCode()).isEqualTo(OK);
        assertNotNull(result.getBody());

        assertThat(result.getBody().id()).isEqualTo(givenProduct.getId());
        assertThat(result.getBody().name()).isEqualTo(givenProduct.getName());
        assertThat(result.getBody().description()).isEqualTo(givenProduct.getDescription());
    }

    @Test
    void should_not_get_product_details_by_id_and_return_401() {
        // when

        var result = restTemplate.getForEntity(BASE_URL + "/" + UUID.randomUUID(), ProductDetailsResponse.class);

        // then

        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(result.getBody()).isNull();
    }
}
