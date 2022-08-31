package example.springbootdynamodb.properties;

import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("dynamo.tablenames")
public class DynamoTableProperties {

  private String books;

  public List<String> getAllTableNames() {
    return Arrays.asList(books);
  }
}
