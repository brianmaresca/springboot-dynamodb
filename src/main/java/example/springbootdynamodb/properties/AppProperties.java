package example.springbootdynamodb.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

  private Region region;
}
