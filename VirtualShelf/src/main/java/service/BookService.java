package service;

import com.google.api.services.books.model.Volume;
import com.querydsl.core.types.dsl.BooleanExpression;
import model.Book;
import model.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    public Iterable<Book> getAllBooks(String sortingAttribute, String sortingDirection, BooleanExpression booleanExpression) {
        if (sortingAttribute == null && booleanExpression == null) {
            return bookRepository.findAll();
        } else if ( booleanExpression == null) {
            return bookRepository.findAll(new Sort(convertToSortDirection(sortingDirection), sortingAttribute));
        }  else if ( sortingAttribute == null){
            return bookRepository.findAll(booleanExpression);
        }
        else {
            return bookRepository.findAll(booleanExpression
                    ,new Sort(convertToSortDirection(sortingDirection), sortingAttribute));
        }
    }

    public Boolean addBook(String isbn, String libraryName, Double price) {

        Volume.VolumeInfo volumeInfo = googleAPIService.getBookByISBN(isbn);

        if (volumeInfo == null) {
            return false;
        }

        Book book = new Book.BookBuilder()
                .title(volumeInfo.getTitle())
                .author(volumeInfo.getAuthors() == null ? null : volumeInfo.getAuthors().get(0))
                .category(volumeInfo.getCategories() == null ? null : volumeInfo.getCategories().get(0))
                .description(volumeInfo.getDescription())
                .image(volumeInfo.getImageLinks().getSmallThumbnail())
                .previewLink(volumeInfo.getPreviewLink())
                .publicationDate(volumeInfo.getPublishedDate())
                .publisher(volumeInfo.getPublisher())
                .rating(volumeInfo.getAverageRating())
                .price(price)
                .ISBN(isbn)
                .libraryName(libraryName)
                .build();

        bookRepository.save(book);
        return true;
    }

    private Sort.Direction convertToSortDirection(String sortingDirection) {
        if (sortingDirection == null || sortingDirection.equals("ASC")) {
            return Sort.Direction.ASC;
        } else {
            return Sort.Direction.DESC;
        }
    }
}
