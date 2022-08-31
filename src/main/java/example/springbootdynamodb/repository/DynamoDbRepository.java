package example.springbootdynamodb.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;

@Slf4j
public abstract class DynamoDbRepository<T> implements IDynamoDbRepository<T> {

  private final Class<T> clazz;
  @Getter
  private final Long scanLimit = 5L;
  private DynamoDbTable<T> table;
  private String partitionKey;

  public DynamoDbRepository() {
    clazz =
        (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), IDynamoDbRepository.class);
  }

  @Autowired
  public final void setTable(@Autowired DynamoDbEnhancedClient dynamoDbEnhancedClient) {
    this.table = dynamoDbEnhancedClient.table(getTableName(),
        TableSchema.fromBean(this.clazz));
    this.partitionKey = this.table.tableSchema().tableMetadata().primaryPartitionKey();
    createTable(this.table);
  }

  private void createTable(DynamoDbTable table) {
    try {
      table.createTable();
    } catch (ResourceInUseException e) {
      log.debug("table {} already exists, skipping creation", getTableName());
    }
  }

  protected abstract String getTableName();

  @Override
  public Optional<T> getItemById(String id) {
    return Optional.ofNullable(id)
        .filter(StringUtils::isNotBlank)
        .map(idx -> Key.builder().partitionValue(idx).build())
        .map(table::getItem);
  }

  @Override
  public Optional<T> getItem(T item) {
    return Optional.ofNullable(item).map(table::getItem);
  }

  /*
   insert an item into the table
   seems as though putItem and updateItem do the same thing,
   but updateItem returns the inserted item
  */
  public T putItem(T item) {
    return table.updateItem(UpdateItemEnhancedRequest.builder(clazz).item(item).build());
  }

  @Override
  public T deleteItem(T item) {
    return table.deleteItem(item);
  }

  @Override
  public T deleteItemById(String id) {
    return table.deleteItem(Key.builder().partitionValue(id).build());
  }

  @Override
  public T updateItem(T item) {
    return updateItem(item, true);
  }

  /**
   * @param item        - the item to write to the table
   * @param ignoreNulls - if true, null attributes in T item will be discarded, and those
   *                    corresponding attributes in the table won't be touched. if false, existing
   *                    attributes in the table will be set to null if they are null in T item
   * @return the updated item
   */
  @Override
  public T updateItem(T item, boolean ignoreNulls) {
    return table.updateItem(
        UpdateItemEnhancedRequest.builder(clazz).item(item).ignoreNulls(ignoreNulls).build());
  }

  public List<Page<T>> scan(String exclusiveStartKey) {
    AttributeValue attributeValue = AttributeValue.builder()
        .s(exclusiveStartKey)
        .build();
    Map<String, AttributeValue> wtf = new HashMap<>();
    wtf.put(this.partitionKey, attributeValue);

    return table.scan(ScanEnhancedRequest.builder()
            .limit(5)
            .exclusiveStartKey(wtf)
            .build())
        .stream()
        .limit(5)
        .findFirst()
        .stream().collect(Collectors.toList());
  }

  @Override
  public PageIterable<T> scan() {
    return table.scan(ScanEnhancedRequest.builder()
        .limit(5).build());
  }

  public Long getEstimatedCount() {
    return this.table.describeTable().table().itemCount();
  }

}
