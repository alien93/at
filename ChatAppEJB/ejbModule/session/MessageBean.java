package session;
/**
 * @author nina
 */
import java.util.Iterator;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.Host;
import model.Message;
import model.User;
/**
 * Session Bean implementation class MessageBean
 */
@Stateless
@LocalBean
@Path("message")
public class MessageBean implements MessageBeanRemote {

	/**
	 * Ako je poruka javna (to polje prazno) onda se prikazuje
	 * svim klijentima, a ako je poruka privatna, proverava se da li je Host
	 * primalaca trenutni cvor. Ako jeste, putem WebSocket-a se prikazuje poruka
	 * datom korisniku, a ukoliko nije, formira se publish zahtev cvoru koji 
	 * jeste Host primalaca poruke. Cvor prihvata poruku i prosledjuje je sesiji koja
	 * je vezana za primalaca poruke.
	 */
	@SuppressWarnings({ "rawtypes" })
	@POST
	@Path("publish")
	@Override
	public Boolean publish(Message message) {
		Boolean retVal = false;
		User to = message.getTo();
		if(to!=null){
			int counter = 0;
			User from = message.getFrom();
			
			Iterator it = Message.messages.entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry pair = (Map.Entry)it.next();
			    String session = (String) pair.getKey();
			    if(to.getSessionID().equals(session) ||  from.getSessionID().equals(session))	//dodaj i poruke koje si ti slao drugima
			    {
			    	Message.messages.get(session).add(message);
			    	counter++;
			    }
			}
			if(counter==2){
				retVal = true;
			}
		}
		else{
			int counter = 0;
			
			Iterator it = Message.messages.entrySet().iterator();
			while (it.hasNext()) {
			    Map.Entry pair = (Map.Entry)it.next();
			    String session = (String) pair.getKey();
			    Message.messages.get(session).add(message);
			    counter++;
			}
			if(counter==Message.messages.size()){
				retVal = true;
			}
		}
		return retVal;
	}
	
	@GET
	@Path("/messagesForUser/{username}")
	public MessageList getMessagesForUser(@PathParam("username") String username){
		MessageList retVal = new MessageList();
		User u = new User();
		u = u.getUserByUsername(username);
		retVal.setMessages(Message.messages.get(u.getSessionID()));
		System.out.println("Messages for user: " + username + " ::" + retVal);
		return retVal;
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/messagesForSession/{session}")
	public MessageList getMessagesForSession(@PathParam("session") String session){
		MessageList retVal = new MessageList();
		retVal.setMessages(Message.messages.get(session));
		return retVal;
	}

}
