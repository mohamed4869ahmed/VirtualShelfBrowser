function showDetails() {
    var book = JSON.parse(localStorage.getItem("book"));
    document.title = book.title;
    document.getElementById('title').innerHTML = book.title;
    document.getElementById('author').innerHTML = book.author;
    document.getElementById('publicationDate').innerHTML = book.publicationDate;
    document.getElementById('publisher').innerHTML = book.publisher;
    document.getElementById('category').innerHTML = book.category;
    document.getElementById('library').innerHTML = book.bookKey.libraryName;
    document.getElementById('price').innerHTML = book.price;
    document.getElementById('ISBN').innerHTML = book.bookKey.isbn;
    document.getElementById('description').innerHTML = book.description;
    document.getElementById('rating').innerHTML = book.rating;
    document.getElementById('previewLink').href = book.previewLink;
    document.getElementById('image').src = book.image;
}
