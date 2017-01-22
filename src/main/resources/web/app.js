'use strict';

angular.module('app',
    [ 'ngRoute', 'ngCookies', 'ngSanitize',
      'pascalprecht.translate', 'ui.bootstrap', 'app.common', 'app.dialog', 'app.home',
      'app.layout', 'app.osd', 'app.recording', 'app.setup', 'app.timer', 'app.video' ])
    .config(appConfig)
    .run(appRun);

appConfig.$inject = [ '$routeProvider', '$locationProvider', '$translateProvider', '$httpProvider' ];
function appConfig($routeProvider, $locationProvider, $translateProvider, $httpProvider)
{
    $locationProvider.hashPrefix('');

    $.jstree.defaults.core.check_callback = true;
    $.jstree.defaults.core.themes.responsive = false;

    $translateProvider.useStaticFilesLoader(
    {
        prefix : '/i18n/lang-',
        suffix : '.properties'
    });
    $translateProvider.uniformLanguageTag('bcp47');
    $translateProvider.determinePreferredLanguage();

    $httpProvider.interceptors.push(appHttpInterceptor);

    $routeProvider.when('/',
    {
        templateUrl : '/app/home/home.html',
        controller : 'HomeCtrl'
    }).when('/osd',
    {
        templateUrl : '/app/osd/osd.html',
        controller : 'OsdCtrl'
    }).when('/recording/:id',
    {
        templateUrl : '/app/recording/recording.details.html',
        controller : 'RecordingDetailsCtrl'
    }).when('/recording',
    {
        templateUrl : '/app/recording/recording.overview.html',
        controller : 'RecordingOverviewCtrl'
    }).when('/setup',
    {
        templateUrl : '/app/setup/setup.html',
        controller : 'SetupCtrl'
    }).when('/timer',
    {
        templateUrl : '/app/timer/timer.overview.html',
        controller : 'TimerOverviewCtrl'
    }).otherwise(
    {
        redirectTo : '/'
    });
}

appRun.$inject = [ 'SetupService' ];
function appRun(SetupService)
{
    SetupService.clearCaches();
}

appHttpInterceptor.$inject = [ '$rootScope', '$q', '$filter', '$timeout', '$location', '$window' ];
function appHttpInterceptor($scope, $q, $filter, $timeout, $location, $window)
{
    var interceptor = {};

    interceptor.request = function(config)
    {
        $('#global-loading-indicator').css('display', 'block');
        return config;
    };

    interceptor.response = function(response)
    {
        $('#global-loading-indicator').css('display', 'none');
        return response;
    };

    interceptor.responseError = function(response)
    {
        $('#global-loading-indicator').css('display', 'none');

        var status = response.status;
        var statusText = response.statusText;
        var statusClass = Math.floor(status / 100);

        if (status == -1)
        {
            var text = $filter('translate')('err.applicationOffline');
            $scope.$broadcast('showErrorMessage',
            {
                status : status,
                text : text
            });
        }
        else if (statusClass == 4 || statusClass == 5)
        {
            if (status == 412)
            {
                alert($filter('translate')('info.reload.appRestarted'));
                $window.location.reload();
            }
            else if (status == 421)
            {
                alert($filter('translate')('info.reload.security'));
                $window.location.reload();
            }
            else
            {
                var text = response.data || statusText || ('HTTP ' + status);
                if (text.startsWith('<html>'))
                    text = text.replace(/<\/*html>/g, '').replace(/<\/*body>/g, '').replace(/<\/*h[0-9]>/g, '');

                $scope.$broadcast('showErrorMessage',
                {
                    status : status,
                    text : text
                });
            }
        }

        return $q.reject(response);
    };

    return interceptor;
}
