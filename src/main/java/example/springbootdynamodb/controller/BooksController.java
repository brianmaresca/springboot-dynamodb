package example.springbootdynamodb.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import example.springbootdynamodb.model.entity.Book;
import example.springbootdynamodb.model.wire.BookResponse;
import example.springbootdynamodb.model.wire.BooksResponse;
import example.springbootdynamodb.model.wire.CreateBookRequest;
import example.springbootdynamodb.service.BookService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BooksController {

  private final BookService bookService;
  private final ObjectMapper objectMapper;

  @PostMapping
  public ResponseEntity<Book> createBook(@RequestBody CreateBookRequest createBookRequest) {
    Book book = bookService.createBook(createBookRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(book);
  }


  @GetMapping("/{isbn}")
  public ResponseEntity getBook(@PathVariable("isbn") String isbn) {
    return Optional.ofNullable(bookService.getBook(isbn))
        .map(book -> objectMapper.convertValue(book, BookResponse.class))
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<BooksResponse> getBooks(@RequestParam(name = "startKey", required = false) String startKey) {

    return ResponseEntity.ok(Optional.ofNullable(startKey)
        .map(bookService::getBooks)
        .orElseGet(bookService::getBooks));
  }
}