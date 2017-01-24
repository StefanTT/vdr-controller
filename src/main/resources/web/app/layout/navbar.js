angular.module('app.layout')
       .controller('NavbarCtrl', NavbarCtrl);

function NavbarCtrl($scope, $location, $filter)
{
    $scope.isActive = function(path)
    {
        var loc = $location.path();
        return path === loc || loc.startsWith(path + '/');
    }
}