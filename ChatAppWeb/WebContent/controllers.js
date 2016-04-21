var webSocket;
var users = [];
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
				else{	//it's an object
					users.push(message.data);
				}
				
				
			}	
		}
		}
	])
	.controller('chatController',['$scope', '$location', function($scope, $location){
		$scope.sendMessage = function(){
			console.log('sending message');
		}
		$scope.logout = function(){
			console.log('logout');
			//var obj = JSON.parse(users[0]);
			//console.log(obj.username);
			for(var i=0; i<users.length; i++){
				console.log(users[i]);
				//var obj = JSON.parse(users[i]);
				//if(obj.username == username){
				//	console.log(username);
				//}
			}
			//webSocket.send(user);
			$location.path('/login')
		}
	}]);
