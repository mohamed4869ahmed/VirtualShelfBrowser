package service;

import model.Book;
import model.BookRepository;
import model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public Long size() {
        return bookRepository.count();
    }

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Boolean addBook(String ISBN, String libraryName, String title, String author, String publisher, Integer year, Double price, String category) {

        if(category != null && !Stream.of(Category.values()).map(Category::name).collect(Collectors.toList()).contains(category)){
            return false;
        }

        Book.BookBuilder bookBuilder = new Book.BookBuilder()
                .ISBN(ISBN)
                .libraryName(libraryName)
                .title(title)
                .author(author)
                .publisher(publisher)
                .year(year)
                .price(price)
                .category(category);

        bookRepository.save(bookBuilder.build());
        return true;
    }
}
