var chatApp = angular.module('chatApp', ['ngRoute','ngResource']);

/*chatApp.factory('UserFactory', function($resource){
	return $resource('/ChatAppWeb/rest/test/findall',{},{
		get:{
			method: 'GET',
			params: {},
			isArray: false
		}
	})
})*/

chatApp.config(
		function($routeProvider){
			$routeProvider
				.when(
						"/",
						{
							templateUrl: "chat.html"
						}
				)
				.when(
						"/login",
						{
							templateUrl: "login.html"
						}
				)
				.when(
						"/register",
						{
							templateUrl: "register.html"
						}
				)
				.when(
						"/chat",
						{
							templateUrl: "chat.html"
						}
				)
		}

)