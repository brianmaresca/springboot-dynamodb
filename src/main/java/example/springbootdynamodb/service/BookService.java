package example.springbootdynamodb.service;

import example.springbootdynamodb.model.entity.Book;
import example.springbootdynamodb.model.wire.BooksResponse;
import example.springbootdynamodb.model.wire.BooksResponse.Page;
import example.springbootdynamodb.model.wire.CreateBookRequest;
import example.springbootdynamodb.repository.BookRepository;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public Book createBook(CreateBookRequest createBookRequest) {
    Book book = Book.builder()
        .isbn(createBookRequest.getIsbn())
        .title(createBookRequest.getTitle())
        .author(createBookRequest.getAuthor())
        .genre(createBookRequest.getGenre())
        .createdTs(Instant.now())
        .build();
    return bookRepository.putItem(book);
  }


  public Optional<Book> getBook(String isbn) {
    return bookRepository.getItemById(isbn);
  }

  public BooksResponse getBooks() {
    Long total = bookRepository.getEstimatedCount();
    Long totalPages = total / bookRepository.getScanLimit();
   return bookRepository.scan().stream().limit(5).findFirst().stream().collect(Collectors.toList())
        .stream()
        .findFirst()
        .map(i -> BooksResponse.builder()
            .books(i.items())
            .page(Page.builder()
                .size(i.items().size())
                .totalElements(total)
                .lastEvaluatedKey(i.lastEvaluatedKey().values().stream().findAny().map(AttributeValue::s).orElse(null))
                .totalPages(totalPages.intValue())
                .build())
            .build())
       .orElse(BooksResponse.builder().build());
  }


  public BooksResponse getBooks(String lastEvaluatedKey) {
    Long total = bookRepository.getEstimatedCount();
    Long totalPages = total / bookRepository.getScanLimit();
    return bookRepository.scan(lastEvaluatedKey).stream().limit(5).findFirst().stream().collect(Collectors.toList())
        .stream()
        .findFirst()
        .map(i -> BooksResponse.builder()
            .books(i.items())
            .page(Page.builder()
                .size(i.items().size())
                .totalElements(total)
                .lastEvaluatedKey(Optional.ofNullable(i.lastEvaluatedKey())
                    .map(Map::values)
                    .filter(CollectionUtils::isNotEmpty)
                    .map(values -> values.stream().findAny().map(AttributeValue::s).orElse(null))
                    .orElse(null))
                .totalPages(totalPages.intValue())
                .build())
            .build())
        .orElse(BooksResponse.builder().build());
  }

}
