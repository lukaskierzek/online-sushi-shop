package pl.lukaskierzek.sushi.shop.service.catalog.api.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import pl.lukaskierzek.sushi.shop.service.catalog.application.CatalogServiceApplication;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static pl.lukaskierzek.sushi.shop.service.catalog.api.product.ProductFixture.EXPECTED_VALIDATION_ERROR_RESULT;

@SpringBootTest(webEnvironment = RANDOM_PORT, classes = CatalogServiceApplication.class)
class ProductControllerTests {

    private static final String BASE_URL = "/products";

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void whenValidRequest_thenReturns201() {
        // given
        var request = new ProductRequest("Valid Name", "A valid description longer than 10");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<ProductRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL, entity, Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }

    @Test
    void whenInvalidRequest_thenReturns400() {
        // given
        var invalidRequest = new ProductRequest("x", "short");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<ProductRequest> entity = new HttpEntity<>(invalidRequest, headers);

        // when
        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, entity, String.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(EXPECTED_VALIDATION_ERROR_RESULT);
    }
}
