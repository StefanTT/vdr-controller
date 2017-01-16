'use strict';

angular.module('app.recording')
       .controller('RecordingDetailsCtrl', RecordingDetailsCtrl);

function RecordingDetailsCtrl($scope, $routeParams, $filter, $window, RecordingService, DialogService)
{
  $scope.rec = RecordingService.get({ id:$routeParams.id });

  $scope.deleteAction = function()
  {
    var dialog = DialogService.confirm
    ({
      title : $filter('translate')('recording.overview.confirmDelete.title'),
      message : $filter('translate')('recording.details.confirmDelete.message'),
      acceptLabel : $filter('translate')('button.continue'),
      accept : deleteRecording
    });
  }

  function deleteRecording()
  {
      var dlg = DialogService.progress({
          title : $filter('translate')('recording.dialog.delete.title'),
          scope : $scope,
          maxValue : 1,
          step : deleteNext,
          autoClose : true,
          finished : function(){ $window.location.href = '#/recording'; }
      });

      function deleteNext(dialog)
      {
        console.log('Deleting ' + ($scope.rec.title || $routeParams.id));
        dialog.value = 1;
        dialog.message = $filter('translate')('recording.dialog.delete.text', { name : $scope.rec.name });
        return RecordingService.delete({ id : $routeParams.id }).$promise;
      }
  }
}
