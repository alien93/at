var webSocket;
//var users = [];
var username = "";

angular.module('chatApp')
	.controller('authController', ['$scope','$rootScope', '$location', '$http',
	   function($scope, $rootScope, $location, $http){
		$rootScope.login = function(){
			$scope.error = false;
			if(webSocket == undefined)
				webSocket = new WebSocket("ws://localhost:8080/ChatAppWeb/websocket");
			
			if(webSocket.readyState == 1){
				console.log("hello from login");
				var text = '{"type":"login", "username":"' + $scope.username + '", "password":"' + $scope.password + '"}';
				webSocket.send(text);	
			}
			
			webSocket.onmessage  = function(message){
				console.log(message);
				if(message.data == "success"){
					$location.path("/chat");
					$scope.$apply();
					username = $scope.username;
				}
				else if(message.data == "error"){
					$location.path("/login");
					$scope.errorMessage = "Wrong username or password";
					$scope.error = true;
					$scope.$apply();
				}
			}
		}
	}
	])
	.controller('regController', ['$scope', '$location',
	   function($scope, $location, $rootScope, UserFactory){
		$scope.register = function(){
			$scope.error = false;
			if(webSocket == undefined)
				webSocket = new WebSocket("ws://localhost:8080/ChatAppWeb/websocket");
			webSocket.onopen = function(event){
				var text = '{"type":"register", "username":"' + $scope.username + '", "password":"' + $scope.password + '"}';
				webSocket.send(text);
			}
			
			webSocket.onmessage = function(message){
				console.log(message);
				if(message.data == "success"){
					$location.path("/login");
					$scope.$apply();
				}
				else if(message.data == "error"){
					console.log("hello from else");
					$location.path("/register");
					$scope.errorMessage = "User already exists.";
					$scope.error = true;
					$scope.$apply();
				}
				
			}	
		}
		}
	])
	.controller('chatController',['$scope', '$location', function($scope, $location){
		$scope.username = username;
		$scope.loggedUsers = [{"username":"test1", "password":"test2"},{"username":"test2", "password":"test3"}];
		$scope.sendMessage = function(){
			
			console.log('sending message');
		}
		$scope.logout = function(){
			console.log('logout');
			var text = '{"type":"logout", "username":"' + username + '"}';
			webSocket.send(text);
			$location.path('/login')
		}
	}]);
