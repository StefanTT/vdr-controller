'use strict';

angular.module('app.timer')
       .controller('TimerOverviewCtrl', TimerOverviewCtrl);

function TimerOverviewCtrl($scope, $routeParams, $filter, $window, $sanitize, TimerService)
{
    var tooltips = {};

    $scope.pendingTip = null;
    $scope.timers = TimerService.query();
    $scope.tip = null;

    $scope.showAll = true;
//  $scope.showAll = $window.sessionStorage.getItem('timer.overview.showAll');
//  if ($scope.showAll === null) $scope.showAll = true;
//  $scope.$watch('showAll', () => $window.sessionStorage.setItem('timer.overview.showAll', $scope.showAll));

    $scope.day = null;
    $scope.isNewDay = function(timer)
    {
        var date = new Date(timer.startTime);
        var lastDay = $scope.day;
        $scope.day = date.getDay();
        //console.log(date.getDay() + " =?= " + lastDay + " " + timer.path);
        return lastDay != $scope.day;
    }

    function assignTooltip(tip)
    {
        var desc = $sanitize(tip.description).replace(/\n+/g, ' ');
        if (desc.length > 500)
        {
            desc = desc.substr(0, 500);
            var idx = desc.lastIndexOf(' ');
            if (idx > 0) desc = desc.substr(0, idx);
            desc = desc + ' [...]';
        }

        $scope.tooltip = desc;
    }

    $scope.mouseOver = function(timer)
    {
        $scope.pendingTip = timer.id;

        var tip = tooltips[timer.id];
        if (tip != undefined)
        {
            $scope.pendingTip = null;
            assignTooltip(tip);
        }
        else
        {
            $scope.tooltip = '...';

            TimerService.details(timer.channel, timer.startTime + timer.duration * 30000)
                .then((resp) =>
                {
                    var tip = resp.data;
                    tooltips[timer.id] = tip;

                    if (timer.id == $scope.pendingTip)
                        assignTooltip(tip);
                });
        }
    }

    $scope.disableTimer = function(timer)
    {
        TimerService.disable(timer.id)
            .then(() => { timer.state = 'DISABLED'; });
    }

    $scope.enableTimer = function(timer)
    {
        TimerService.enable(timer.id)
            .then(() => { timer.state = 'ENABLED'; });
    }
}
