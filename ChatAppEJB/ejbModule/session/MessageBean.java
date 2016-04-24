package session;
/**
 * @author nina
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import entity.Host;
import entity.Message;
import entity.User;

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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@POST
	@Path("publish")
	@Override
	public void publish(Message message) {
		User to = message.getTo();
		User from = message.getFrom();
		
		Iterator it = Message.messages.entrySet().iterator();
		while (it.hasNext()) {
		    Map.Entry pair = (Map.Entry)it.next();
		    Host h = (Host)pair.getKey();
		    ArrayList<Message> msgs = (ArrayList<Message>)pair.getValue();
		    if(to.getHost().equals(h) ||  from.getHost().equals(h))	//dodaj i poruke koje si ti slao drugima
		    {
		    	msgs.add(message);
		    }
		    it.remove();
		}
		
	}


}
