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

import java.math.BigDecimal;
import java.util.Map;
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
    void should_create_product_when_request_is_valid() {
        // given

        var givenRequest = new ProductRequest("Valid Name", "A valid description longer than 10", new BigDecimal(20));

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
    void should_return_400_when_creating_product_with_invalid_data() throws JSONException {
        // given

        var givenRequest = new ProductRequest("x", "short", new BigDecimal(-1));

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
    void should_return_product_details_when_product_exists() {
        // given

        var givenProduct = productRepository.save(ProductEntity.builder()
                .name("Name")
                .description("Description")
                .price(new BigDecimal(200))
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
    void should_return_404_when_getting_non_existing_product() {
        // when

        var result = restTemplate.getForEntity(BASE_URL + "/" + UUID.randomUUID(), ProductDetailsResponse.class);

        // then

        assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(result.getBody()).isNull();
    }

    @Test
    void should_update_existing_product_when_patch_request_is_valid() {
        // given
        var givenProduct = productRepository.save(ProductEntity.builder()
                .name("Original Name")
                .description("Original Description")
                .price(new BigDecimal("100.00"))
                .build());

        var patchRequest = Map.of(
                "name", "Updated Name",
                "price", new BigDecimal("150.00")
        );

        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var entity = new HttpEntity<>(patchRequest, headers);

        // when
        var result = restTemplate.exchange(
                BASE_URL + "/" + givenProduct.getId(),
                HttpMethod.PATCH,
                entity,
                Void.class
        );

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);

        var updatedProduct = productRepository.findById(givenProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getName()).isEqualTo("Updated Name");
        assertThat(updatedProduct.getDescription()).isEqualTo("Original Description"); // unchanged
        assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("150.00"));
    }

    @Test
    void should_return_404_when_patching_non_existing_product() {
        // given
        var randomId = UUID.randomUUID().toString();

        var patchRequest = Map.of("name", "Doesn't Matter");

        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var entity = new HttpEntity<>(patchRequest, headers);

        // when
        var result = restTemplate.exchange(
                BASE_URL + "/" + randomId,
                HttpMethod.PATCH,
                entity,
                Void.class
        );

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void should_delete_existing_product_and_return_204() {
        // given
        var product = productRepository.save(ProductEntity.builder()
                .name("To be deleted")
                .description("This product will be deleted")
                .price(new BigDecimal("99.99"))
                .build());

        // when
        var response = restTemplate.exchange(
                BASE_URL + "/" + product.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(productRepository.findById(product.getId())).isEmpty();
    }

    @Test
    void should_return_404_when_deleting_non_existing_product() {
        // given
        var nonExistingId = UUID.randomUUID().toString();

        // when
        var response = restTemplate.exchange(
                BASE_URL + "/" + nonExistingId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
