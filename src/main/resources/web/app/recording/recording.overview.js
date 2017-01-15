'use strict';

angular.module('app.recording').controller('RecordingOverviewCtrl', RecordingOverviewCtrl);

function RecordingOverviewCtrl($scope, $routeParams, $filter, $location, $timeout, $uibModal, $q,
    DialogService, RecordingService)
{
  $scope.selected = [];
  $scope.numSelected = 0;
  var $ctrl = this;

  function loadMore(parentNode, cb)
  {
    var path = parentNode.id;
    if (path === '#')
      path = '';

    var list = RecordingService.queryFolder(
    {
      path : path
    });
    list.$promise.then(function()
    {
      var nodes = [];
      var idx = 0;

      if (path)
        path = path + '~';

      angular.forEach(list, function(entry)
      {
        var id = path + encodeURIComponent(entry.id || entry.name);
        var node =
        {
          id : id,
          data : entry
        };
        if (entry.childs != null)
        {
          entry.type = 'folder';
          entry.id = id;
          node.children = entry.childs > 0;
          node.icon = '/assets/icon/folder-22.png';
          node.text = entry.name + ' <span class="overviewInfo">(' + entry.childs + ')</span>';
        }
        else
        {
          entry.type = 'recording';
          node.icon = '/assets/icon/video-22.png';
          node.text = entry.name + ' <span class="overviewInfo">('
                                 + $filter('date')(entry.start, 'medium')
                                 + ', '
                                 + $filter('translate')('duration.min', {t:entry.duration})
                                 + ')</span>';
        }
        nodes.push(node);
      });

      cb.call($('#recordingsTree'), nodes);
    });
  }

  var tree = $('#recordingsTree');
  tree.jstree(
  {
    'core' :
    {
      'data' : loadMore
    },
    'plugins' : [ 'checkbox', 'dnd', 'sort' ],
    'themes' :
    {
      'icons' : false
    },
    'checkbox' :
    {
      tie_selection : false,
      whole_node : false
    }
  });

  tree.on('select_node.jstree', function(e, args)
  {
    var node = args.node;
    if (node.data && node.data.type == 'recording')
      $timeout(function()
      {
        $location.path('/recording/' + node.data.id);
      });
    else
      $('#recordingsTree').jstree('toggle_node', node);
  });

  function onCheckboxChanged(e, args)
  {
    $scope.selected = tree.jstree('get_checked', true);

    $scope.numSelected = 0;
    angular.forEach($scope.selected, function(node)
    {
      var data = node.data;
      if (data && data.childs != null)
        $scope.numSelected += data.childs;
      else if (data)
        $scope.numSelected++;
    });

    if ($scope.numSelected > 0)
      $('#actionButtons a.nav-link').removeClass('disabled');
    else $('#actionButtons a.nav-link').addClass('disabled');
  }
  tree.on('check_node.jstree', onCheckboxChanged);
  tree.on('uncheck_node.jstree', onCheckboxChanged);

  $scope.deleteAction = function()
  {
    deleteSelected();
    return;

    var dialog = DialogService.confirm
    ({
      title : $filter('translate')('recording.overview.confirmDelete.title'),
      message : $filter('translate')('recording.overview.confirmDelete.message',
                                     { count : $scope.numSelected }),
      acceptLabel : $filter('translate')('button.continue'),
      accept : function()
      {
        dialog.close();
        deleteSelected();
      }
    });
  }

  function deleteSelected()
  {
    console.log("deleting...");
    $scope.progressValue = 0;
    $scope.progressType = 'info';
    $scope.aborted = false;

    var idx = 0;
    var num = $scope.selected.length;

    var dialog = DialogService.progress({
        title : $filter('translate')('recording.overview.deleting.title'),
        scope : $scope,
        maxValue : $scope.numSelected,
        abort : function(){ alert("aborting"); }
    });

    function deleteNext()
    {
      var node = $scope.selected[idx];
      var rec = node.data;

      $scope.progressMessage = $filter('translate')('recording.dialog.delete.text', { name : rec.name });
      console.log('Deleting ' + (idx + 1) + '/' + num + ': ' + angular.toJson(rec));

      var ref = rec == null ? node.id : rec.id;
      RecordingService.delete({ id : ref })
        .$promise.then(function()
        {
          tree.jstree('delete_node', node);

          $scope.progressValue = $scope.progressValue + (rec.childs || 1);
          idx = idx + 1;

          if (idx < num)
          {
            deleteNext();
          }
          else
          {
            tree.jstree('uncheck_all');
            $scope.progressType = 'success';
            $timeout(function(){ dialog.close(); }, 500);
          }
        },
        function(resp)
        {
          tree.jstree('uncheck_all');
          $scope.progressAborted = true;
          $scope.progressType = 'danger';
          $scope.progressMessage = $scope.progressMessage + ' '
                                   + $filter('translate')('recording.dialog.delete.failed')
                                   + ': ' + resp.data;
        });
    }

    deleteNext();
  }

  $scope.moveAction = function()
  {
    var numSelected = $scope.selected.length;

    var dialog = $uibModal.open({
      animation: true, size: 'md', ariaLabelledBy: 'modal-title',
      ariaDescribedBy: 'modal-body', templateUrl: '/app/dialog/selectFolder.html',
      controller: function($scope)
      {
        $scope.title = $filter('translate')('recording.overview.confirmDelete.title');
        $scope.ok = $filter('translate')('button.ok');
        $scope.cancel = $filter('translate')('button.cancel');
        $scope.message = $filter('translate')('recording.overview.confirmDelete.message',
            { count:numSelected });
        $scope.actionOk = function()
        {
          dialog.close();
          console.log("moving...");
        }
        $scope.actionCancel = function()
        {
          dialog.close();
        }
      },
    });
  }
}
