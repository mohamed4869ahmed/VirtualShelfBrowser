var demoApp = angular.module('demo', [])

demoApp.controller('BooksController', function($scope, $http) {

    $scope.sorting_direction = "ASC";
    $scope.sorting_attribute = "title";

    $scope.update = function() {
        $http.get('http://localhost:8080/all-books?sorting-attribute=' +
            $scope.sorting_attribute + '&sorting-direction=' +
            $scope.sorting_direction).
        then(function(response) {
            $scope.books = response.data;
        });
    };

    $scope.update();

    $scope.showBookDetails = function(book) {
        localStorage.setItem("book", JSON.stringify(book));
        window.location = "./details.html";
    };
});
