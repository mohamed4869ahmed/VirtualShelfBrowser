var demoApp = angular.module('demo', [])

demoApp.controller('BooksController', function($scope, $http) {
    $http.get('http://localhost:8080/all-books').
    then(function(response) {
        $scope.books = response.data;
    });

    $scope.showBookDetails = function(book) {
        localStorage.setItem("book", JSON.stringify(book));
        window.location = "./details.html";
    };
});
