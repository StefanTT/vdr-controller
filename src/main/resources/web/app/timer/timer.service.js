'use strict';

angular.module('app.timer')
       .factory('TimerService', TimerService);

function TimerService($resource)
{
  return $resource('/rest/vdr/timer/:id');
}
