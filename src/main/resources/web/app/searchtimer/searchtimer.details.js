'use strict';

angular.module('app.searchtimer')
       .controller('SearchtimerDetailsCtrl', SearchtimerDetailsCtrl);

function SearchtimerDetailsCtrl($scope, $routeParams, $filter, $window, $sanitize, SearchtimerService)
{
    $scope.id = $routeParams.id;
    $scope.timer = SearchtimerService.get({id: $routeParams.id});

    // Clock picker from http://weareoutman.github.io/clockpicker
    $('.clockpicker').clockpicker({ autoclose:true, vibrate:false });

    function minutesToTimeStr(min)
    {
        var h = Math.floor(min / 60);
        var m = min % 60;
        return '' + Math.floor(h / 10) + (h % 10)
             + ':' + Math.floor(m / 10) + (m % 10);
    }

    $scope.timer.$promise.then(() =>
    {
        if ($scope.timer.startTimeRange)
        {
            $scope.startTimeMin = minutesToTimeStr($scope.timer.startTimeRange.minimum);
            $scope.startTimeMax = minutesToTimeStr($scope.timer.startTimeRange.maximum);
        }

        $scope.compareDescription = $scope.timer.repeatsMatchDescriptionPercent != undefined;

        $scope.enableDelete = $scope.timer.deleteAfterDays > 0;
        $scope.timer.deleteAfterDays = Math.max(1, $scope.timer.deleteAfterDays);

        $scope.enableKeep = $scope.timer.keepRecordings > 0;
        $scope.timer.keepRecordings = Math.max(1, $scope.timer.keepRecordings);
    });

    $scope.$watch('timer.avoidRepeats', () =>
    {
        $('#avoidRepeats input').attr('disabled', !$scope.timer.avoidRepeats);
    });
}
