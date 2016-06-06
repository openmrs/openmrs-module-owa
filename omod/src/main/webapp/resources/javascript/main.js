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
