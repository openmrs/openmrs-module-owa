// Manage Apps page

var owaAppManager = angular.module('owaAppManager', []);

owaAppManager.directive('fileModel', function($parse){
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            element.bind('change', function() {
                scope.$apply(function() {
                    model.assign(scope, element[0].files[0]);
                });
            });
        }
    };
});

owaAppManager.controller('AppUploadCtrl', function($scope, $http) {
    $scope.uploadApp = function() {
        var fd = new FormData();
        var f = $scope.file;
        fd.append('file', f);
        $http({
            method: 'POST',
            url: '/openmrs/ws/rest/owa/addapp',
            data: fd,
            transformRequest: angular.identity,
            headers: {'Content-Type': undefined}
        }).then(function success(response) {
             window.location.reload();
        }, function error(response) {
            console.log(response)
        });
    }
});

owaAppManager.controller('AppListCtrl', function($scope, $http) {
    $http({
        method: 'GET',
        url: '/openmrs/ws/rest/owa/applist'
    }).then(function success(response) {
        $scope.appList = response.data;
    }, function error(response) {
        console.log(response);
    });
    
    $scope.location = function(uri) {
        window.location.href = uri;
    }

    $scope.deleteApp = function(appName) {
        var result = window.confirm('Do you want to delete this app?' + '\n\n' + appName);
        if (result) {
            $http({
                method: 'GET',
                url: '/openmrs/ws/module/owa/deleteApp?appName=' + appName
            }).then(function success(response) {
                window.location = '/openmrs/ws/module/owa/deleteApp?appName=' + appName;
            }, function error(response) {
                console.log(response);
            });
        }
    }
}

// Settings page

var owaSettings = angular.module('owaSettings', []);

owaSettings.controller('SettingsFormCtrl', function($scope, $http) {
    $http({
        method: 'GET',
        url: '/openmrs/ws/rest/owa/settings'
    }).then(function success(response) {
        $scope.settingsList = response.data;
    }, function error(response) {
        console.log(response);
    });


    $scope.save = function() {
        console.log($scope);
        $http({
            method: 'POST',
            url: '/openmrs/ws/rest/owa/settings',
            data: {
                properties: $scope.settingsList
            }
        }).then(function success(response) {
            window.location.reload();
        }, function error(response) {
            console.log(response);
        });
    }
});
