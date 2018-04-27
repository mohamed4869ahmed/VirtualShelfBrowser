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

import static org.hamcrest.Matchers.hasSize;
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

    private Book bookOne;
    private Book bookTwo;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.bookRepository.deleteAllInBatch();

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

        bookRepository.save(bookOne);
        bookRepository.save(bookTwo);

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
    public void addBookWithInvalidISBN() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=1" +
                "&libraryName=Mary+GrandPre" +
                "&price=8.59"))
                .andExpect(status().isInternalServerError());
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
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookTwo.getBookKey().getISBN())));

    }

    @Test
    public void checkPriceASCCSorting() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "sorting-attribute=price" +
                "&sorting-direction=ASC"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookOne.getBookKey().getISBN())));

    }
    
    @Test
    public void checkTitleASCCSorting() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "sorting-attribute=title" +
                "&sorting-direction=ASC"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookTwo.getBookKey().getISBN())));

    }

    @Test
    public void filterByPrice() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "filter-query=price:5.74"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookOne.getBookKey().getISBN())));

    }

    @Test
    public void filterByPriceGreaterThan() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "filter-query=price>5.74"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookTwo.getBookKey().getISBN())));
    }

    @Test
    public void filterByPriceLessThan() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "filter-query=price<5.74"))
                .andExpect(content().string(""));
    }

    @Test
    public void filterEmptyPriceBadRequest() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "filter-query=price"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void filterInvalidPriceBadRequest() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "filter-query=price:string"))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    public void filterInvalidTitleBadRequest() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "filter-query=title<8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void filterByISBN() throws Exception {
        mockMvc.perform(post("/all-books?" +
                "filter-query=bookKey.ISBN:1781100217"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is(bookOne.getBookKey().getISBN())));
    }

    @Test
    public void filterByLibraryNameANDPrice() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=1781100527" +
                "&libraryName=Bahy" +
                "&price=5.74"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/all-books?" +
                "filter-query=price:5.74,bookKey.libraryName:Bahy"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].bookKey.isbn", is("1781100527")));
    }

    @Test
    public void filterByPriceAndSortByLibraryName() throws Exception {
        mockMvc.perform(post("/add-book?" +
                "ISBN=1781100527" +
                "&libraryName=Bahy" +
                "&price=5.74"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/all-books?" +
                "filter-query=price:5.74" +
                "&sorting-attribute=bookKey.libraryName" +
                "&sorting-direction=ASC"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].bookKey.libraryName", is("Bahy")))
                .andExpect(jsonPath("$[0].bookKey.isbn", is("1781100527")))
                .andExpect(jsonPath("$[1].bookKey.libraryName", is("Mary GrandPre")))
                .andExpect(jsonPath("$[1].bookKey.isbn", is("1781100217")));
    }


}
