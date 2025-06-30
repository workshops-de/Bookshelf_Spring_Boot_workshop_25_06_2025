package de.workshops.bookshelf.book;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import de.workshops.bookshelf.config.JacksonTestConfiguration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(BookRestController.class)
@AutoConfigureMockMvc
@Import(JacksonTestConfiguration.class)
class BookRestControllerMockitoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void getAllBooks() throws Exception {
        List<Book> mockedBookList = new ArrayList<>();
        mockedBookList.add(new Book());

        Book mockedBook = Mockito.mock(Book.class);
        Mockito.when(mockedBook.getTitle()).thenReturn("Mocked Book Title");
        mockedBookList.add(mockedBook);

        Mockito.when(bookService.getAllBooks()).thenReturn(mockedBookList);

        mockMvc
            .perform(MockMvcRequestBuilders.get("/book"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", is("Mocked Book Title")));
    }
}
