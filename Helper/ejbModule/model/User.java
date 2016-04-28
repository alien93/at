package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class User implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;
	private Host host;
	private String sessionID;
	
	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public transient static List<User> registeredUsers = new ArrayList<>();
	public transient static List<User> loggedUsers = new ArrayList<>();
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public User(String username, String password){
		this.username = username;
		this.password = password;
		this.host = null;
		this.sessionID = "";
	}
	
	public User(String username, String password, Host host) {
		super();
		this.username = username;
		this.password = password;
		this.host = host;
		this.sessionID = "";
	}
	
	public User(String username, String password, Host host, String sessionID){
		this.username = username;
		this.password = password;
		this.host = host;
		this.sessionID = sessionID;
	}
	
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", host=" + host + ", sessionID=" + sessionID
				+ "]";
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
	
	public boolean addRegisteredUser(String username, String password){		
		//check if user exists
		for(User user : registeredUsers){
			if(user.getUsername().equals(username)){
				return false;
			}
		}
		
		User u = new User(username, password, null);
		registeredUsers.add(u);
		System.out.println(registeredUsers.toString());
		return true;
	}

	public boolean addLoggedUser(User user) {
		boolean retVal = false;
		System.out.println("Registered users: " + registeredUsers);
		for(User u: registeredUsers){
			if(u.getUsername().equals(user.username) && u.getPassword().equals(user.password)){
				loggedUsers.add(user);
				retVal =  true;
				break;
			}
			else
				retVal =  false;
		}
		
		return retVal;
	}
	
	public boolean removeLoggedUser(User user){
		System.out.println("users before removal:");
		System.out.println(loggedUsers.toString());
		boolean retVal = false;
		for (int i=0; i<loggedUsers.size(); i++){
			if(loggedUsers.get(i).getUsername().equals(user.getUsername())){
				loggedUsers.remove(i);
				retVal = true;
				System.out.println("users after removal:");
				System.out.println(loggedUsers.toString());
				break;
			}
		}
		if(!retVal){
			System.out.println("There is no logged user under this username");
		}
		return retVal;
	}
	
	public User getUserByUsername(String username){
		User retVal = null;
		for(User u: loggedUsers){
			if(u.username.equals(username)){
				retVal = u;
				break;
			}
		}
		return retVal;
	}
	
	/*public List<User> getRegisteredUsers(){
		return registeredUsers;
	}*/
	
	
	
	
}