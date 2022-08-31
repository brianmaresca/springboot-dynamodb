package example.springbootdynamodb.repository;

import example.springbootdynamodb.model.entity.Book;
import example.springbootdynamodb.properties.DynamoTableProperties;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class BookRepository extends DynamoDbRepository<Book> {

  @Getter
  private final String tableName;

  public BookRepository(DynamoTableProperties dynamoTableProperties) {
    tableName = dynamoTableProperties.getBooks();
  }

}
