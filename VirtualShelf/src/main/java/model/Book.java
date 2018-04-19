package model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Arrays;

@Entity
@Table(name = "book")
public class Book {

    @EmbeddedId
    private BookKey bookKey;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "publisher")
    private String publisher;
    @Column(name = "year")
    private Integer year;
    @Column(name = "price")
    private Double price;
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    public BookKey getBookKey() {
        return bookKey;
    }

    public void setBookKey(BookKey bookKey) {
        this.bookKey = bookKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public model.Category getCategory() {
        return category;
    }

    public void setCategory(model.Category category) {
        this.category = category;
    }

    @Embeddable
    public static class BookKey implements Serializable {

        private static final long serialVersionUID = 1L;

        @Column(name = "ISBN", nullable = false)
        private String ISBN;
        @Column(name = "library_name", nullable = false)
        private String libraryName;

        public String getISBN() {
            return ISBN;
        }

        public void setISBN(String ISBN) {
            this.ISBN = ISBN;
        }

        public String getLibraryName() {
            return libraryName;
        }

        public void setLibraryName(String libraryName) {
            this.libraryName = libraryName;
        }
    }

    public static class BookBuilder {

        private String ISBN;
        private String libraryName;
        private String title;
        private String author;
        private String publisher;
        private Integer year;
        private Double price;
        private Category category;

        public BookBuilder ISBN(String ISBN) {
            this.ISBN = ISBN;
            return this;
        }

        public BookBuilder libraryName(String libraryName) {
            this.libraryName = libraryName;
            return this;
        }

        public BookBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BookBuilder author(String author) {
            this.author = author;
            return this;
        }

        public BookBuilder publisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public BookBuilder year(Integer year) {
            this.year = year;
            return this;
        }

        public BookBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public BookBuilder category(String category) {
            if(category != null)
                this.category = Category.valueOf(category);
            return this;
        }

        public Book build() {
            Book book = new Book();
            BookKey bookKey = new BookKey();
            bookKey.setISBN(ISBN);
            bookKey.setLibraryName(libraryName);
            book.setBookKey(bookKey);
            book.setTitle(title);
            book.setAuthor(author);
            book.setPublisher(publisher);
            book.setYear(year);
            book.setPrice(price);
            book.setCategory(category);
            return book;
        }
    }

}
