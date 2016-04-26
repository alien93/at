package mdb;
/**
 * @author nina
 */
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.sun.xml.internal.ws.api.model.WSDLOperationMapping;

import model.Host;
import model.User;

/**
 * Message-Driven Bean implementation class for: SenderReceiver
 */
@MessageDriven(
		activationConfig = { 
			@ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
			@ActivationConfigProperty(
				propertyName = "destination", propertyValue = "queue/mojQueue")	
		})
public class ChatSenderReceiver implements MessageListener {



	
    /**
     * Default constructor. 
     */
    public ChatSenderReceiver() {
    	System.out.println("ChatSenderReceiver created");
    }
	
    
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message msg) {
    	if(msg instanceof TextMessage){
    		TextMessage tm = (TextMessage) msg;
    		try{
    			String text = tm.getText();
    			System.out.println("ChatSenderReceiver: received new message: " + text);
    		}catch(JMSException e){
    			e.printStackTrace();
    		}
    	}
    	else if(msg instanceof ObjectMessage){
    		ObjectMessage obj = (ObjectMessage)msg;
    		try {
    			//prijavio se novi korisnik, javi svima!
				if(msg.getJMSType().equals("login"))
					try {
						User user = (User)obj.getObject();
						
						//prodji kroz listu svih cvorova i posalji adduser zahtev
						//for(int i=0; i<Host.hosts.size(); i++){
							ResteasyClient client = new ResteasyClientBuilder().build();
							//String ip = Host.hosts.get(i).getAddress();
							String ip = "localhost";
							String val = "http://" + ip + ":8080/ChatAppWeb/rest/host/addUser";
							ResteasyWebTarget target = client.target(val);
							target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
							
						//}
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				else if(msg.getJMSType().equals("logout"))
					try {
						User user = (User)obj.getObject();
						
						//prodji kroz listu svih cvorova i posalji adduser zahtev
						//for(int i=0; i<Host.hosts.size(); i++){
							ResteasyClient client = new ResteasyClientBuilder().build();
							//String ip = Host.hosts.get(i).getAddress();
							String ip = "localhost";
							String val = "http://" + ip + ":8080/ChatAppWeb/rest/host/removeUser";
							ResteasyWebTarget target = client.target(val);
							target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
							
						//}
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

}
