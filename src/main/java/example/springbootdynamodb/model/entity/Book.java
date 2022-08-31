package example.springbootdynamodb.model.entity;


import example.springbootdynamodb.converter.JacksonCustomConverterProvider;
import example.springbootdynamodb.model.Genre;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.DefaultAttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Data
@Builder
@DynamoDbBean(
    converterProviders = {
        JacksonCustomConverterProvider.class,
        DefaultAttributeConverterProvider.class
    })
@NoArgsConstructor
@AllArgsConstructor
public class Book {

  @Getter(onMethod = @__({@DynamoDbPartitionKey}))
  private String isbn;

  private String title;
  private String author;
  private Instant createdTs;
  private Instant updatedTs;

  @Getter(onMethod = @__({@DynamoDbAttribute("example_snake_case_property")}))
  @Setter(onMethod = @__({@DynamoDbAttribute("example_snake_case_property")}))
  private String exampleSnakeCaseProperty;

  private Genre genre;

}
