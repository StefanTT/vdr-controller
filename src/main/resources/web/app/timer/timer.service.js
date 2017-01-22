'use strict';

angular.module('app.timer').factory('TimerService', TimerService);

function TimerService($resource, $http)
{
    var resource = $resource('/rest/vdr/timer/:id');

    resource.enable = function(id)
    {
        return $http.post('/rest/vdr/timer/' + id + '/enable');
    }

    resource.disable = function(id)
    {
        return $http.post('/rest/vdr/timer/' + id + '/disable');
    }

    return resource;
}
