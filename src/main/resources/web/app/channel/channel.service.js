'use strict';

angular.module('app.channel').factory('ChannelService', ChannelService);

function ChannelService($resource, $http)
{
    return $resource('/rest/vdr/channel/:id', {},
    {
        get: { cache: true },
        query: { cache: true, isArray: true }
    });
}
