package session;

import java.util.HashMap;
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

import entity.Host;
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
		User retVal = new User(username, password);
		retVal.addRegisteredUser(username, password);
		return retVal;
	}

	@GET
	@Path("/login/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Boolean login(@PathParam("username") String username, @PathParam("password") @Encoded String password) throws InvalidCredentialsException {
		Boolean retVal = false;
		User user = new User(username, password, new Host("1", "host1"));
		retVal = user.addLoggedUser(user);	
		return retVal;
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
