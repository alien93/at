package session;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;

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

	
	@GET
	@Path("/register/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public User register(@PathParam("username") String username, @PathParam("password") @Encoded String password) throws UsernameExistsException {
		System.out.println("I am here");
		System.out.println("user:" +username);
		System.out.println("pass:" + password);
		User retVal = new User(username, password);
		retVal.addRegisteredUser(username, password);
		System.out.println("retVal " + retVal.toString());
		return retVal;
	}

	@POST
	@Path("login")
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public Boolean login(String username, String password) throws InvalidCredentialsException {
		// TODO Auto-generated method stub
		return false;
	}

	@GET
	@Path("logout")
	@Produces(MediaType.TEXT_PLAIN)
	@Override
	public Boolean logout(User logout) {
		// TODO Auto-generated method stub
		return false;
	}

	@GET
	@Path("allUsers")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public List<User> getAllUsers() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}
