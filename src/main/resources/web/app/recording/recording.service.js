'use strict';

angular.module('app.recording')
       .factory('RecordingService', RecordingService);

function RecordingService($resource, $http)
{
  return $resource('/rest/vdr/recording/:id', {},
  {
      queryFolder : {
          url : '/rest/vdr/recordings/:path',
          isArray : true
      },
      move : {
          method : 'POST',
          url : '/rest/vdr/recording/:id/move/:target'
      },
      moveFolder : {
          method : 'POST',
          url : '/rest/vdr/recordins/:path/move/:target'
      }
  });
}
