var webSocket;
//var users = [];
var username = "";

angular.module('chatApp')
	//login
	.controller('authController', ['$scope','$rootScope', '$location', '$http',
	   function($scope, $rootScope, $location, $http){
		$rootScope.login = function(){
			$scope.error = false;
			if(webSocket == undefined)
				webSocket = new WebSocket("ws://192.168.46.121:8080/ChatAppWeb/websocket");
			
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
	//register
	.controller('regController', ['$scope', '$location',
	   function($scope, $location, $rootScope, UserFactory){
		$scope.register = function(){
			$scope.error = false;
			if(webSocket == undefined)
				webSocket = new WebSocket("ws://192.168.46.121:8080/ChatAppWeb/websocket");
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
	//chat
	.controller('chatController',['$scope', '$location', function($scope, $location){
		//get all logged users
		if(webSocket == undefined)
			webSocket = new WebSocket("ws://192.168.46.121:8080/ChatAppWeb/websocket");
		
		if(webSocket.readyState == 1){
			var text = '{"type":"getLoggedUsers"}';
			webSocket.send(text);	
		}
		
		webSocket.onmessage  = function(message){
			console.log("-1- " + message);
			$scope.$apply(function(){
				if(message.data == "success_loggedUsers"){
					console.log("logged users retreived successfully")
				}
				else if(message.data == "error_loggedUsers"){
					console.log("could not retreive loggedUsers list")
				}
				else{
					var temp = JSON.parse(message.data);
					console.log("-2- " + temp.userList);
					if(temp.userList!=null){
						console.log('loggedusers: ' + $scope.loggedUsers);
						var temp = JSON.parse(message.data);
						$scope.loggedUsers = temp.userList;
						//$scope.$apply();
					}
					else{
						console.log('received messages: ');
						var temp = JSON.parse(message.data);
						console.log(temp);
						$scope.messages = temp.messages;
						console.log("messages: " + $scope.messages);
					}
				}
			});
		}
		
		//set logged users
		$scope.username = username;
		$scope.selectedValue = "";
		$scope.selected = function(selectedValue){
			console.log(selectedValue);
			$scope.selectedValue = selectedValue;
		}
		//send message
		$scope.sendMessage = function(){
			console.log('to ' + $scope.selectedValue);
			console.log('from ' + $scope.username);
			console.log('date ' +  new Date());
			console.log('subject ' + $scope.subject);
			console.log('message ' + $scope.content);
			
			
			if(webSocket == undefined)
				webSocket = new WebSocket("ws://192.168.46.121:8080/ChatAppWeb/websocket");
			
			//ukoliko je websocket spreman, posalji poruku
			if(webSocket.readyState == 1){
				var text = '{"type":"message", "to":"' + $scope.selectedValue + '", "from":"' + $scope.username + '", "date":"' + new Date() + '", "subject":"' + $scope.subject + '", "message":"' + $scope.content + '"}';
				webSocket.send(text);	
			}
			
			webSocket.onmessage  = function(message){
				$scope.$apply(function(){
					console.log(message);
					if(message.data == "success_message"){
						console.log("message has been sent")
					}
					else if(message.data == "error_message"){
						console.log("error while delivering the message")
					}
					else{
						var temp = JSON.parse(message.data);
						console.log("-2- " + temp.userList);
						if(temp.userList!=null){
							console.log('loggedusers: ' + $scope.loggedUsers);
							var temp = JSON.parse(message.data);
							$scope.loggedUsers = temp.userList;
							//$scope.$apply();
						}
						else{
							console.log('received messages: ');
							var temp = JSON.parse(message.data);
							console.log(temp);
							$scope.messages = temp.messages;
							console.log("messages: " + $scope.messages);
						}
					}
				});
			}
			
			$scope.subject = "";	//reset fields
			$scope.content = "";
			
		}
		
		$scope.logout = function(){
			console.log('logout');
			var text = '{"type":"logout", "username":"' + username + '"}';
			webSocket.send(text);
			$location.path('/login')
		}
	}]);
