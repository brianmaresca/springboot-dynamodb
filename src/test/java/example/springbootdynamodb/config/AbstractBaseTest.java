package example.springbootdynamodb.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import example.springbootdynamodb.Application;
import example.springbootdynamodb.repository.DynamoDbRepository;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@ExtendWith(SpringExtension.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = Application.class)
@ActiveProfiles("dev")
@Slf4j
public abstract class AbstractBaseTest {

  private static final String dynamoDbLocalPort = DynamoDBLocalUtil.getFreePortString();
  @Autowired
  public ObjectMapper objectMapper;
  @Autowired
  public DynamoDbClient dynamoDbClient;
  @LocalServerPort
  protected int serverPort;

  @Autowired
  private ApplicationContext applicationContext;

  @DynamicPropertySource
  public static void configureDynamoDbLocalServer(DynamicPropertyRegistry registry) {
    registry.add("dynamodblocal.server.port", () -> dynamoDbLocalPort);
  }

  @BeforeAll
  public static void beforeAll() {
    DynamoDBLocalUtil.createProxyServer(dynamoDbLocalPort);
  }

  @BeforeEach
  public void setupBaseTest() {
    configureRestAssured();
  }

  private void configureRestAssured() {
    RestAssured.port = serverPort;
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

    RestAssured.config =
        RestAssuredConfig.config()
            .objectMapperConfig(
                new ObjectMapperConfig()
                    .jackson2ObjectMapperFactory((cls, charset) -> objectMapper))
            .logConfig(
                LogConfig.logConfig()
                    .enableLoggingOfRequestAndResponseIfValidationFails()
                    .enablePrettyPrinting(true));

    RestAssured.requestSpecification =
        new RequestSpecBuilder()
            .build()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .config(RestAssured.config());
  }

  @AfterEach
  public <T> void cleanUp() {
    RestAssured.reset();
    applicationContext.getBeansOfType(DynamoDbRepository.class).values().parallelStream()
        .forEach(repo -> {
          Iterator<T> iterator = repo.scan().items().iterator();
          while (iterator.hasNext()) {
            repo.deleteItem(iterator.next());
          }
        });
  }


}
