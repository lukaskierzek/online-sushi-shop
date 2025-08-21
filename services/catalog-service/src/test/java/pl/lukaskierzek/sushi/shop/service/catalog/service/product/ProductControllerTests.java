package pl.lukaskierzek.sushi.shop.service.catalog.service.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.CREATED;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ProductControllerTests {

    static final String BASE_URL = "/products";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProductEntityRepository entityRepository;

    @Autowired
    ProductHistoryEntityRepository historyEntityRepository;

    @BeforeEach
    void beforeEach() {
        entityRepository.deleteAll();
        historyEntityRepository.deleteAll();
    }

    @Test
    void shouldNewProductAndPersistProduct() {
        var request = new ProductController.ProductRequest("Sushi", "All kinds of sushi", new BigDecimal(20.00), "1");

        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
    }
}
