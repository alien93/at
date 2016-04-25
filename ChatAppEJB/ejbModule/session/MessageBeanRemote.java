package session;

import javax.ejb.Remote;

import model.Message;

@Remote
public interface MessageBeanRemote {

	void publish(Message message);
	
}
