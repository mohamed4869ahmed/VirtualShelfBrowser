import model.Book;
import model.BookRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class VirtualShelfControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.bookRepository.deleteAllInBatch();

        Book.BookBuilder bookOne = new Book.BookBuilder()
                .ISBN("1781100217")
                .title("Harry Potter and the Philosopher's Stone")
                .libraryName("Mary GrandPre")
                .price(5.74);

        Book.BookBuilder bookTwo = new Book.BookBuilder()
                .ISBN("1781100500")
                .title("Harry Potter and the Chamber of Secrets")
                .libraryName("Mary GrandPre")
                .price(10.39);

        bookRepository.save(bookOne.build());
        bookRepository.save(bookTwo.build());

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
                "&libraryName=Mary+GrandPre" +
                "&price=8.59"))
                .andExpect(status().isCreated());
    }

    @Test
    public void addBookAndCheckSize() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=0545791340" +
                "&libraryName=Mary+GrandPre" +
                "&price=8.59"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    public void checkBadRequest() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "&libraryName=Mary+GrandPre" +
                "&price=8.59"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkBadRequestAndSize() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "&libraryName=Mary+GrandPre" +
                "&price=8.59"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    public void checkDifferentLibraryNameAndSameISBN() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=0545791340" +
                "&libraryName=Bahy" +
                "&price=8.59"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    public void checkDifferentISBNAndSameLibraryName() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=1781100527" +
                "&libraryName=Mary+GrandPre" +
                "&price=8.59"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/count-books"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    public void checkPriceDESCSorting() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "sorting-attribute=price" +
                "&sorting-direction=DESC"))
                .andExpect(jsonPath("$[0].bookKey.isbn", is("1781100500")));

    }

    @Test
    public void checkTitleASCCSorting() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "sorting-attribute=title" +
                "&sorting-direction=ASC"))
                .andExpect(jsonPath("$[0].bookKey.isbn", is("1781100500")));

    }

}
