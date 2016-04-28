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
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import exception.InvalidCredentialsException;
import exception.UsernameExistsException;
import model.Host;
import model.User;
import session.UserBeanRemote;
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
						for(int i=0; i<Host.hosts.size(); i++){
							ResteasyClient client = new ResteasyClientBuilder().build();
							String ip = Host.hosts.get(i).getAddress();
							String val = "http://" + ip + ":8080/ChatAppWeb/rest/host/addUser";
							ResteasyWebTarget target = client.target(val);
							target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
							
						}
					} catch (JMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if(msg.getJMSType().equals("logout")){
					try {
						User user = (User)obj.getObject();
						
						//prodji kroz listu svih cvorova i posalji adduser zahtev
						for(int i=0; i<Host.hosts.size(); i++){
							ResteasyClient client = new ResteasyClientBuilder().build();
							String ip = Host.hosts.get(i).getAddress();
							String val = "http://" + ip + ":8080/ChatAppWeb/rest/host/removeUser";
							ResteasyWebTarget target = client.target(val);
							target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
							
						}
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
						User user = (User)obj.getObject();
						System.out.println("User:" + user.toString());
						Context context = new InitialContext();
						String remoteName = "java:global/ChatApp/ChatAppWeb/UserBean!session.UserBean";
						UserBeanRemote ub = (UserBeanRemote)context.lookup(remoteName);
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
							e.printStackTrace();
						}
						System.out.println("[/MessageReceiver]");
					}catch(JMSException e){
						e.printStackTrace();
					} catch (NamingException e2) {
						e2.printStackTrace();
					}
				}
				else if(msg.getJMSType().equals("register_jms")){
					try{
						System.out.println("[MessageReceiver]");
						String sessionID = msg.getStringProperty("json");
						System.out.println("Session: " + sessionID);
						User user = (User)obj.getObject();
						System.out.println("User:" + user.toString());
						Context context = new InitialContext();
						String remoteName = "java:global/ChatApp/ChatAppWeb/UserBean!session.UserBean";
						UserBeanRemote ub = (UserBeanRemote)context.lookup(remoteName);						User retVal = null;
						try {
							retVal = ub.register(user.getUsername(), user.getPassword());
							System.out.println("User registered? " + retVal!=null?true:false);
						} catch (UsernameExistsException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
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
							if(retVal!=null && session!=null)
								session.getBasicRemote().sendText("success");
							else
								session.getBasicRemote().sendText("error");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("[/MessageReceiver]");
					}catch(JMSException | NamingException e){
						e.printStackTrace();
					}
				}
				else if(msg.getJMSType().equals("logout_jms")){
					try{
						System.out.println("[MessageReceiver]");
						String sessionID = msg.getStringProperty("json");
						System.out.println("Session: " + sessionID);
						User user = (User)obj.getObject();
						System.out.println("User:" + user.toString());
						Context context = new InitialContext();
						String remoteName = "java:global/ChatApp/ChatAppWeb/UserBean!session.UserBean";
						UserBeanRemote ub = (UserBeanRemote)context.lookup(remoteName);						Boolean retVal = false;
						retVal = ub.logout(user);
						System.out.println("User removed? " + retVal!=null?true:false);
						
						javax.websocket.Session session = null;
						//finding the session
						for(javax.websocket.Session s: WSManager.sessions){
							if(s.getId().equals(sessionID)){
								session = s;
								break;
							}
						}
						
						try {
							if(retVal!=null && session!=null)
								session.getBasicRemote().sendText("success");
							else
								session.getBasicRemote().sendText("error");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("[/MessageReceiver]");
						try {
							WSManager.getInstance().addUsers();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}catch(JMSException | NamingException e){
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
