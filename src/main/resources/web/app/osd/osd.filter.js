'use strict';

angular.module('app.osd')
       .filter('osdMainScreen', function(){ return filterOsdMainScreen })
       .filter('osdLabel', function(){ return filterOsdLabel })
       .filter('osdFind', function(){ return filterOsdFind })
       ;

function filterOsdLabel(item)
{
  return item == null ? '' : (item.label || '');
}

function filterOsdMainScreen(items)
{
  var result = [];

  for (var idx = 0; idx < items.length; idx++)
  {
    var t = items[idx].type.toLowerCase();
    if (t == 'item' || t == 'item_sel' || t == 'title' || t == 'message')
      result.push(items[idx]);
  }

  return result;
}

function filterOsdFind(items, type)
{
  for (var idx = 0; idx < items.length; idx++)
  {
    if (items[idx].type.toLowerCase() == type)
      return items[idx];
  }

  return null;
}
