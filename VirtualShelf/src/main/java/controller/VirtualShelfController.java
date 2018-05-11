package controller;

import com.google.common.collect.Iterables;
import com.querydsl.core.types.dsl.BooleanExpression;
import model.Book;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.BookService;
import service.UserService;
import utility.BookPredicatesBuilder;
import utility.SearchCriteria;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
public class VirtualShelfController {

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping(value = "/all-books", method = GET)
    public @ResponseBody
    ResponseEntity<Iterable<Book>> getBooks(@RequestParam(name = "sorting-attribute", required = false) String sortingAttribute,
                                            @RequestParam(name = "sorting-direction", required = false) String sortingDirection,
                                            @RequestParam(name = "filter-query", required = false) String searchCriteria
    ) {
        BooleanExpression exp = null;
        if (searchCriteria != null && !searchCriteria.isEmpty()) {
            BookPredicatesBuilder builder = new BookPredicatesBuilder();
            Pattern pattern = Pattern.compile("([\\w|.]+?)(:|<|>)([\\w|.| ]+?),");
            Matcher matcher = pattern.matcher(searchCriteria + ",");
            while (matcher.find()) {
                builder.with(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
            }
            exp = builder.build();
            if (exp == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        Iterable<Book> books = bookService.getAllBooks(sortingAttribute, sortingDirection, exp);
        if (Iterables.size(books) == 0) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping(value = "/count-books", method = GET)
    public @ResponseBody
    ResponseEntity<Long> countBooks() {
        return new ResponseEntity<Long>(bookService.size(), HttpStatus.OK);
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping(value = "/add-book", method = POST)
    public @ResponseBody
    ResponseEntity addBook(@RequestParam(name = "ISBN") String ISBN,
                           @RequestParam(name = "price", required = false) Double price,
                           @RequestBody User user) {

        if (!userService.authenticate(user)) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        Boolean done = bookService.addBook(ISBN, userService.getLibraryName(user), price);
        if (done) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping(value = "/remove-book", method = POST)
    public @ResponseBody
    ResponseEntity removeBook(@RequestParam(name = "ISBN") String ISBN,
                              @RequestBody User user) {

        if (!userService.authenticate(user) || !bookService.exist(ISBN, userService.getLibraryName(user))) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        bookService.removeBook(ISBN, userService.getLibraryName(user));
        return new ResponseEntity(HttpStatus.OK);
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping(value = "/add-user", method = POST)
    public @ResponseBody
    ResponseEntity addUser(@RequestBody User user) {
        if (userService.checkIfExists(user) || userService.checkIfLibraryNameExists(user)) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        if (user.getPassword() == null || user.getLibraryName() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        userService.addUser(user);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @CrossOrigin(exposedHeaders = "Access-Control-Allow-Origin")
    @RequestMapping(value = "/authenticate", method = POST)
    public @ResponseBody
    ResponseEntity<User> authenticate(@RequestBody User user) {
        if (!userService.checkIfExists(user)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (userService.authenticate(user)) {
            user.setLibraryName(userService.getLibraryName(user));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
