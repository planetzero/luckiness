/**
 *
 */
app.controller('histogrammController', ['$scope', 'luckyService', 'luckyFactory', '$interval', '$http', '$translate', 'addressAnalyticsService', '$window', 'keyGenerationService', '$timeout', 'luckyConstants', function ($scope, luckyService, luckyFactory, $interval, $http, $translate, addressAnalyticsService, $window, keyGenerationService, $timeout, luckyConstants) {

    var vm = this;

    vm.histogrammLabels = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"];
    vm.histogrammLabelsBin = ["0", "1"];

    vm.histogrammValues = new Array(10);
    vm.histogrammValues.fill(10);

    vm.histogrammValuesBin = new Array(2);
    vm.histogrammValuesBin.fill(1);

    vm.histoChartOptions = {
        legend: {
            display: false
        },
        animation: {
            duration: 1
        },
        tooltips: {
            enabled: true,
            mode: 'single',
            callbacks: {
                label: function(tooltipItem, data) {
                    var label = vm.histogrammLabels[tooltipItem.index];
                    return label;
                }
            }
        }
    };

    vm.histoChartOptionsBin = {
        legend: {
            display: false
        },
        animation: {
            duration: 1
        },
        tooltips: {
            enabled: true,
            mode: 'single',
            callbacks: {
                label: function(tooltipItem, data) {
                    var label = vm.histogrammLabelsBin[tooltipItem.index];
                    return label;
                }
            }
        }
    };

    $scope.$on(luckyConstants.KEY_VALUE_CHANGED_EVT, function (event, args) {

        var newKeyVal = args.newChosenKey;

        vm.histogrammValues.fill(0);

        for (var i = 0; i < newKeyVal.length; i++) {
            var charAtI = parseInt(newKeyVal.charAt(i));
            vm.histogrammValues[charAtI] = vm.histogrammValues[charAtI] + 1;
        }

        vm.histogrammValuesBin.fill(0);

        var binValStr = bigInt(newKeyVal).toString(2);
        for (var i = 0; i < binValStr.length; i++) {
            var charAtI = parseInt(binValStr.charAt(i));
            vm.histogrammValuesBin[charAtI] = vm.histogrammValuesBin[charAtI] + 1;
        }

    });

}]);

