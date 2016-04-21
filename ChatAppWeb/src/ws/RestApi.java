package ws;

import java.util.List;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import entity.User;
import exception.InvalidCredentialsException;
import exception.UsernameExistsException;

public interface RestApi {
	@GET
	@Path("test")
	@Produces(MediaType.TEXT_PLAIN)
	public String test();

	
	@GET
	@Path("/register/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	
	public User register(@PathParam("username") String username, @PathParam("password") @Encoded String password) throws UsernameExistsException;

	@GET
	@Path("/login/{username}/{password}")
	@Produces(MediaType.APPLICATION_JSON)
	public Boolean login(@PathParam("username") String username, @PathParam("password") @Encoded String password) throws InvalidCredentialsException;

	@GET
	@Path("logout")
	@Produces(MediaType.TEXT_PLAIN)
	public Boolean logout(User logout);

	@GET
	@Path("allUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public List<User> getAllUsers();
	
}
