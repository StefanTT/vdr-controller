'use strict';

angular.module('app.timer')
  .controller('TimerOverviewCtrl', TimerOverviewCtrl);

function TimerOverviewCtrl($scope, $routeParams, $filter, TimerService)
{
  $scope.loading = true;
  $scope.timers = TimerService.query();
  $scope.timers.$promise.then(function(){ $scope.loading = false; })
}
