'use strict';

angular.module('app.common')
       .filter('split', function(){ return split })
       ;

function split(str, sep)
{
  if (str == null)
    return [];

  return str.split(sep);
}
