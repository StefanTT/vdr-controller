'use strict';

angular.module('app.searchtimer')
       .factory('SearchtimerService', SearchtimerService);


function SearchtimerService($resource, $http)
{
    return $resource('/rest/vdr/searchtimer/:id', { id:'@id' },
    {
        enable:
        {
            url: '/rest/vdr/searchtimer/:id/enable',
            method: 'POST'
        },

        disable:
        {
            url: '/rest/vdr/searchtimer/:id/disable',
            method: 'POST'
        },

        search:
        {
            url: '/rest/vdr/searchtimer/:id/search',
            method: 'POST',
            isArray: true
        },

        eventDetails:
        {
            url: '/rest/vdr/epg/:channel/:time'
        }
    });
}
