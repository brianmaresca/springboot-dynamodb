package example.springbootdynamodb.converter;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.ListAttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.internal.converter.attribute.SetAttributeConverter;

@Slf4j
public class JacksonCustomConverterProvider implements AttributeConverterProvider {

  @Override
  @SuppressWarnings("unchecked")
  public <T> AttributeConverter<T> converterFor(EnhancedType<T> enhancedType) {
    if (enhancedType.rawClass().isEnum()) {
      return new JacksonEnumAttributeConverter<>(enhancedType.rawClass());
    } else if (enhancedType.rawClass().isAssignableFrom(List.class)) {
      return Optional.ofNullable((EnhancedType<T>) enhancedType.rawClassParameters().get(0))
          .map(EnhancedType::rawClass)
          .filter(Class::isEnum)
          .map(this::createListEnumConverter)
          .orElse(null);
    } else if (enhancedType.rawClass().isAssignableFrom(Set.class)) {
      return Optional.ofNullable((EnhancedType<T>) enhancedType.rawClassParameters().get(0))
          .map(EnhancedType::rawClass)
          .filter(Class::isEnum)
          .map(this::createSetEnumConverter)
          .orElse(null);
    } else {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  private <T> AttributeConverter<T> createListEnumConverter(Class<T> rawInnerClass) {
    return (AttributeConverter<T>)
        ListAttributeConverter.create(new JacksonEnumAttributeConverter<>(rawInnerClass));
  }

  @SuppressWarnings("unchecked")
  private <T> AttributeConverter<T> createSetEnumConverter(Class<T> rawInnerClass) {
    return (AttributeConverter<T>)
        SetAttributeConverter.setConverter(new JacksonEnumAttributeConverter<>(rawInnerClass));
  }
}
