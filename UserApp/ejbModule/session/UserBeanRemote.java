package session;


import javax.ejb.Remote;

import model.User;
import exception.InvalidCredentialsException;
import exception.UsernameExistsException;

@Remote
public interface UserBeanRemote {

	User register(String username, String password) throws UsernameExistsException;
	Boolean login(String username, String password, String session) throws InvalidCredentialsException;
	Boolean logout(User logout);
	UserList getAllUsers();
	
}
