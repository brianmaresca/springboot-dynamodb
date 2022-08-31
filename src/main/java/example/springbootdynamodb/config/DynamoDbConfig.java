package example.springbootdynamodb.config;


import example.springbootdynamodb.properties.AppProperties;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.util.UriComponentsBuilder;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
@Slf4j
public class DynamoDbConfig {

  @Bean
  @Profile("dev")
  public DynamoDbClient dynamoDbDevClient(
      @Value("${dynamodblocal.server.host}") String dynamoDbLocalEndpoint,
      @Value("${dynamodblocal.server.port}") String dynamoDbLocalPort,
      AppProperties appProperties) {
    String x = UriComponentsBuilder.fromUriString(dynamoDbLocalEndpoint).port(dynamoDbLocalPort)
        .build().toUriString();
    return DynamoDbClient.builder()
        .endpointOverride(URI.create(x))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create("dummy-key", "dummy-secret")))
        .region(appProperties.getRegion())
        .build();
  }

  @Bean
  @Profile("live")
  public DynamoDbClient dynamoDbClient(AppProperties appProperties) {
    return DynamoDbClient.builder().region(appProperties.getRegion()).build();
  }

  @Bean
  public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
    return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
  }
}
