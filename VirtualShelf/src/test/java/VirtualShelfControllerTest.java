import model.Book;
import model.BookRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

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
    public void setup(){
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        this.bookRepository.deleteAllInBatch();

        Book.BookBuilder bookOne = new Book.BookBuilder()
                .title("Harry Potter and the Philosophers Stone")
                .author("J.K. Rowling")
                .ISBN("1")
                .libraryName("Mary GrandPre")
                .price(32.74);

        Book.BookBuilder bookTwo = new Book.BookBuilder()
                .title("Harry Potter and the Chamber of Secrets")
                .author("J.K. Rowling")
                .ISBN("2")
                .libraryName("Mary GrandPre")
                .price(7.39);

        bookRepository.save(bookOne.build());
        bookRepository.save(bookTwo.build());

    }

    @Test
    public void getSize() throws Exception {
        mockMvc.perform(get("/countBooks"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    public void addBook() throws Exception{
        mockMvc.perform(post("/addBook?" +
                "ISBN=3" +
                "&libraryName=Mary+GrandPre" +
                "&title=Harry+Potter+and+the+Prisoner+of+Azkaban" +
                "&author=J.+K.+Rowling" +
                "&price=8.59"))
                .andExpect(status().isCreated());
    }

    @Test
    public void addBookAndCheckSize() throws Exception{
        mockMvc.perform(post("/addBook?" +
                "ISBN=3" +
                "&libraryName=Mary+GrandPre" +
                "&title=Harry+Potter+and+the+Prisoner+of+Azkaban" +
                "&author=J.+K.+Rowling" +
                "&price=8.59"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/countBooks"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    public void checkBadRequest() throws Exception{
        mockMvc.perform(post("/addBook?" +
                "&libraryName=Mary+GrandPre" +
                "&title=Harry+Potter+and+the+Prisoner+of+Azkaban" +
                "&author=J.+K.+Rowling" +
                "&price=8.59"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void checkBadRequestAndSize() throws Exception{
        mockMvc.perform(post("/addBook?" +
                "&libraryName=Mary+GrandPre" +
                "&title=Harry+Potter+and+the+Prisoner+of+Azkaban" +
                "&author=J.+K.+Rowling" +
                "&price=8.59"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/countBooks"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    public void checkInvalidCategory() throws Exception{
        mockMvc.perform(post("/addBook?" +
                "ISBN=3" +
                "&libraryName=Mary+GrandPre" +
                "&title=Harry+Potter+and+the+Prisoner+of+Azkaban" +
                "&author=J.+K.+Rowling" +
                "&price=8.59" +
                "&category=sport"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void checkValidCategory() throws Exception{
        mockMvc.perform(post("/addBook?" +
                "ISBN=3" +
                "&libraryName=Mary+GrandPre" +
                "&title=Harry+Potter+and+the+Prisoner+of+Azkaban" +
                "&author=J.+K.+Rowling" +
                "&price=8.59" +
                "&category=Art"))
                .andExpect(status().isCreated());
    }

    @Test
    public void checkDifferentLibraryNameAndSameISBN() throws Exception{
        mockMvc.perform(post("/addBook?" +
                "ISBN=1" +
                "&libraryName=Bahy" +
                "&title=Harry+Potter+and+the+Prisoner+of+Azkaban" +
                "&author=J.+K.+Rowling" +
                "&price=8.59"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/countBooks"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }

    @Test
    public void checkDifferentISBNAndSameLibraryName() throws Exception{
        mockMvc.perform(post("/addBook?" +
                "ISBN=4" +
                "&libraryName=Mary+GrandPre" +
                "&title=Harry+Potter+and+the+Prisoner+of+Azkaban" +
                "&author=J.+K.+Rowling" +
                "&price=8.59"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/countBooks"))
                .andExpect(status().isOk())
                .andExpect(content().string("3"));
    }


}
