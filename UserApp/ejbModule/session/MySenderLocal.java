package session;

import javax.ejb.Local;
import javax.jms.JMSException;

@Local
public interface MySenderLocal {
	void sendMessage(String txt) throws JMSException;
	void sendMessage(Object obj) throws JMSException;
}
