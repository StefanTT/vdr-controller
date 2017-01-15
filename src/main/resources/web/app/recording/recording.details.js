'use strict';

angular.module('app.recording')
       .controller('RecordingDetailsCtrl', RecordingDetailsCtrl);

function RecordingDetailsCtrl($scope, $routeParams, $filter, RecordingService)
{
  $scope.rec = RecordingService.get({ id:$routeParams.id });
}
