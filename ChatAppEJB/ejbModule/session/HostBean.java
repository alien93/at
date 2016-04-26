package session;

/**
 * @author nina
 */
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import model.Host;
import model.Message;
import model.User;
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
		PropertiesReader pr = new PropertiesReader();
		System.out.println("Local: " + pr.getLocal());
		System.out.println("Master: " + pr.getMaster());
		return "test";
	}
	
	/**
	 * Kada se podigne ne-master cvor, na adresu master cvora, ne-master cvor salje svoju adresu i alias.
	 * Ukoliko je zahtev uspesan, dobija listu svih cvorova u klasteru koju belezi kod sebe.
	 */
	@SuppressWarnings("unchecked")
	@GET
	@Path("/register/{address}/{alias}")
	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public HostsList register(@PathParam("address") String address, @PathParam("alias")String alias) throws AliasExsistsException {
		Host host = new Host(address, alias);
		Host.hosts.add(host);
		//send register request to other nodes...
		for(int i=0; i<Host.hosts.size(); i++){
			if(!Host.hosts.get(i).getAddress().equals(address)){ //ako to nije cvor koji je poslao register zahtev
				//posalji register zahtev ostalim cvorovima da obnove listu hostova
				ResteasyClient client = new ResteasyClientBuilder().build();
				String val = "http://" + Host.hosts.get(i).getAddress() + ":8080/ChatAppWeb/rest/host/register/"+ address + "/"+alias+";address=" + address + ";alias=" + alias;
				ResteasyWebTarget target = client.target(val);
				Response response = target.request().get();
				HostsList ret = response.readEntity(HostsList.class);
				System.out.println("Registring new node result (request from master node)... ");
				System.out.println(ret);
				Host.hosts = (List<Host>) ret;
			}
		}
		return (HostsList) Host.hosts;
	}

	@POST
	@Path("unregister")
	@Override
	public void unregister(Host host) {
		Message.messages.remove(host);	//obrisi ga iz liste poruka
		Host.hosts.remove(host);		
	}

	/**
	 * Kada se korisik uspesno prijavi na sistem, ChatApp prolazi kroz listu
	 * svih cvorova i pravi addUser zahtev kako bi i drugi cvorovi bili azurirani.
	 * Zatim, putem WebSocket-a se azurira klijentska aplikacija da prikaze
	 * novog korisnika u listi korisnika.
	 * @param user
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("addUser")
	@Override
	public void addUser(User user) {
		System.out.println("Adding user " + user.getUsername() + " to logged users...");
		Host.loggedUsers.add(user);
	}

	@POST
	@Path("removeUser")
	@Override
	public void removeUser(User user) {
		System.out.println("Removing user " + user.getUsername() + "from logged users...");
		Host.loggedUsers.remove(user);
	}



}
