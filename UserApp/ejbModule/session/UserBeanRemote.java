package session;

import java.util.List;

import javax.ejb.Remote;

import entity.User;
import exception.InvalidCredentialsException;
import exception.UsernameExistsException;

@Remote
public interface UserBeanRemote {

	User register(String username, String password) throws UsernameExistsException;
	Boolean login(String username, String password) throws InvalidCredentialsException;
	Boolean logout(User logout);
	UserList getAllUsers();
	
}
