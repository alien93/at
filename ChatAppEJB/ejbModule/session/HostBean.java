package session;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import entity.Host;
import entity.User;
import exception.AliasExsistsException;

/**
 * Session Bean implementation class HostBean
 */
@Stateless
@LocalBean
@Path("host")
public class HostBean implements HostBeanRemote {

	@GET
	@Path("test")
	public String test(){
		return "test";
	}
	
	
	@POST
	@Path("register")
	@Override
	public List<Host> register(String address, String alias) throws AliasExsistsException {
		// TODO Auto-generated method stub
		return null;
	}

	@POST
	@Path("unregister")
	@Override
	public void unregister(Host host) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addUser(User user) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeUser(User user) {
		// TODO Auto-generated method stub
		
	}



}
