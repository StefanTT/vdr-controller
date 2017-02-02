'use strict';

angular.module('app.searchtimer')
       .controller('SearchtimerOverviewCtrl', SearchtimerOverviewCtrl);

function SearchtimerOverviewCtrl($scope, $routeParams, $filter, $location, SearchtimerService)
{
    $scope.timers = SearchtimerService.query();

    $scope.orderComp = function(a, b)
    {
        if (a.value == '')
            return 1;
        if (b.value == '')
            return -1;
        return a.value.localeCompare(b.value);
    }

    $scope.disableTimer = function(timer)
    {
        SearchtimerService.disable({id: timer.id})
            .$promise.then(() => { timer.enabled = false; });
    }

    $scope.enableTimer = function(timer)
    {
        SearchtimerService.enable({id: timer.id})
            .$promise.then(() => { timer.enabled = true; });
    }

    $scope.showDetails = function(timer)
    {
        $location.path('/searchtimer/' + timer.id);
    }
}
