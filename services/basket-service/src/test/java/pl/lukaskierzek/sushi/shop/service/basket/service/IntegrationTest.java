package pl.lukaskierzek.sushi.shop.service.basket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@SpringBootTest(classes = BasketServiceApplication.class, webEnvironment = RANDOM_PORT)
@EmbeddedKafka
public class IntegrationTest {

    @Autowired
    protected RedisTemplate<Object, Object> redisTemplate;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    protected MockMvc mvc;

    @Value("${kafka.topics.product-price-updated}")
    protected String productPriceUpdatedTopic;

    @Value("${kafka.consumer.fixed-backoff.interval}")
    protected long consumerBackoffInterval;

    @Value("${kafka.consumer.fixed-backoff.attempts}")
    protected int consumerBackoffAttempts;

    @BeforeEach
    void setUp() {
        assertNotNull(redisTemplate.getConnectionFactory());
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
        kafkaTemplate.flush();
    }

    @Container
    static RedisContainer redisContainer = new RedisContainer(DockerImageName.parse("redis:alpine"));

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
    }
}
