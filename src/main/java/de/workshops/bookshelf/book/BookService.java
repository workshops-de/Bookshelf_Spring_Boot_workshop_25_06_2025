package de.workshops.bookshelf.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
class BookService {

    private final BookRepository bookRepository;

    List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    Book searchBookByIsbn(String isbn) throws BookNotFoundException {
        return bookRepository
            .findAll()
            .stream()
            .filter(book -> hasIsbn(book, isbn))
            .findFirst()
            .orElseThrow(BookNotFoundException::new);
    }

    Book searchBookByAuthor(String author) throws BookNotFoundException {
        return bookRepository
            .findAll()
            .stream()
            .filter(book -> hasAuthor(book, author))
            .findFirst()
            .orElseThrow(BookNotFoundException::new);
    }

    List<Book> searchBooks(BookSearchRequest request) {
        return bookRepository
            .findAll()
            .stream()
                .filter(book -> hasAuthor(book, request.author()))
                .filter(book -> hasIsbn(book, request.isbn()))
                .toList();
    }

    Book createBook(Book book) {
        return bookRepository.create(book);
    }

    private boolean hasIsbn(Book book, String isbn) {
        return book.getIsbn().equals(isbn);
    }

    private boolean hasAuthor(Book book, String author) {
        return book.getAuthor().contains(author);
    }
}
