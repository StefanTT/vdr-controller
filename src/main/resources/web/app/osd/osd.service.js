'use strict';

angular.module('app.osd')
       .factory('OsdService', OsdService);

function OsdService($resource, $http)
{
  var res = $resource('/rest/vdr/osd');
  var resource = {};

  resource.query = res.query;

  resource.key = function(key)
  {
    return $http.post('/rest/vdr/osd/' + key);
  }

  return resource;
}
