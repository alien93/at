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
	public void publish(Message message) {
		User to = message.getTo();
		System.out.println("to: " + to);
		User from = message.getFrom();
		System.out.println("from: " + from);
		
		System.out.println("here are all messages from message bean:");
		System.out.println(Message.messages);
		Iterator it = Message.messages.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry pair = (Map.Entry)it.next();
		    String session = (String) pair.getKey();
		    if(to.getSessionID().equals(session) ||  from.getSessionID().equals(session))	//dodaj i poruke koje si ti slao drugima
		    {
		    	System.out.println("for session: " + session);
		    	System.out.println(Message.messages.get(session));
		    	Message.messages.get(session).add(message);
		    	
		    }
		}
		System.out.println("here are all messages from message bean:");
		System.out.println(Message.messages);
		
		System.out.println("Messages for " + to.getUsername() + Message.messages.get(to.getSessionID()));
		System.out.println("Messages for " + from.getUsername() + Message.messages.get(from.getSessionID()));
		
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

}
