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
}


function DialogServiceProgressCtrl($scope, $filter, $timeout)
{
    var args = $scope.$resolve.args || {};

    $scope.title = args.title;
    $scope.value = args.value || 0;
    $scope.maxValue = args.maxValue;
    $scope.increment = 0;
    $scope.abortLabel = args.abortLabel || $filter('translate')('button.abort');
    $scope.acceptLabel = args.acceptLabel || $filter('translate')('button.close');
    $scope.step = args.step;
    $scope.active = true;
    $scope.aborted = false;
    $scope.type = 'info';

    $scope.accept = function()
    {
        $scope.$close();

        if (args.accept)
            args.accept($scope);
    }

    $scope.abort = function()
    {
        $scope.type = 'warning';
        $scope.active = false;
        $scope.aborted = true;
        $scope.errorMessage = $filter('translate')('dialog.progress.abort');
    }

    function nextStep()
    {
        $scope.value = $scope.value + $scope.increment;

        if ($scope.active && $scope.value < $scope.maxValue)
        {
            $scope.increment = 1;
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
        else if ($scope.aborted)
        {
            if (args.autoClose)
                $timeout(function(){ $scope.$close(); }, 500);
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
