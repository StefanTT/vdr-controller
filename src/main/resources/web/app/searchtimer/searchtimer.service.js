'use strict';

angular.module('app.searchtimer')
       .factory('SearchtimerService', SearchtimerService);


function SearchtimerService($resource, $http)
{
    var resource = $resource('/rest/vdr/searchtimer/:id');

    resource.enable = function(id)
    {
        return $http.post('/rest/vdr/searchtimer/' + id + '/enable');
    }

    resource.disable = function(id)
    {
        return $http.post('/rest/vdr/searchtimer/' + id + '/disable');
    }

    return resource;
}
