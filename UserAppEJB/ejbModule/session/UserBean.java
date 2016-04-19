package session;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import entity.User;
import exception.InvalidCredentialsException;
import exception.UsernameExistsException;

/**
 * Session Bean implementation class UserBean
 */
@Stateless
@LocalBean
@Path("user")
public class UserBean implements UserBeanRemote {

	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test(){
		return "test";
	}

	
	@POST
	@Override
	public User register(String username, String password) throws UsernameExistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@GET
	@Override
	public Boolean login(String username, String password) throws InvalidCredentialsException {
		// TODO Auto-generated method stub
		return false;
	}

	@POST
	@Override
	public Boolean logout(User logout) {
		// TODO Auto-generated method stub
		return false;
	}

	@GET
	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
