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
    var dialog = $uibModal.open({
      animation : true,
      size : 'md',
      backdrop : true,
      templateUrl : '/app/dialog/confirm.html',
      controller : function($scope)
      {
        scope = $scope;
        $scope.title = args.title;
        $scope.message = args.message;
        $scope.acceptLabel = args.acceptLabel || $filter('translate')('button.ok');
        $scope.rejectLabel = args.rejectLabel || $filter('translate')('button.cancel');
        $scope.accept = args.accept || function() { dialog.close(); };
        $scope.reject = args.reject || function() { dialog.close(); };
      }
    });
    return dialog;
  }

  /**
   * Open a progress dialog.
   */
  service.progress = function(args)
  {
    var dialog = $uibModal.open({
      animation : true,
      size : 'md',
      scope : args.scope,
      resolve : args.resolve,
      backdrop : 'static',
      //keyboard : false,
      templateUrl : '/app/dialog/progress.html',
      controller : function($scope)
      {
        $scope.args = args;
        $scope.title = args.title;
        $scope.maxValue = args.maxValue;
        $scope.abortLabel = args.abortLabel || $filter('translate')('button.abort');
        $scope.abort = args.abort;
        $scope.acceptLabel = args.acceptLabel || $filter('translate')('button.close');
        $scope.accept = args.accept || function() { dialog.close(); };

        if (args.message)
          $scope.progressMessage = args.message;
      }
    });

    return dialog;
  }

  /**
   * Open a folder selection dialog.
   */
  service.selectFolder = function(args)
  {
    var scope = {};
    var dialog = $uibModal.open({
      animation : true,
      size : 'md',
      scope : scope,
      templateUrl : '/app/dialog/selectFolder.html',
      controller : function($scope)
      {
        $scope.title = args.title;
        $scope.message = args.message;
        $scope.acceptLabel = args.acceptLabel || $filter('translate')('button.ok');
        $scope.rejectLabel = args.rejectLabel || $filter('translate')('button.cancel');
        $scope.accept = args.accept || function() { dialog.close(); };
        $scope.reject = args.reject || function() { dialog.close(); };
      }
    });

    return dialog;
  }

  return service;
}
