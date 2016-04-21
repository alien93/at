package entity;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jms.JMSSessionMode;
import javax.ws.rs.FormParam;

import jdk.nashorn.internal.ir.annotations.Ignore;


public class User {

	private String username;
	private String password;
	private Host host;
	
	private static HashMap<String, String> registeredUsers = new HashMap<>();
	private static ArrayList<User> loggedUsers = new ArrayList<>();
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public User(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public User(String username, String password, Host host) {
		super();
		this.username = username;
		this.password = password;
		this.host = host;
	}
	@Override
	public String toString() {
		return "{username:" + username + ", password:" + password + ", host:" + host + "}";
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Host getHost() {
		return host;
	}
	public void setHost(Host host) {
		this.host = host;
	}
	
	public void addRegisteredUser(String username, String password){
		registeredUsers.put(username, password);
		System.out.println(registeredUsers.toString());
	}

	public boolean addLoggedUser(User user) {
		boolean retVal = false;
		System.out.println("Registered users: " + registeredUsers);
		if(registeredUsers.containsKey(user.username)){
			if(registeredUsers.get(username).equals(password)){
				loggedUsers.add(user);
				retVal =  true;
			}
			else{
				retVal =  false;
			}
		}
		else
			retVal =  false;
		return retVal;
	}
	
	public boolean removeLoggedUser(User user){
		boolean retVal = false;
		if(loggedUsers.contains(user)){
			loggedUsers.remove(user);
			retVal = true;
		}
		else{
			System.out.println("There is no logged user under this username");
		}
		return retVal;
	}
	
}
