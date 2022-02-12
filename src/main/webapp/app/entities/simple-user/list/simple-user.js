angular.module('app', ['ngTouch', 'ui.grid']).controller('MainCtrl', MainCtrl);

function MainCtrl() {
  this.myData = [
    {
      firstName: 'Cox',
      lastName: 'Carney',
      company: 'Enormo',
      employed: true,
    },
    {
      firstName: 'Lorraine',
      lastName: 'Wise',
      company: 'Comveyer',
      employed: false,
    },
    {
      firstName: 'Nancy',
      lastName: 'Waters',
      company: 'Fuelton',
      employed: false,
    },
  ];
}
