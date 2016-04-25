package session;


import java.util.ArrayList;

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


import entity.Host;
import entity.Message;
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
	@Override
	public User register(@PathParam("username") String username, @PathParam("password") @Encoded String password) throws UsernameExistsException {
		User retVal = new User(username, password);
		retVal.addRegisteredUser(username, password);
		System.out.println("hello from register");
		return retVal;
	}

	@GET
	@Path("/login/{username}/{password}/{session}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public Boolean login(@PathParam("username") String username, @PathParam("password") @Encoded String password, @PathParam("session")String session) throws InvalidCredentialsException {
		Message.messages.put(session, new ArrayList<Message>());	//dodaj host na listu poruka
		Boolean retVal = false;
		User user = new User(username, password, new Host("1", "host1"), session);
		retVal = user.addLoggedUser(user);
		if(retVal == true){
			//putem jms-a javi aplikaciji master cvora da se prijavio novi korisnik

		}
		System.out.println("here are messages");
		System.out.println(Message.messages);
		return retVal;
	}

	@POST
	@Path("logout")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Override
	public Boolean logout(User logout) {
		System.out.println("hello from logout");
		Boolean retVal = false;
		retVal = logout.removeLoggedUser(logout);
		if(retVal == true){
			//putem jms-a javi aplikaciji master cvora da se korisnik odjavio
		}
		return retVal;
	}

	@GET
	@Path("allUsers")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public UserList getAllUsers() {
		UserList ul = new UserList();
		ul.setUserList(User.registeredUsers);
		return ul;
	}
	
	@GET
	@Path("loggedUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public UserList getAllLoggedUsers() {
		UserList ul = new UserList();
		ul.setUserList(User.loggedUsers);
		return ul;
	}
	
	
}