package controller;

import com.google.common.collect.Iterables;
import model.Book;
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
    @RequestMapping("/all-books")
    public @ResponseBody
    ResponseEntity<Iterable<Book>> getBooks(@RequestParam(name = "sorting-attribute", required = false) String sortingAttribute,
                                            @RequestParam(name = "sorting-direction", required = false) String sortingDirection) {
        Iterable<Book> books = bookService.getAllBooks(sortingAttribute, sortingDirection);
        if (Iterables.size(books) == 0) {
            return new ResponseEntity<Iterable<Book>>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<Iterable<Book>>(books, HttpStatus.OK);
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping("/count-books")
    public @ResponseBody
    ResponseEntity<Long> countBooks() {
        return new ResponseEntity<Long>(bookService.size(), HttpStatus.OK);
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping("/add-book")
    public @ResponseBody
    ResponseEntity addBook(@RequestParam(name = "ISBN") String ISBN,
                           @RequestParam(name = "libraryName") String libraryName,
                           @RequestParam(name = "price", required = false) Double price) {

        Boolean done = bookService.addBook(ISBN, libraryName, price);
        if (done) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
