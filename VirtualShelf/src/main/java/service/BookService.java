package service;

import com.google.api.services.books.model.Volume;
import model.Book;
import model.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private GoogleAPIService googleAPIService;

    public Long size() {
        return bookRepository.count();
    }

    public Iterable<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Boolean addBook(String isbn, String libraryName, Double price) {

        Volume.VolumeInfo volumeInfo = googleAPIService.getBookByISBN(isbn);

        if (volumeInfo == null) {
            return false;
        }

        Book book = new Book.BookBuilder()
                .title(volumeInfo.getTitle())
                .author(volumeInfo.getAuthors() == null? null : volumeInfo.getAuthors().get(0))
                .category(volumeInfo.getCategories() == null? null : volumeInfo.getCategories().get(0))
                .description(volumeInfo.getDescription())
                .image(volumeInfo.getImageLinks().getSmallThumbnail())
                .previewLink(volumeInfo.getPreviewLink())
                .publishDate(volumeInfo.getPublishedDate())
                .publisher(volumeInfo.getPublisher())
                .rating(volumeInfo.getAverageRating())
                .price(price)
                .ISBN(isbn)
                .libraryName(libraryName)
                .build();

        bookRepository.save(book);
        return true;
    }
}