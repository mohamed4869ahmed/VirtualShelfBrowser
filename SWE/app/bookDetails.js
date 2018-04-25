function showDetails(){
  var book = JSON.parse(localStorage.getItem("book"));
  document.title = book.title;
  document.getElementById('title').innerHTML = book.title;
  document.getElementById('author').innerHTML = book.author;
  document.getElementById('publishDate').innerHTML = book.publishDate;
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


/*


<li> Title: <div id="title"> </div> </li>
<li> Author: <div id="author"> </div> </li>
<li> ISBN: <div id="ISBN"> </div> </li>
<li> Price: <div id="price"> </div> </li>
<li> Publisher: <div id="publisher"> </div> </li>
<li> Library: <div id="library"> </div> </li>
<li> Year: <div id="year"> </div> </li>
<li> Category: <div id="category"> </div> </li>


    <dl>
      <dt> Title: </dt> <dd id="title"> </dd></dt>
      <dt> Author: </dt> <dd id="author"> </dd> </dt>
      <dt> ISBN: </dt> <dd id="ISBN"> </dd> </dt>
      <dt> Price: </dt> <dd id="price"> </dd> </dt>
      <dt> Publisher: </dt> <dd id="publisher"> </dd> </dt>
      <dt> Library: </dt> <dd id="library"> </dd> </dt>
      <dt> Year: </dt> <dd id="year"> </dd> </dt>
      <dt> Category: </dt> <dd id="category"> </dd> </dt>
    </dl>

*/
