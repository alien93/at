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
			console.log("hello from login");
			var text = '{"type":"login", "username":"' + $scope.username + '", "password":"' + $scope.password + '"}';
			webSocket.send(text);
		}	
		
		webSocket.onmessage  = function(message){
			console.log(message.data);
			if(message.data == "success"){
				$location.path("/chat");
				$scope.$apply();
			}
			else if(message.data == "error"){
				$location.path("/login");
				$rootScope.errorMessage = "Wrong username or password";
				$scope.$apply();
			}
		}
		}
	}
	])
	.controller('regController', ['$scope', '$location',
	   function($scope, $location, $rootScope, UserFactory){
		$scope.register = function(){
			if(webSocket == undefined)
				webSocket = new WebSocket("ws://localhost:8080/ChatAppWeb/websocket");
			webSocket.onopen = function(event){
				var text = '{"type":"register", "username":"' + $scope.username + '", "password":"' + $scope.password + '"}';
				webSocket.send(text);
			}
			
			webSocket.onmessage = function(message){
				console.log(message.data);
				if(message.data == "success"){
					$location.path("/login");
					$scope.$apply();
				}
				else if(message.data == "error"){
					console.log("hello from else");
					$location.path("/register");
					$rootScope.errorMessage = "User already exists.";
					$scope.$apply();
				}
			}	
		}
		}
	])
	.controller('chatController',['$scope', function($scope){
		$scope.sendMessage = function(){
			console.log('sending message');
		}
	}]);
