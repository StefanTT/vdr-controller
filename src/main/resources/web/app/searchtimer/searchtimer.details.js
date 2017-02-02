'use strict';

angular.module('app.searchtimer')
       .controller('SearchtimerDetailsCtrl', SearchtimerDetailsCtrl);

function SearchtimerDetailsCtrl($scope, $routeParams, $filter, $window, $sanitize, SearchtimerService)
{
    var tooltips = {};

    $scope.id = $routeParams.id;
    $scope.timer = SearchtimerService.get({id: $routeParams.id});

    // Clock picker from http://weareoutman.github.io/clockpicker
    $('.clockpicker').clockpicker({ autoclose:true, vibrate:false });

    function minutesToTimeStr(min)
    {
        var h = Math.floor(min / 60);
        var m = min % 60;
        return '' + Math.floor(h / 10) + (h % 10)
             + ':' + Math.floor(m / 10) + (m % 10);
    }

    function timeStrToMinutes(str)
    {
        if (str == undefined || str == '')
            return 0;

        var hm = str.split(':');
        return (hm[0] * 60) + (hm[1] * 1);
    }

    $scope.timer.$promise.then(() =>
    {
        if ($scope.timer.startTimeMin || $scope.timer.startTimeMax)
        {
            $scope.startTimeMin = minutesToTimeStr($scope.timer.startTimeMin);
            $scope.startTimeMax = minutesToTimeStr($scope.timer.startTimeMax);
        }

        $scope.compareDescription = $scope.timer.repeatsMatchDescriptionPercent != undefined;

        $scope.enableDelete = $scope.timer.deleteAfterDays > 0;
        $scope.deleteAfterDays = Math.max(1, 0 + $scope.timer.deleteAfterDays);

        $scope.enableKeep = $scope.timer.keepRecordings > 0;
        $scope.keepRecordings = Math.max(1, 0 + $scope.timer.keepRecordings);
    });

    $scope.$watch('timer.avoidRepeats', () =>
    {
        $('#avoidRepeats input').attr('disabled', !$scope.timer.avoidRepeats);
    });

    function apply()
    {
        $scope.timer.startTimeMin = timeStrToMinutes($scope.startTimeMin);
        $scope.timer.startTimeMax = timeStrToMinutes($scope.startTimeMax);
        $scope.timer.deleteAfterDays = $scope.enableDelete ? $scope.deleteAfterDays : 0;
        $scope.timer.keepRecordings = $scope.enableKeep ? $scope.keepRecordings : 0;
    }

    $scope.save = function()
    {
        apply();
        SearchtimerService.save($scope.timer);
    }

    $scope.test = function()
    {
        apply();

        $scope.testResultsEmpty = false;
        $scope.testResults = SearchtimerService.search($scope.timer);
        $scope.testResults.$promise.then(function()
        {
            angular.forEach($scope.testResults, function(event)
            {
                event.id = 'event-' + event.channel + '-' + event.startTime;
            });

            $('html,body').animate({ scrollTop: $('#testResults').offset().top }, 'slow');

            $scope.testResultsEmpty = $scope.testResults.length <= 0;
        });
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

    $scope.mouseOver = function(event)
    {
        $scope.pendingTip = event.id;

        var tip = tooltips[event.id];
        if (tip != undefined)
        {
            $scope.pendingTip = null;
            assignTooltip(tip);
        }
        else
        {
            $scope.tooltip = '...';

            var details = SearchtimerService.eventDetails
            ({
                channel: event.channel,
                time: event.startTime + event.duration * 30000
            });

            details.$promise.then(() =>
            {
                var tip = details;
                tooltips[event.id] = tip;

                if (event.id == $scope.pendingTip)
                    assignTooltip(tip);
            });
        }
    }
}
