package model;

import javax.persistence.*;
import java.io.Serializable;

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
    @Column(name = "publish_date")
    private String publishDate;
    @Column(name = "image")
    private String image;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    @Column(name = "category")
    private String category;
    @Column(name = "preview_link")
    private String previewLink;
    @Column(name = "price")
    private Double price;
    @Column(name = "rating")
    private Double rating;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPreviewLink() {
        return previewLink;
    }

    public void setPreviewLink(String previewLink) {
        this.previewLink = previewLink;
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
        private String publishDate;
        private String category;
        private String description;
        private String image;
        private String previewLink;
        private Double price;
        private Double rating;

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

        public BookBuilder publishDate(String publishDate) {
            this.publishDate = publishDate;
            return this;
        }

        public BookBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public BookBuilder category(String category) {
            this.category = category;
            return this;
        }

        public BookBuilder rating(Double rating) {
            this.rating = rating;
            return this;
        }

        public BookBuilder image(String image) {
            this.image = image;
            return this;
        }

        public BookBuilder previewLink(String previewLink) {
            this.previewLink = previewLink;
            return this;
        }

        public BookBuilder description(String description) {
            this.description = description;
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
            book.setPublishDate(publishDate);
            book.setPrice(price);
            book.setCategory(category);
            book.setRating(rating);
            book.setImage(image);
            book.setDescription(description);
            book.setPreviewLink(previewLink);
            return book;
        }
    }

}
