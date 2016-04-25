package session;

import java.util.List;

import entity.User;

public class UserList {

	private List<User> userList;

	public List<User> getUserList() {
		return userList;
	}

	public void setUserList(List<User> loggedUsers) {
		this.userList = loggedUsers;
	}
	
	
	
}
