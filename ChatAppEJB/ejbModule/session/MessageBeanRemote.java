package session;

import javax.ejb.Remote;

import entity.Message;

@Remote
public interface MessageBeanRemote {

	void publish(Message message);
	
}
