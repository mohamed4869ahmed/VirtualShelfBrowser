package controller;

import Utility.BookPredicatesBuilder;
import Utility.SearchCriteria;
import com.google.common.collect.Iterables;
import com.querydsl.core.types.dsl.BooleanExpression;
import model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BookService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class VirtualShelfController {

    @Autowired
    private BookService bookService;

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping("/all-books")
    public @ResponseBody
    ResponseEntity<Iterable<Book>> getBooks(@RequestParam(name = "sorting-attribute", required = false) String sortingAttribute,
                                            @RequestParam(name = "sorting-direction", required = false) String sortingDirection,
                                            @RequestParam(name = "filter-query", required = false) String searchCriteria
    ) {
        BooleanExpression exp = null;

        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            System.out.println(searchCriteria);
            BookPredicatesBuilder builder = new BookPredicatesBuilder();
            Pattern pattern = Pattern.compile("([\\w|.]+?)(:|<|>)([\\w|.]+?),");
            Matcher matcher = pattern.matcher(searchCriteria + ",");
            while (matcher.find()) {
                System.out.println("Hi" + matcher.group(1) + matcher.group(2) + matcher.group(3));
                builder.with(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
            }
            exp = builder.build();
            if (exp == null) {
                System.out.println("Null");
                return new ResponseEntity<Iterable<Book>>(HttpStatus.BAD_REQUEST);
            }
        }

        Iterable<Book> books = bookService.getAllBooks(sortingAttribute, sortingDirection, exp);
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
