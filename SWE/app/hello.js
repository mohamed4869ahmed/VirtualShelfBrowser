var demoApp = angular.module('demo', ['rzModule'])

demoApp.controller('IndexController', function($scope, $http) {

  localStorage.setItem("libraryNameQuery", "");
  $scope.signUpFlag = false;
  $scope.signInFlag = true;

  $scope.guest = function() {
    window.location = "./book-shelf.html";
  };

  $scope.signUp = function() {
    $scope.signUpError = "";
    var data = {
      "username": $scope.sign_up_username,
      "password": $scope.sign_up_password,
      "libraryName": $scope.sign_up_library_name
    };
    $http.post('http://localhost:8080/add-user',
        JSON.stringify(data), {
          headers: {
            'Content-Type': ' application/json'
          }
        }
      )
      .success(function(response) {
        var query = ",bookKey.libraryName:" + $scope.sign_up_library_name;
        localStorage.setItem("libraryNameQuery", query);
        localStorage.setItem("username", $scope.sign_up_username);
        localStorage.setItem("password", $scope.sign_up_password);
        window.location = "./book-store.html";
      }).error(function(status) {
        if (status == 400) {
          $scope.signUpError = "Use valid password.";
        } else {
          $scope.signUpError = "Username is already used.";
        }
      });
  };

  $scope.signIn = function() {
    $scope.signInError = "";
    var data = {
      "username": $scope.sign_in_username,
      "password": $scope.sign_in_password
    };
    $http.post('http://localhost:8080/authenticate',
        JSON.stringify(data), {
          headers: {
            'Content-Type': ' application/json'
          }
        }
      )
      .success(function(data, status, headers, config) {
        var query = ",bookKey.libraryName:" + data.libraryName;
        localStorage.setItem("libraryNameQuery", query);
        localStorage.setItem("username", $scope.sign_in_username);
        localStorage.setItem("password", $scope.sign_in_password);
        window.location = "./book-store.html";
      }).error(function(data, status, headers, config) {
        if (status == 404) {
          $scope.signInError = "Username is not found.";
        } else {
          $scope.signInError = "Incorrect Password.";
        }
      });
  };

});

demoApp.controller('UserController', function($scope, $http) {

  $scope.add_book_flag = true;
  $scope.username = localStorage.getItem("username");

  $scope.guest = function() {
    window.location = "./book-shelf.html";
  };

  $scope.addBook = function() {
    var data = {
      "username": localStorage.getItem("username"),
      "password": localStorage.getItem("password")
    };
    $http.post('http://localhost:8080/add-book?ISBN=' +
        $scope.isbn +
        "&price=" +
        $scope.price,
        JSON.stringify(data), {
          headers: {
            'Content-Type': ' application/json'
          }
        }
      )
      .success(function(response) {
        alert("Done");
      }).error(function(data, status, headers, config) {
        if (status == 400) {
          alert("The price must not be null");
        } else {
          alert("Invalid Book.")
        }
      });
  };

  $scope.removeBook = function() {
    var data = {
      "username": localStorage.getItem("username"),
      "password": localStorage.getItem("password")
    };
    $http.post('http://localhost:8080/remove-book?ISBN=' +
        $scope.isbn,
        JSON.stringify(data), {
          headers: {
            'Content-Type': ' application/json'
          }
        }
      )
      .success(function(response) {
        alert("Done");
      }).error(function(data, status, headers, config) {
        alert("The Book does not exist.");
      });
  };

});

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
      $scope.ratingSlider.minValue +
      localStorage.getItem("libraryNameQuery")
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
