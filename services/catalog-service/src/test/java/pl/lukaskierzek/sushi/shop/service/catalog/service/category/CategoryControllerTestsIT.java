package pl.lukaskierzek.sushi.shop.service.catalog.service.category;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import pl.lukaskierzek.sushi.shop.service.catalog.service.category.CategoryController.CategoryRequest;

import java.util.HashSet;
import java.util.Set;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = RANDOM_PORT)
class CategoryControllerTestsIT {

    static final String BASE_URL = "/categories";

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    CategoryEntityRepository entityRepository;

    @Autowired
    CategoryHistoryEntityRepository historyEntityRepository;

    @BeforeEach
    void beforeEach() {
        entityRepository.deleteAll();
        historyEntityRepository.deleteAll();
    }

    @Test
    void shouldNewCategoryAndPersistCategory() {
        var request = new CategoryRequest("Books", "All kinds of books", new HashSet<>());

        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(CREATED);
        assertThat(response.getBody()).isNotBlank();
        assertThat(response.getHeaders().getLocation()).hasToString(BASE_URL + "/" + response.getBody());
    }

    @Test
    void shouldReturn400WhenNameTooShort() {
        var request = new CategoryRequest("ab", "Valid description", new HashSet<>());

        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).contains("Category name must be at least 3 characters long");
    }

    @Test
    void shouldReturn400WhenDescriptionTooShort() {
        var request = new CategoryRequest("Books", "xy", new HashSet<>());

        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).contains("Category description must be at least 3 characters long");
    }

    @Test
    void shouldPatchCategory() {
        var id = createCategory("Initial", "Initial description");

        var updateRequest = new CategoryRequest("Updated", null, new HashSet<>());
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        var requestEntity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<Void> response = restTemplate.exchange(
            BASE_URL + "/" + id,
            HttpMethod.PATCH,
            requestEntity,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);

        ResponseEntity<Category.CategorySnapshot> updated = restTemplate.getForEntity(
            BASE_URL + "/" + id,
            Category.CategorySnapshot.class
        );

        assertNotNull(updated.getBody());
        assertThat(updated.getBody().name()).isEqualTo("Updated");
    }

    @Test
    void shouldReturn400WhenPatchNameIsTooShort() {
        var id = createCategory("Valid Name", "Valid description");

        var updateRequest = new CategoryRequest("ab", null, new HashSet<>());
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        var requestEntity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL + "/" + id,
            HttpMethod.PATCH,
            requestEntity,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).contains("Category name must be at least 3 characters long");
    }

    @Test
    void shouldReturn400WhenPatchDescriptionIsTooShort() {
        var id = createCategory("Valid Name", "Valid description");

        var updateRequest = new CategoryRequest(null, "xy", new HashSet<>());
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);

        var requestEntity = new HttpEntity<>(updateRequest, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL + "/" + id,
            HttpMethod.PATCH,
            requestEntity,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).contains("Category description must be at least 3 characters long");
    }

    @Test
    void shouldReturnPersistedCategory() {
        var id = createCategory("Food", "Groceries");

        ResponseEntity<Category.CategorySnapshot> response = restTemplate.getForEntity(
            BASE_URL + "/" + id,
            Category.CategorySnapshot.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);
        assertNotNull(response.getBody());
        assertThat(response.getBody().name()).isEqualTo("Food");
    }

    @Test
    void shouldReturn404WhenCategoryNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            BASE_URL + "/nonexistent-id",
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).contains("Category not found");
    }

    @Test
    void shouldAddSubCategoryToParent() {
        String parentId = createCategory("Parent", "Main category");
        String childId = createCategory("Child", "Nested category");

        var request = new CategoryRequest(null, null, Set.of(childId));
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var entity = new HttpEntity<>(request, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
            BASE_URL + "/" + parentId,
            HttpMethod.PATCH,
            entity,
            Void.class
        );

        assertThat(response.getStatusCode()).isEqualTo(OK);

        // Verify subcategory was added
        ResponseEntity<Category.CategorySnapshot> getResponse = restTemplate.getForEntity(
            BASE_URL + "/" + parentId,
            Category.CategorySnapshot.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(OK);
        assertThat(getResponse.getBody()).isNotNull();
    }

    @Test
    void shouldReturn404WhenSubCategoryNotExists() {
        String parentId = createCategory("Parent", "Main category");

        var request = new CategoryRequest(null, null, Set.of("nonexistent-id"));
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL + "/" + parentId,
            HttpMethod.PATCH,
            entity,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).contains("Category not found");
    }

    @Test
    void shouldReturn400WhenAddingItselfAsSubCategory() {
        String id = createCategory("SelfCategory", "Cannot self-contain");

        var request = new CategoryRequest(null, null, Set.of(id));
        var headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        var entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
            BASE_URL + "/" + id,
            HttpMethod.PATCH,
            entity,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).contains("cannot be a subcategory of itself");
    }

    String createCategory(String name, String description) {
        var request = new CategoryRequest(name, description, new HashSet<>());
        ResponseEntity<String> response = restTemplate.postForEntity(BASE_URL, request, String.class);
        return response.getBody();
    }
}
