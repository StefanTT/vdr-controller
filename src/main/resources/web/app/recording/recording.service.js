'use strict';

angular.module('app.recording')
       .factory('RecordingService', RecordingService);

function RecordingService($resource, $http)
{
  var resource = $resource('/rest/vdr/recording/:id');
  var folderResource = $resource('/rest/vdr/recordings/:path');

  resource.queryFolder = folderResource.query;

  return resource;
}
