'use strict';

angular.module('app.channel')
       .controller('ChannelOverviewCtrl', ChannelOverviewCtrl);

function ChannelOverviewCtrl($scope, $routeParams, $filter, $location, ChannelService)
{
    $scope.channels = ChannelService.query();
}
