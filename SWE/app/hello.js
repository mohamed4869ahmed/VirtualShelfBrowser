var demoApp = angular.module('demo', [])

demoApp.controller('BooksController', function($scope, $http) {
    $http.get('http://localhost:8080/allBooks').
    then(function(response) {
        $scope.books = response.data;
    });

    $scope.showBookDetails = function(book) {
        localStorage.setItem("book", JSON.stringify(book));
        window.location = "./details.html";
    };
});
