package example.springbootdynamodb.converter;


import com.fasterxml.jackson.databind.ObjectMapper;
import example.springbootdynamodb.exception.InternalErrorException;
import java.util.Arrays;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
@Configurable
@RequiredArgsConstructor
@Slf4j
public class JacksonEnumAttributeConverter<T> implements AttributeConverter<T> {

  private static ObjectMapper objectMapper;
  @Nullable
  private final Class<T> enumClass;

  @Autowired
  public void setObjectMapper(ObjectMapper objectMapper) {
    JacksonEnumAttributeConverter.objectMapper = objectMapper;
  }

  @Override
  public AttributeValue transformFrom(T input) {
    return AttributeValue.builder().s(objectMapper.convertValue(input, String.class)).build();
  }

  @Override
  public T transformTo(AttributeValue input) {
    // first try to map the string to enum by looking for the value as indiciated by the @JsonValue
    // on the getter in the enum
    // if that fails, try to map the enum constant as a fallback
    return Optional.ofNullable(objectMapper.convertValue(input.s(), this.enumClass))
        .orElseGet(
            () ->
                Arrays.stream(this.enumClass.getEnumConstants())
                    .filter(constant -> input.s().equals(constant.toString()))
                    .findFirst()
                    .orElseGet(
                        () -> {
                          throw new InternalErrorException(
                              String.format(
                                  "Unable to convert string value %s to enum type %s",
                                  input.s(), this.enumClass));
                        }));
  }

  @Override
  public EnhancedType type() {
    return EnhancedType.of(this.enumClass);
  }

  @Override
  public AttributeValueType attributeValueType() {
    return AttributeValueType.S;
  }
}
