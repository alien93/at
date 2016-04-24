package entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


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
		System.out.println("users before removal:");
		System.out.println(loggedUsers.toString());
		boolean retVal = false;
		for (int i=0; i<loggedUsers.size(); i++){
			if(loggedUsers.get(i).getUsername().equals(user.getUsername())){
				loggedUsers.remove(i);
				retVal = true;
				System.out.println("users after removal:");
				System.out.println(loggedUsers.toString());
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
	
	public List<User> getAllUsers(){
	/*	List<User> retVal = new ArrayList<User>();
		Iterator<?> it = registeredUsers.entrySet().iterator();
	    while (it.hasNext()) {
	        @SuppressWarnings("rawtypes")
			Map.Entry pair = (Map.Entry)it.next();
	        User user = new User(pair.getKey().toString(), pair.getValue().toString());
	        retVal.add(user);
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	    return retVal;*/
		return null;
	}
	
}
