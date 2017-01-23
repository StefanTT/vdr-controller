'use strict';

angular.module('app.layout').directive('globalAlertBox', globalAlertBox);

function globalAlertBox()
{
    return {
        restrict : 'E',
        templateUrl : 'app/layout/globalAlertBox.html',
        scope :
        {
            type : '='
        },
        link : function($scope, $element, attrs)
        {
            $scope.messages = [];

            $scope.$on('showErrorMessage', function(ev, args)
            {
                if ($scope.messages.indexOf(args.text) < 0)
                {
                    if ($scope.messages.length > 9)
                        $scope.messages.splice(0, $scope.messages.length - 9);

                    $scope.messages.push(args.text);
                    $element.css('display', 'block');
                }
            });

            $scope.$on('$locationChangeStart', function(ev)
            {
                $scope.close();
            });

            $scope.close = function()
            {
                $element.css('display', 'none');
                $scope.messages.length = 0;
            };
        }
    }
}
