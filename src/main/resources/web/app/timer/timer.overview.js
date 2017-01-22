'use strict';

angular.module('app.timer')
  .controller('TimerOverviewCtrl', TimerOverviewCtrl);

function TimerOverviewCtrl($scope, $routeParams, $filter, TimerService)
{
  $scope.timers = TimerService.query();
  $scope.showDisabled = true;

  $scope.day = null;
  $scope.isNewDay = function(timer)
  {
      var date = new Date(timer.startTime);
      var lastDay = $scope.day;
      $scope.day = date.getDay();
      return lastDay != $scope.day;
  }

  $scope.disableTimer = function(timer)
  {
      TimerService.disable(timer.id)
          .then(() => { timer.enabled = false; });
  }

  $scope.enableTimer = function(timer)
  {
      TimerService.enable(timer.id)
          .then(() => { timer.enabled = true; });
  }
}
