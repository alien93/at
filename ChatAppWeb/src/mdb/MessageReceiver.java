package mdb;
import java.io.IOException;

/**
 * @author nina
 */
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.websocket.WebSocketContainer;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import exception.InvalidCredentialsException;
import model.Host;
import model.User;
import session.UserBean;
import ws.WSManager;

/**
 * Message-Driven Bean implementation class for: MessageReceiver
 */
@MessageDriven(
		activationConfig = { 
			@ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
			@ActivationConfigProperty(
				propertyName = "destination", propertyValue = "queue/mojQueue")	
		})
public class MessageReceiver implements MessageListener {

	
    /**
     * Default constructor. 
     */
    public MessageReceiver() {
    	System.out.println("MessageReceiver created");
    }
	
    
	/**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message msg) {
    	if(msg instanceof TextMessage){
    		TextMessage tm = (TextMessage) msg;
    		try{
    			String text = tm.getText();
    			System.out.println("MessageReceiver: received new message: " + text);
    		}catch(JMSException e){
    			e.printStackTrace();
    		}
    	}
    	else if(msg instanceof ObjectMessage){
    		ObjectMessage obj = (ObjectMessage)msg;
    		try {
    			//prijavio se novi korisnik, javi svima!
				if(msg.getJMSType().equals("login")){
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
				}
				else if(msg.getJMSType().equals("logout")){
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
				}
				else if(msg.getJMSType().equals("login_jms")){
					try{
						System.out.println("[MessageReceiver]");
						String sessionID = msg.getStringProperty("json");
						System.out.println("Session: " + sessionID);
						User u = new User();
						User user = (User)obj.getObject();
						System.out.println("User:" + user.toString());
						UserBean ub = new UserBean();
						boolean retVal = false;
						try {
							retVal = ub.login(user.getUsername(), user.getPassword(), sessionID);
							System.out.println("User added? " + retVal);
						} catch (InvalidCredentialsException e1) {
							e1.printStackTrace();
						}
						javax.websocket.Session session = null;
						//finding the session
						for(javax.websocket.Session s: WSManager.sessions){
							if(s.getId().equals(sessionID)){
								session = s;
								break;
							}
						}
						
						try {
							if(retVal && session!=null)
								session.getBasicRemote().sendText("success");
							else
								session.getBasicRemote().sendText("error");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("[/MessageReceiver]");
					}catch(JMSException e){
						e.printStackTrace();
					}
				}
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

}
