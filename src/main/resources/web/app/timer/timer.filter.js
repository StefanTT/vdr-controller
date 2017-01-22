'use strict';

angular.module('app.timer')
       .filter('timerFilterState', function(){ return timerFilterState; });


function timerFilterState(timers, showAll)
{
    var result = [];
    for (var idx = 0; idx < timers.length; idx++)
    {
        var t = timers[idx];
        if (showAll || t.state != 'DISABLED')
            result.push(t);
    }

    return result;
}
