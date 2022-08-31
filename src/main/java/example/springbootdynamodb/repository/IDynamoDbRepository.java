package example.springbootdynamodb.repository;

import java.util.List;
import java.util.Optional;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

public interface IDynamoDbRepository<T> {

  T putItem(T object);

  T updateItem(T object);

  T updateItem(T object, boolean ignoreNulls);

  Optional<T> getItemById(String id);

  Optional<T> getItem(T item);

  T deleteItemById(String id);

  T deleteItem(T object);

  PageIterable<T> scan();

  List<Page<T>> scan(String lastEvaluatedKey);

}
