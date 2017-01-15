'use strict';

angular.module('app.setup')
       .factory('SetupService', SetupService);

function SetupService($resource, $http)
{
  var resource = $resource('/rest/config');

  resource.clearCaches = function()
  {
    $http.post('/rest/clearCaches');
  }

  return resource;
}
