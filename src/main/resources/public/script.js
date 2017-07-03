var app = angular.module("logViewer", ["ngRoute"]);
app.config(function($routeProvider) {
    $routeProvider
        .when("/", {
            templateUrl : "home.html",
        })
        .when("/viewer", {
            templateUrl : "viewer.html",
            controller : "viewerCtrl"
        })
        .when("/contact", {
            templateUrl : "contact.html",
            controller : "contactCtrl"
        });
});
app.controller("viewerCtrl", function ($scope, $http, $filter, $interval) {

    var orderBy = $filter('orderBy');

    $scope.logTypeFilterLock = false;
    $scope.sourceFilterLock = false;
    $scope.startDateFilterLock = true;
    $scope.endDateFilterLock = false;
    $scope.logTypeFilter = "";
    $scope.sourceFilter ="";
    $scope.additionalFieldsFilter = {};
    $scope.additionalFieldsChecker = {};


    var today = new Date();
    tMonth = today.getMonth().toString();
    var previousMonth = new Date(today.setMonth(tMonth-1));
    pMonth = previousMonth.getMonth().toString();
    pYear = previousMonth.getFullYear().toString();
    pDay = previousMonth.getDay().toString();
    $scope.startDateFilter = pYear + "-" + pMonth + "-" + pDay + " 12:00:00";

    var getObject = new Object();
    var getObjectReady = false;
    var lastlyUsedUrl = "http://localhost:8080/logs/complete/period/" + $scope.startDateFilter;
    $http.get(lastlyUsedUrl)
        .then(function (response) {
            $scope.response = response.data;
            $scope.logs = response.data.logs;
        });


    $interval(function () {
        update();
    }, 30000);

    var update = function () {
        if(!$scope.logTypeFilterLock) {

            $http.get(lastlyUsedUrl)
                .then(function (response) {
                    $scope.response = response.data;
                    $scope.logs = response.data.logs;
                });
        } else {
            if(getObjectReady == true) {
                sendGetLog(getObject);
            }
        }
    };

    $scope.filter = function () {

        if ($scope.logTypeFilterLock) {


            getObject.logName = $scope.logTypeFilter;
            getObject.startTime = $scope.startDateFilter;
            getObject.endTime = null;
            getObject.source = null;
            getObject.otherFieldsValues =[];
            getObject.otherFieldsNames =[];

            angular.forEach($scope.additionalFieldsFilter, function(value, key) {
                if($scope.additionalFieldsChecker[key]) {
                    getObject.otherFieldsValues.push(value);
                    getObject.otherFieldsNames.push(key);
                }
            });

            if ($scope.sourceFilterLock) {
                getObject.source = $scope.sourceFilter;
            }
            if ($scope.endDateFilterLock) {
                getObject.endTime = $scope.endDateFilter;
            }

            sendGetLog(getObject);
            $http.get("http://localhost:8080/logs/logType/" + $scope.logTypeFilter)
                .then(function (response) {
                    $scope.logName = response.data;

                });
            getObjectReady = true;
        } else {
            if ($scope.sourceFilterLock) {
                if ($scope.endDateFilterLock) {
                    lastlyUsedUrl = "http://localhost:8080/logs/complete/source/" + $scope.sourceFilter + "/" +
                        $scope.startDateFilter + "/" + $scope.endDateFilter;
                    $http.get(lastlyUsedUrl)
                        .then(function (response) {
                            $scope.response = response.data;
                            $scope.logs = response.data.logs;

                        });
                } else {
                    lastlyUsedUrl = "http://localhost:8080/logs/complete/source/" +
                        $scope.sourceFilter + "/" + $scope.startDateFilter;
                    $http.get(lastlyUsedUrl)
                        .then(function (response) {
                            $scope.response = response.data;
                            $scope.logs = response.data.logs;
                        });
                }
            } else {
                if ($scope.endDateFilterLock) {
                    lastlyUsedUrl = "http://localhost:8080/logs/complete/period/" + $scope.startDateFilter + "/" + $scope.endDateFilter;
                    $http.get(lastlyUsedUrl)
                        .then(function (response) {
                            $scope.response = response.data;
                            $scope.logs = response.data.logs;
                        });
                } else {
                    lastlyUsedUrl = "http://localhost:8080/logs/complete/period/" + $scope.startDateFilter;
                    $http.get(lastlyUsedUrl)
                        .then(function (response) {
                            $scope.response = response.data;
                            $scope.logs = response.data.logs;
                        });
                }
            }
        }

    };

    $scope.orderByDate = function(item) {
        var parts = item.timestamp.split(' ');
        var date = new Date();
        var stringDate = parts[0];
        var stringTime = parts[1];
        var dateParts = stringDate.split('-');
        date.setFullYear(parseInt(dateParts[0]),parseInt(dateParts[1]),parseInt(dateParts[2]));

        return date;
    };

    $scope.orderByIp = function(item) {
        var octets = item.source.split('.');
        var number = parseInt(octets[3]) +  parseInt(octets[2]) * 300 +  parseInt(octets[1]) * 77000 +  parseInt(octets[0]) * 20000000;

        return number;
    };

    $scope.order = function(x, reverse) {
        if(x == 0) {
            var column = "id";
            $scope.logRecords = orderBy($scope.logRecords,column,reverse);
        } else  if(x == 1) {
            var column = "source";
            $scope.logRecords = orderBy($scope.logRecords,$scope.orderByIp,reverse);
        } else if(x == 2) {
            var column = "timestamp";
            $scope.logRecords = orderBy($scope.logRecords,column,reverse);
        } else {
            var y =x -3;
            var column = "otherFields[" + y.toString() + "]";
            $scope.logRecords = orderBy($scope.logRecords,column,reverse);
        }

    };

    $scope.order(0);

    $scope.removeItem = function (name,id) {

        var postObject = new Object();
        postObject.logName = name;
        postObject.id = id;

        $http({
            url: 'http://localhost:8080/logs/logRecord',
            dataType: 'json',
            method: 'DELETE',
            data: postObject,
            headers: {
                "Content-Type": "application/json"
            }
        }).then(function () {
            update();
        });

        $scope.updateDevices();
    };

    var sendGetLog = function(getObject) {
        $http({
            url: "http://localhost:8080/logs/getLogRecords",
            dataType: 'json',
            method: 'POST',
            data: getObject,
            headers: {
                "Content-Type": "application/json"
            }
        }).then(function (response) {
            $scope.logRecords = response.data;
        });
    }

});
app.controller("contactCtrl", function ($scope, $http) {
    
});
app.directive('dynamicModel', ['$compile', '$parse', function ($compile, $parse) {
    return {
        restrict: 'A',
        terminal: true,
        priority: 100000,
        link: function (scope, elem) {
            var name = $parse(elem.attr('dynamic-model'))(scope);
            elem.removeAttr('dynamic-model');
            elem.attr('ng-model', name);
            $compile(elem)(scope);
        }
    };
}]);





