var demoApp = angular.module('demo', ['rzModule'])

demoApp.controller('BooksController', function($scope, $http) {

    $scope.ratingSlider = {
        minValue: 0,
        maxValue: 5,
        options: {
            floor: 0,
            ceil: 5,
            step: 0.5,
            precision: 1
        }
    };

    $scope.priceSlider = {
        minValue: 0,
        maxValue: 500,
        options: {
            floor: 0,
            ceil: 500,
            step: 1,
        }
    };

    $scope.refreshSlider = function() {
        $timeout(function() {
            $scope.$broadcast('rzSliderForceRender');
        });
    };

    $scope.sorting_direction = "ASC";
    $scope.sorting_attribute = "title";

    $scope.update = function() {
        $http.get('http://localhost:8080/all-books?sorting-attribute=' +
            $scope.sorting_attribute +
            '&sorting-direction=' +
            $scope.sorting_direction +
            '&filter-query=price>' +
            $scope.priceSlider.minValue +
            ',price<' +
            $scope.priceSlider.maxValue +
            ',rating<' +
            $scope.ratingSlider.maxValue +
            ',rating>' +
            $scope.ratingSlider.minValue
        ).
        then(function(response) {
            $scope.books = response.data;
        });
    };

    $scope.ratingSlider.options.onChange = $scope.update;
    $scope.priceSlider.options.onChange = $scope.update;

    $scope.update();

    $scope.showBookDetails = function(book) {
        localStorage.setItem("book", JSON.stringify(book));
        window.location = "./details.html";
    };
});
