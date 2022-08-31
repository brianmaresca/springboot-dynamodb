package example.springbootdynamodb;

import static io.restassured.RestAssured.given;

import example.springbootdynamodb.config.AbstractBaseTest;
import example.springbootdynamodb.model.Genre;
import example.springbootdynamodb.model.entity.Book;
import example.springbootdynamodb.model.wire.BookResponse;
import example.springbootdynamodb.model.wire.BooksResponse;
import example.springbootdynamodb.model.wire.CreateBookRequest;
import example.springbootdynamodb.repository.BookRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BooksFT extends AbstractBaseTest {

  @Autowired
  BookRepository bookRepository;

  @Test
  public void createAndGetBookFT() {
    CreateBookRequest createBookRequest = CreateBookRequest.builder()
        .isbn(UUID.randomUUID().toString())
        .genre(Genre.SCIENCE_FICTION)
        .title("Avengers Endgame")
        .author("brian")
        .build();

    BookResponse createBookResponse = given()
        .when()
        .body(createBookRequest)
        .log().all()
        .post("/books")
        .then()
        .log().all()
        .statusCode(HttpStatus.SC_CREATED)
        .body("isbn", Matchers.equalTo(createBookRequest.getIsbn()))
        .body("genre", Matchers.equalTo(createBookRequest.getGenre().getName()))
        .body("title", Matchers.equalTo(createBookRequest.getTitle()))
        .body("author", Matchers.equalTo(createBookRequest.getAuthor()))
        .body("createdTs", Matchers.notNullValue())
        .extract().as(BookResponse.class);

    given()
        .when()
        .log().all()
        .get("/books/{isbn}", createBookResponse.getIsbn())
        .then()
        .log().all()
        .statusCode(HttpStatus.SC_OK)
        .body("isbn", Matchers.equalTo(createBookRequest.getIsbn()))
        .body("genre", Matchers.equalTo(createBookRequest.getGenre().getName()))
        .body("title", Matchers.equalTo(createBookRequest.getTitle()))
        .body("author", Matchers.equalTo(createBookRequest.getAuthor()))
        .body("createdTs", Matchers.notNullValue());
  }

  @Test
  public void scanBooks() {
    IntStream.range(0, 20).forEach(i -> {
      CreateBookRequest createBookRequest = CreateBookRequest.builder()
          .isbn(UUID.randomUUID().toString())
          .genre(Genre.SCIENCE_FICTION)
          .title(String.format("Avengers Endgame, Special Edition: {}", UUID.randomUUID()))
          .author("brian")
          .build();

      given().when()
          .body(createBookRequest)
          .post("/books")
          .then()
          .statusCode(HttpStatus.SC_CREATED)
          .body("isbn", Matchers.equalTo(createBookRequest.getIsbn()))
          .body("genre", Matchers.equalTo(createBookRequest.getGenre().getName()))
          .body("title", Matchers.equalTo(createBookRequest.getTitle()))
          .body("author", Matchers.equalTo(createBookRequest.getAuthor()))
          .body("createdTs", Matchers.notNullValue());
    });

    BooksResponse booksResponse = given()
        .when()
        .log().all()
        .get("/books")
        .then()
        .log().all()
        .statusCode(HttpStatus.SC_OK)
        .extract().as(BooksResponse.class);

    List<Book> books = booksResponse.getBooks();

    while (booksResponse.getPage().getLastEvaluatedKey() != null) {
      booksResponse = given()
          .when()
          .get("/books?startKey=" + booksResponse.getPage().getLastEvaluatedKey())
          .then()
          .statusCode(HttpStatus.SC_OK)
          .extract().as(BooksResponse.class);

      booksResponse.getBooks().parallelStream()
          .forEach(book -> Assertions.assertFalse(books.contains(book)));
      books.addAll(booksResponse.getBooks());
    }

    Assertions.assertEquals(books.size(), bookRepository.getEstimatedCount());

  }
}
