'use strict';

angular.module('app.searchtimer')
       .filter('searchtimerFilterEnabled', function(){ return searchtimerFilterEnabled; });


function searchtimerFilterEnabled(timers, showAll)
{
    var result = [];
    for (var idx = 0; idx < timers.length; idx++)
    {
        var t = timers[idx];
        if (showAll || t.enabled)
            result.push(t);
    }

    return result;
}
