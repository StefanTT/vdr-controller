'use strict';

angular.module('app.setup')
  .controller('SetupCtrl', SetupCtrl);

function SetupCtrl($scope, $routeParams, $filter, $timeout, SetupService)
{
  $scope.config = SetupService.get();

  $scope.save = function()
  {
    SetupService.save($scope.config).$promise
      .then(function()
      {
        $scope.saveMessage = $filter('translate')('setup.save.success');
        $timeout(function(){ $scope.saveMessage = ''; }, 2000);
      });
  }

  $scope.cancel = function()
  {
    $scope.config = SetupService.get();
  }
}
