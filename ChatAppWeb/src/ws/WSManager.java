package ws;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpRequest;
import org.json.JSONObject;
import org.json.JSONWriter;

import entity.User;

//localhost:8080/ChatAppWeb/websocket
//POJO
//Server endpoint handles incoming WebSocket messages
@ServerEndpoint(value="/websocket", encoders={UserEncoder.class})
public class WSManager {

	List<Session> sessions = new ArrayList<Session>();
	Timer t = new Timer();
	
	public WSManager(){
		
	}
	
	//let the user know that the handshake was successful
	@OnOpen
	public void onOpen(Session session){
		System.out.println(session.getId() + " has opened a connection");
		if(!sessions.contains(session)){
			sessions.add(session);		//dodaj sesiju ukoliko ne postoji
		}
		try{
			session.getBasicRemote().sendText("Connection established...");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@OnMessage
	public void onMessage(Session session, String message, boolean last){
		System.out.println("Message from " + session.getId() + ":" + message);
		try{
			if(session.isOpen()){
				//check if login
				System.out.println(message);
				
				JSONObject jsonmsg = new JSONObject(message);
				//login
				if(jsonmsg.getString("type").equals("login")){
					String username = jsonmsg.getString("username");
					String password = jsonmsg.getString("password");
					
					//rest
					ResteasyClient client = new ResteasyClientBuilder().build();
					String val = "http://localhost:8080/ChatAppWeb/rest/user/login/"+ username + "/"+password+";username=" + username + ";password=" + password;
					ResteasyWebTarget target = client.target(val);
					Response response = target.request().get();
					Boolean ret = response.readEntity(Boolean.class);
					if (ret == true){
						session.getBasicRemote().sendText("success");
						
					}
					else{
						session.getBasicRemote().sendText("error");
					}
					
				}
				else if(jsonmsg.getString("type").equals("register")){
					String username = jsonmsg.getString("username");
					String password = jsonmsg.getString("password");
					//rest
					ResteasyClient client = new ResteasyClientBuilder().build();
					String val = "http://localhost:8080/ChatAppWeb/rest/user/register/"+ username + "/"+password+";username=" + username + ";password=" + password;
					ResteasyWebTarget target = client.target(val);
					Response response = target.request(MediaType.APPLICATION_JSON).get();
					User ret = response.readEntity(User.class);
					if(ret!=null){
						session.getBasicRemote().sendText("success");
						try {
							session.getBasicRemote().sendObject(ret);
						} catch (EncodeException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else{
						session.getBasicRemote().sendText("error");
					}
				}
				else{
					System.out.println(jsonmsg);
				}
				
					
					//create REST or JMS request to UserApp
			
				for(Session s: sessions){
					if(!s.getId().equals(session.getId())){
						s.getBasicRemote().sendText(message, last);
					}
				}
			}
		}catch(IOException e){
			try{
				session.close();
			}catch(IOException e1){
				e1.printStackTrace();
			}
		}
	}
	
	@OnClose
	public void onClose(Session session){
		System.out.println("Session " + session.getId() + " has ended");
		/*ResteasyClient client = new ResteasyClientBuilder().build();
		String val = "http://localhost:8080/ChatAppWeb/rest/user/logout";
		ResteasyWebTarget target = client.target(val);
		target.request(MediaType.APPLICATION_JSON).post(Entity.entity(new User("",""), MediaType.APPLICATION_JSON));	//logout user TODO: add user
		*/sessions.remove(session);
	}
	
	@OnError
	public void onError(Session session, Throwable t){
		sessions.remove(session);
		t.printStackTrace();
	}
	
	
}