package entity;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.FormParam;


public class User {

	private String username;
	private String password;
	private Host host;
	
	HashMap<String, String> registeredUsers = new HashMap<>();
	ArrayList<User> loggedUsers = new ArrayList<>();
	
	
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
		return "User [username=" + username + ", password=" + password + ", host=" + host + "]";
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
		this.registeredUsers.put(username, password);
	}
	
	
}
