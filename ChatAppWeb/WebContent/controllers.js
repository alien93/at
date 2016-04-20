var webSocket;

angular.module('chatApp')
	.controller('authController', ['$scope','$rootScope', '$location', '$http',
	   function($scope, $rootScope, $location, $http){
		$rootScope.login = function(){
	        /*$http.get('http://localhost:8080/ChatAppWeb/rest/test/findall').success(function(response){
	        	$rootScope.test = response[0].name;
	        	console.log($rootScope.test);
	        })*/
		if(webSocket == undefined)
			webSocket = new WebSocket("ws://localhost:8080/ChatAppWeb/websocket");
		webSocket.onopen = function(event){
			var text = '{"type":"login", "username":"' + $scope.username + '", "password":"' + $scope.password + '"}';
			webSocket.send(text);
		}	
		
		webSocket.onmessage  = function(message){
			console.log(message);
		}
		$location.path('/chat');
		}
	}
	])
	.controller('regController', ['$scope', '$location',
	   function($scope, $location, UserFactory){
		$scope.register = function(){
			if(webSocket == undefined)
				webSocket = new WebSocket("ws://localhost:8080/ChatAppWeb/websocket");
			webSocket.onopen = function(event){
				var text = '{"type":"register", "username":"' + $scope.username + '", "password":"' + $scope.password + '"}';
				webSocket.send(text);
			}
			
			webSocket.onmessage = function(message){
				console.log(message);
			}	
			$location.path('/login');                        
			}
		}
	]);
