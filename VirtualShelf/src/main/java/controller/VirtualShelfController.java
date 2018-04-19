package controller;

import com.google.common.collect.Iterables;
import model.Book;
import model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BookService;

@RestController
public class VirtualShelfController {

    @Autowired
    private BookService bookService;

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping("/allBooks")
    public @ResponseBody
    ResponseEntity<Iterable<Book>> getBooks() {
        Iterable<Book> books = bookService.getAllBooks();
        if (Iterables.size(books) == 0) {
            return new ResponseEntity<Iterable<Book>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Iterable<Book>>(bookService.getAllBooks(), HttpStatus.OK);
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping("/countBooks")
    public @ResponseBody
    ResponseEntity<Long> countBooks() {
        return new ResponseEntity<Long>(bookService.size(), HttpStatus.OK);
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping("/addBook")
    public @ResponseBody
    ResponseEntity addBooks(@RequestParam(name = "ISBN") String ISBN,
                            @RequestParam(name = "libraryName") String libraryName,
                            @RequestParam(name = "title") String title,
                            @RequestParam(name = "author", required = false) String author,
                            @RequestParam(name = "publisher", required = false) String publisher,
                            @RequestParam(name = "year", required = false) Integer year,
                            @RequestParam(name = "price", required = false) Double price,
                            @RequestParam(name = "category", required = false) String category) {

        Boolean done = bookService.addBook(ISBN, libraryName, title, author, publisher, year, price, category);
        if(done) {
            return new ResponseEntity(HttpStatus.CREATED);
        }else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
