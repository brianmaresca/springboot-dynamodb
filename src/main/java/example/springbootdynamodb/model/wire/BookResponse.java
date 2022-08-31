package example.springbootdynamodb.model.wire;


import example.springbootdynamodb.model.Genre;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class BookResponse {

  private final String isbn;
  private String title;
  private String author;
  private Genre genre;
  private String exampleSnakeCaseProperty;
  private Instant createdTs;
  private Instant updatedTs;
}
