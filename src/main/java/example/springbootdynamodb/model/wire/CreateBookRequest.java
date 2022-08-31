package example.springbootdynamodb.model.wire;


import example.springbootdynamodb.model.Genre;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class CreateBookRequest {

  @NotBlank
  private final String isbn;

  @NotBlank
  private String title;

  @NotBlank
  private String author;

  @NotBlank
  private Genre genre;
}
