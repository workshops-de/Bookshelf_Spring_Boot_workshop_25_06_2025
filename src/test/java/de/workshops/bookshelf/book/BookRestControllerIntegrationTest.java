package de.workshops.bookshelf.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.workshops.bookshelf.config.JacksonTestConfiguration;
import io.restassured.RestAssured;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(JacksonTestConfiguration.class)
class BookRestControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRestController bookRestController;

    @Test
    void getAllBooks() throws Exception {
        MvcResult mvcResult = mockMvc
            .perform(MockMvcRequestBuilders.get("/book"))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(3)))
            .andExpect(MockMvcResultMatchers.jsonPath("$[1].title", is("Clean Code")))
            .andReturn();
        String jsonPayload = mvcResult.getResponse().getContentAsString();

        Book[] books = objectMapper.readValue(jsonPayload, Book[].class);
        assertEquals(3, books.length);
        assertEquals("Clean Code", books[1].getTitle());
    }

    @Test
    void testWithRestAssuredMockMvc() {
        RestAssuredMockMvc.standaloneSetup(bookRestController);
        RestAssuredMockMvc.
            given().
                log().all().
            when().
                get("/book").
            then().
                log().all().
                statusCode(200).
                body("author[0]", equalTo("Erich Gamma"));
    }

    @Test
    void testWithRestAssured(@LocalServerPort int port) {
        RestAssured.port = port;
        RestAssured.
            given().
                log().all().
            when().
                get("/book").
            then().
                log().all().
                statusCode(200).
                body("author[0]", equalTo("Erich Gamma"));
    }

    @Test
    void createBook() throws Exception {
        String author = "Eric Evans";
        String title = "Domain-Driven Design: Tackling Complexity in the Heart of Software";
        String isbn = "978-0321125217";
        String description = "This is not a book about specific technologies. It offers readers a systematic approach to domain-driven design, presenting an extensive set of design best practices, experience-based techniques, and fundamental principles that facilitate the development of software projects facing complex domains.";

        Book expectedBook = generateExpectedBook(
            author,
            title,
            isbn,
            description
        );

        ResultMatcher expectedHttpStatus = MockMvcResultMatchers.status().isOk();
        var mvcResult = sendCreateBookRequest(
            isbn,
            title,
            author,
            description,
            expectedHttpStatus
        );
        String jsonPayload = mvcResult.getResponse().getContentAsString();

        Book book = objectMapper.readValue(jsonPayload, Book.class);

        assertThat(book)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(expectedBook);

        // Restore previous database state.
        mockMvc
            .perform(MockMvcRequestBuilders.delete("/book/{isbn}", book.getIsbn())
            .contentType(MediaType.APPLICATION_JSON))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    private MvcResult sendCreateBookRequest(
        String isbn,
        String title,
        String author,
        String description,
        ResultMatcher expectedHttpStatus
    )
        throws Exception {
        return mockMvc
            .perform(MockMvcRequestBuilders.post("/book")
            .content(
                """
                    {
                        "isbn": "%s",
                        "title": "%s",
                        "author": "%s",
                        "description": "%s"
                    }
                    """
                    .formatted(
                        isbn,
                        title,
                        author,
                        description
                    )
            )
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(expectedHttpStatus)
            .andReturn();
    }

    private Book generateExpectedBook(String author, String title, String isbn, String description) {
        return Book
            .builder()
            .author(author)
            .title(title)
            .isbn(isbn)
            .description(description)
            .build();
    }
}
