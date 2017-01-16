'use strict';

angular.module('app.dialog')
       .factory('DialogService', DialogService);


function DialogService($uibModal, $filter)
{
  var service = {};

  /**
   * Open a confirmation dialog.
   */
  service.confirm = function(args)
  {
    return $uibModal.open({
      animation : true,
      size : 'md',
      resolve : { args : args },
      backdrop : true,
      templateUrl : '/app/dialog/confirm.html',
      controller : DialogServiceConfirmCtrl
    });
  }

  /**
   * Open a progress dialog.
   */
  service.progress = function(args)
  {
    return $uibModal.open({
      animation : true,
      size : 'md',
      scope : args.scope,
      resolve : { args : args },
      backdrop : 'static',
      keyboard : args.keyboard,
      templateUrl : '/app/dialog/progress.html',
      controller : DialogServiceProgressCtrl
    });
  }

  /**
   * Open a folder selection dialog.
   */
  service.selectFolder = function(args)
  {
    return $uibModal.open({
      animation : true,
      size : 'md',
      templateUrl : '/app/dialog/selectFolder.html',
      controller : DialogServiceSelectFolderCtrl
    });
  }

  return service;
}


function DialogServiceConfirmCtrl($scope, $filter)
{
    var args = $scope.$resolve.args || {};

    $scope.title = args.title;
    $scope.message = args.message;
    $scope.acceptLabel = args.acceptLabel || $filter('translate')('button.ok');
    $scope.rejectLabel = args.rejectLabel || $filter('translate')('button.cancel');

    $scope.accept = function()
    {
        console.log('Confirmation dialog accepted');
        $scope.$close('accept');
    }

    $scope.reject = function()
    {
        console.log('Confirmation dialog rejected');
        $scope.$dismiss('reject');
    }
}


function DialogServiceProgressCtrl($scope, $filter, $timeout)
{
    var args = $scope.$resolve.args || {};

    $scope.title = args.title;
    $scope.value = args.value || 0;
    $scope.maxValue = args.maxValue;
    $scope.abortLabel = args.abortLabel || $filter('translate')('button.abort');
    $scope.acceptLabel = args.acceptLabel || $filter('translate')('button.close');
    $scope.step = args.step;
    $scope.active = true;
    $scope.type = 'info';

    $scope.accept = function()
    {
        $scope.$close();

        if (args.accept)
            args.accept($scope);
    }

    $scope.abort = function()
    {
        $scope.active = false;
    }

    function nextStep()
    {
        if ($scope.active && $scope.value < $scope.maxValue)
        {
            $scope.step($scope)
                .then(function()
                {
                    nextStep();
                },
                function(resp)
                {
                    $scope.active = false;
                    $scope.type = 'danger';
                    $scope.errorMessage = $filter('translate')('dialog.progress.failed', { error: resp.data });
                });
        }
        else
        {
            $scope.active = false;
            $scope.type = 'success';

            if (args.finished)
                args.finished($scope);

            if (args.autoClose)
                $timeout(function(){ $scope.$close(); }, 500);
        }
    }

    nextStep();
}


function DialogServiceSelectFolderCtrl($scope, $filter)
{
    $scope.title = args.title;
    $scope.message = args.message;
    $scope.acceptLabel = args.acceptLabel || $filter('translate')('button.ok');
    $scope.rejectLabel = args.rejectLabel || $filter('translate')('button.cancel');
    $scope.accept = args.accept || function() { dialog.close(); };
    $scope.reject = args.reject || function() { dialog.close(); };
}
