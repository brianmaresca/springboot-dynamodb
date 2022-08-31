package example.springbootdynamodb.model;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Genre {

  SCIENCE_FICTION("science fiction"),
  FANTASY("fantasy"),
  NON_FICTION("non fiction");

  @JsonValue
  @Getter
  private final String name;

  public static Genre fromValue(String name) {
    return Arrays.stream(values())
        .filter(Objects::nonNull)
        .filter(v -> v.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElse(null);
  }
}
