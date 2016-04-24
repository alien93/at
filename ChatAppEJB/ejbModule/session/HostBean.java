package session;
/**
 * @author nina
 */
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import entity.Host;
import entity.Message;
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
		PropertiesReader pr = new PropertiesReader();
		System.out.println("Local: " + pr.getLocal());
		System.out.println("Master: " + pr.getMaster());
		return "test";
	}
	
	/**
	 * Kada se podigne ne-master cvor, na adresu master cvora, ne-master cvor salje svoju adresu i alias.
	 * Ukoliko je zahtev uspesan, dobija listu svih cvorova u klasteru koju belezi kod sebe.
	 */
	@POST
	@Path("register")
	@Override
	public List<Host> register(String address, String alias) throws AliasExsistsException {
		Host host = new Host(address, alias);
		Host.hosts.add(host);
		Message.messages.put(host, new ArrayList<Message>());	//dodaj host na listu poruka
		return Host.hosts;
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
	@Path("addUser")
	@Override
	public void addUser(User user) {
		Host.loggedUsers.add(user);
	}

	@POST
	@Path("removeUser")
	@Override
	public void removeUser(User user) {
		Host.loggedUsers.remove(user);
	}



}
