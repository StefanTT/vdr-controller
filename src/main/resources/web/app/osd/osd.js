'use strict';

angular.module('app.osd')
  .controller('OsdCtrl', OsdCtrl);

function OsdCtrl($scope, $routeParams, $filter, OsdService)
{
  $scope.items = OsdService.query();

  $scope.items.$promise.then(function()
  {
    if ($scope.items.length == 0)
      $scope.key('setup');
  });

  $scope.key = function(key)
  {
    OsdService.key(key).then(function()
    {
      var newItems = OsdService.query();
      newItems.$promise.then(function(){ $scope.items = newItems; });
    });
  }
}
