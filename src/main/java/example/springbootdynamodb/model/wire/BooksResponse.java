package example.springbootdynamodb.model.wire;


import example.springbootdynamodb.model.entity.Book;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class BooksResponse {

  @Builder.Default
  private List<Book> books = new ArrayList<>();
  @Builder.Default
  private Page page = Page.builder().build();

  @Builder
  @Data
  @Jacksonized
  public static class Page {
    @Builder.Default
    private Integer size = 0;
    @Builder.Default
    private Long totalElements = 0L;
    @Builder.Default
    private Integer totalPages = 0;
    private String lastEvaluatedKey;
  }
}
