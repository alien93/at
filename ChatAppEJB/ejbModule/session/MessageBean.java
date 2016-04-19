package session;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Path;

import entity.Message;

/**
 * Session Bean implementation class MessageBean
 */
@Stateless
@LocalBean
@Path("message")
public class MessageBean implements MessageBeanRemote {

	
	@Path("publish")
	@Override
	public void publish(Message message) {
		// TODO Auto-generated method stub
		
	}


}
