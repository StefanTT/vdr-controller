'use strict';

angular.module('app.recording').controller('RecordingOverviewCtrl', RecordingOverviewCtrl);

function RecordingOverviewCtrl($scope, $route, $routeParams, $filter, $location, $timeout, $uibModal, $q,
    DialogService, RecordingService)
{
  $scope.selected = [];
  $scope.numSelected = 0;

  function loadMore(parentNode, cb)
  {
    var path = parentNode.id;
    if (path === '#')
      path = '';

    var list = RecordingService.queryFolder({ path : path });
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
      'data' : loadMore,
      'check_callback' : checkModifyTree
    },
    'plugins' : [ 'checkbox', 'dnd', 'sort' ],
    'themes' :
    {
      'icons' : false
    },
    'checkbox' :
    {
      'tie_selection' : false,
      'whole_node' : false
    },
    'dnd' :
    {
      'copy' : false,
      'check_callback' : true
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

  function countSelected()
  {
    $scope.numSelected = 0;
    angular.forEach($scope.selected, function(node)
    {
      var data = node.data;
      if (data && data.childs != null)
        $scope.numSelected += data.childs;
      else if (data)
        $scope.numSelected++;
    });
  }

  function onCheckboxChanged(e, args)
  {
    $scope.selected = [];
    angular.forEach(tree.jstree('get_checked', true), function(node)
    {
      var data = node.data;
      if (data.type != 'folder' || node.children.length == 0)
        $scope.selected.push(node);
    });

    $scope.selected.sort((a, b) => (a.data.number || 0) - (b.data.number || 0));
    countSelected();

    if ($scope.numSelected > 0)
      $('#actionButtons a.nav-link').removeClass('disabled');
    else $('#actionButtons a.nav-link').addClass('disabled');
  }
  tree.on('check_node.jstree', onCheckboxChanged);
  tree.on('uncheck_node.jstree', onCheckboxChanged);


  function checkModifyTree(operation, node, node_parent, node_position, more)
  {
    if (operation != 'move_node') return true;
    if (!node_parent) return false;
    if (node_parent.id == '#') return true;

    var node = tree.jstree('get_node', node_parent.id);
    return node && node.data.type == 'folder';
  }

  function canDropInto(parents, nodes)
  {
    for (var j = 0; j < nodes.length; j++)
    {
      var node = nodes[j];
      for (var i = 0; i < parents.length; i++)
      {
        if (parents[i] == node)
          return false;
      }
    }
    return true;
  }

  $(document).on('dnd_start.vakata', function(e, args)
  {
    var nodes = args.data.nodes;
    for (var i = 0; i < $scope.numSelected; i++)
    {
      var sel = $scope.selected[i].id;
      if (sel != nodes[0])
        nodes.push(sel);
    }
  });

  $(document).on('dnd_stop.vakata', function(e, args)
  {
    var target = $(args.event.target).closest('.jstree-node');
    if (target.length <= 0) return;

    var targetNode = tree.jstree('get_node', target.get(0).id);
    if (!targetNode || targetNode.data.type != 'folder') return;

    if (!canDropInto(targetNode.parents, args.data.nodes))
      return; // cannot drop into a child folder

    $scope.selected = [];
    for (var j = 0; j < args.data.nodes.length; j++)
    {
      var nodeId = args.data.nodes[j];
      var node = tree.jstree('get_node', nodeId);
      console.log(nodeId + " dropped into " + targetNode.id);
      $scope.selected.push(node);
    }
    countSelected();

    $timeout(() => { moveConfirm(targetNode.data); });
  });


  function moveConfirm(target)
  {
    DialogService.confirm
    ({
      title : $filter('translate')('recording.overview.confirmMove.title'),
      message : $filter('translate')('recording.overview.confirmMove.message',
          { count : $scope.numSelected, target : target.name }),
      acceptLabel : $filter('translate')('button.continue'),
    })
    .result.then(moveSelected,
        function()
        {
          tree.jstree('uncheck_all');
          $route.reload();
        });
  }

  $scope.moveAction = function()
  {
    var numSelected = $scope.selected.length;

    $uibModal.open({
      animation: true, size: 'md', ariaLabelledBy: 'modal-title',
      ariaDescribedBy: 'modal-body', templateUrl: '/app/dialog/selectFolder.html',
      controller: function($scope)
      {
        $scope.title = $filter('translate')('recording.overview.confirmDelete.title');
        $scope.ok = $filter('translate')('button.ok');
        $scope.cancel = $filter('translate')('button.cancel');
        $scope.message = $filter('translate')('recording.overview.confirmDelete.message', { count:numSelected });
      }
    })
    .result.then(moveSelected);
  }

  function moveSelected(target)
  {
    console.log("Moving...");
  }


  $scope.deleteAction = function()
  {
    DialogService.confirm
    ({
      title : $filter('translate')('recording.overview.confirmDelete.title'),
      message : $filter('translate')('recording.overview.confirmDelete.message', { count : $scope.numSelected }),
      acceptLabel : $filter('translate')('button.continue'),
    })
    .result.then(deleteSelected);
  }

  function deleteSelected()
  {
    var idx = $scope.selected.length;
    var node = null;

    function uncheckDeleteNode()
    {
      if (node)
      {
        tree.jstree('uncheck_node', node);
        tree.jstree('delete_node', node);
      }
    }

    DialogService.progress({
        title : $filter('translate')('recording.dialog.delete.title'),
        scope : $scope,
        maxValue : $scope.numSelected,
        step : deleteNext,
        autoClose : true,
        finished : uncheckDeleteNode,
    })
    .result.then(() => { tree.jstree('uncheck_all'); });

    function deleteNext(dialog)
    {
      uncheckDeleteNode();

      node = $scope.selected[--idx];
      var rec = node.data;
      console.log('Deleting ' + (idx + 1) + ': ' + angular.toJson(rec));

      dialog.increment = (rec.childs || 1);
      dialog.message = $filter('translate')('recording.dialog.delete.text', { name : rec.name });

      var ref = rec == null ? node.id : rec.id;
      return RecordingService.delete({ id : ref }).$promise;
    }
  }
}
