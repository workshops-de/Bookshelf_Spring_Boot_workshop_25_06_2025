package de.workshops.bookshelf.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(BookRestController.class)
class BookRestControllerMockitoTest {

    @Autowired
    private BookRestController bookRestController;

    @MockitoBean
    private BookService bookService;

    @Test
    void getAllBooks() {
        List<Book> mockedBookList = new ArrayList<>();
        mockedBookList.add(new Book());
        Mockito.when(bookService.getAllBooks()).thenReturn(mockedBookList);

        assertNotNull(bookRestController.getAllBooks());
        assertEquals(1, bookRestController.getAllBooks().size());
    }
}
