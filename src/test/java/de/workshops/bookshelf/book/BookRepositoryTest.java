package de.workshops.bookshelf.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Testcontainers
class BookRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer
        = new PostgreSQLContainer<>("postgres:latest")
        .withReuse(true);

    @Autowired
    private BookRepository bookRepository;

    @Test
    void create() {
        Book book = buildAndSaveBook("123-4567890");

        List<Book> books = bookRepository.findAll();

        assertNotNull(books);
        assertEquals(4, books.size());
        assertEquals(book.getIsbn(), books.get(3).getIsbn());

        // Restore previous state
        bookRepository.delete(book);
    }

    @Test
    void findBookByIsbn() {
        String isbn = "123-4567890";
        Book book = buildAndSaveBook(isbn);

        Book newBook = bookRepository.findByIsbn(isbn);

        assertNotNull(newBook);
        assertEquals(book.getTitle(), newBook.getTitle());

        // Restore previous state
        bookRepository.delete(book);
    }

    private Book buildAndSaveBook(String isbn) {
        Book book = Book.builder()
            .title("Title")
            .author("Author")
            .description("Description")
            .isbn(isbn)
            .build();
        bookRepository.save(book);

        return book;
    }
}
