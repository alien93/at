package session;

import javax.ejb.Remote;

import model.Message;

@Remote
public interface MessageBeanRemote {

	Boolean publish(Message message);
	
}
