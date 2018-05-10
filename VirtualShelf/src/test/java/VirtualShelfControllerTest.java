import model.Book;
import model.BookRepository;
import model.User;
import model.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class VirtualShelfControllerTest {

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;

    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private Book bookOne;
    private Book bookTwo;
    private User userOne;
    private User userTwo;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.bookRepository.deleteAllInBatch();
        this.userRepository.deleteAllInBatch();

        this.bookOne = new Book.BookBuilder()
                .ISBN("1781100217")
                .title("Harry Potter and the Philosopher's Stone")
                .libraryName("Mary GrandPre")
                .price(5.74)        //Cheap Book
                .build();

        this.bookTwo = new Book.BookBuilder()
                .ISBN("1781100500")
                .title("Harry Potter and the Chamber of Secrets")
                .libraryName("Mary GrandPre")
                .price(10.39)        //Expensive Book
                .build();

        this.userOne = new User("Bahy", "123456", "Mary GrandPre");
        this.userTwo = new User("Mohamed", "123456", "Alef");

        bookRepository.save(bookOne);
        bookRepository.save(bookTwo);
        userRepository.save(userOne);
        userRepository.save(userTwo);
    }

    @Test
    public void getSize() throws Exception {
        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    public void addBook() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=0545791340" +
                "&price=8.59")
                .content(this.json(userOne))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void addBookWithInvalidISBN() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=1" +
                "&price=8.59")
                .content(this.json(userOne))
                .contentType(contentType))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void addBookAndCheckSize() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=0545791340" +
                "&price=8.59")
                .content(this.json(userOne))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    public void removeBookWithValidISBN() throws Exception {
        mockMvc.perform(post("/remove-book?" +
                "ISBN=1781100217")
                .content(this.json(userOne))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void removeBookThatBelongsToDifferentUser() throws Exception {
        mockMvc.perform(post("/remove-book?" +
                "ISBN=1781100217")
                .content(this.json(userTwo))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkBadRequest() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "&price=8.59")
                .content(this.json(userOne))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkBadRequestAndSize() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "&price=8.59")
                .content(this.json(userOne))
                .contentType(contentType))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    public void checkDifferentLibraryNameAndSameISBN() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=0545791340" +
                "&price=8.59")
                .content(this.json(userTwo))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    public void checkDifferentISBNAndSameLibraryName() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=1781100527" +
                "&price=8.59")
                .content(this.json(userOne))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    public void checkPriceDESCSorting() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "sorting-attribute=price" +
                "&sorting-direction=DESC"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookTwo.getBookKey().getISBN())));

    }

    @Test
    public void checkPriceASCCSorting() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "sorting-attribute=price" +
                "&sorting-direction=ASC"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookOne.getBookKey().getISBN())));

    }

    @Test
    public void checkTitleASCCSorting() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "sorting-attribute=title" +
                "&sorting-direction=ASC"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookTwo.getBookKey().getISBN())));

    }

    @Test
    public void filterByPrice() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "filter-query=price:5.74"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookOne.getBookKey().getISBN())));

    }

    @Test
    public void filterByPriceGreaterThan() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "filter-query=price>5.75"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookTwo.getBookKey().getISBN())));
    }

    @Test
    public void filterByPriceLessThan() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "filter-query=price<5.73"))
                .andExpect(content().string(""));
    }

    @Test
    public void filterEmptyPriceBadRequest() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "filter-query=price"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void filterInvalidPriceBadRequest() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "filter-query=price:string"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void filterInvalidTitleBadRequest() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "filter-query=title<8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void filterByISBN() throws Exception {
        mockMvc.perform(get("/all-books?" +
                "filter-query=bookKey.ISBN:1781100217"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookOne.getBookKey().getISBN())));
    }

    @Test
    public void filterByLibraryNameANDPrice() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=1781100527" +
                "&price=5.74")
                .content(this.json(userTwo))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/all-books?" +
                "filter-query=price:5.74,bookKey.libraryName:Alef"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is("1781100527")));
    }

    @Test
    public void filterByPriceAndSortByLibraryName() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=1781100527" +
                "&price=5.74")
                .content(this.json(userTwo))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/all-books?" +
                "filter-query=price:5.74" +
                "&sorting-attribute=bookKey.libraryName" +
                "&sorting-direction=ASC"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookKey.libraryName", is("Alef")))
                .andExpect(jsonPath("$[0].bookKey.isbn", is("1781100527")))
                .andExpect(jsonPath("$[1].bookKey.libraryName", is("Mary GrandPre")))
                .andExpect(jsonPath("$[1].bookKey.isbn", is("1781100217")));
    }

    @Test
    public void addValidUserReturnsCreate() throws Exception {
        mockMvc.perform(post("/add-user")
                .content(this.json(new User("ElRakad", "123456", "ElRakad's Book Store")))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }

    @Test
    public void addUserWithDuplicateUserNameReturnsConflict() throws Exception {
        mockMvc.perform(post("/add-user")
                .content(this.json(new User("Bahy", "123456", "ElRakad's Book Store")))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void addUserWithNoPasswordReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/add-user")
                .content(this.json(new User("ElRakad", null, "ElRakad's Book Store")))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserWithSameLibraryNameReturnsConflict() throws Exception {
        mockMvc.perform(post("/add-user")
                .content(this.json(new User("ElRakad", "123", "Mary GrandPre")))
                .contentType(contentType))
                .andExpect(status().isConflict());
    }

    @Test
    public void addUserWithNoLibraryNameReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/add-user")
                .content(this.json(new User("ElRakad", "123456", null)))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authenticateValidUserReturnsOK() throws Exception {
        mockMvc.perform(post("/authenticate")
                .content(this.json(new User("Bahy", "123456", null)))
                .contentType(contentType))
                .andExpect(status().isOk());
    }

    @Test
    public void authenticateNonexistentUserReturnsNotFound() throws Exception {
        mockMvc.perform(post("/authenticate")
                .content(this.json(new User("ElRakad", null, null)))
                .contentType(contentType))
                .andExpect(status().isNotFound());
    }

    @Test
    public void authenticateInvalidPasswordReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/authenticate")
                .content(this.json(new User("Bahy", "1234", null)))
                .contentType(contentType))
                .andExpect(status().isBadRequest());
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
