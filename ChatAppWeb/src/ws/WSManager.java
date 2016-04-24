package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

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
import org.json.JSONObject;

import entity.Host;
import entity.User;
import session.HostsList;
import session.PropertiesReader;
import session.UserList;


//localhost:8080/ChatAppWeb/websocket
//POJO
//Server endpoint handles incoming WebSocket messages
@ServerEndpoint(value="/websocket", encoders={UserEncoder.class})
public class WSManager {

	List<Session> sessions = new ArrayList<Session>();
	Timer t = new Timer();
	String usr = "";
	
	public WSManager(){
		
	}
	
	//let the user know that the handshake was successful
	@SuppressWarnings("unchecked")
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
		
		//send register request if you're not a master node
		PropertiesReader pr = new PropertiesReader();
		String master = pr.getMaster();					//master node address
		String address = pr.getLocal();
		String alias = address;							//TODO: something else?
		if(!master.equals("")){
			ResteasyClient client = new ResteasyClientBuilder().build();
			String val = "http://" + master + ":8080/ChatAppWeb/rest/host/register/"+ address + "/"+alias+";address=" + address + ";alias=" + alias;
			ResteasyWebTarget target = client.target(val);
			Response response = target.request().get();
			HostsList ret = response.readEntity(HostsList.class);
			System.out.println("Registring node result... ");
			System.out.println(ret);
			Host.hosts = (List<Host>) ret;
			
			//get all users
			client = new ResteasyClientBuilder().build();
			val = "http://" + master + ":8080/ChatAppWeb/rest/user/allUsers";
			target = client.target(val);
			response = target.request().get();
			UserList ret1 = response.readEntity(UserList.class);
			System.out.println("Getting registeres users...");
			System.out.println(ret1);
			User.registeredUsers = (List<User>)ret1;
		}
	}
	
	@OnMessage
	public void onMessage(Session session, String message, boolean last){
		System.out.println("Message from " + session.getId() + ":" + message);
		try{
			if(session.isOpen()){
				//check if login			
			JSONObject jsonmsg = new JSONObject(message);
				//login
				if(jsonmsg.getString("type").equals("login")){
					String username = jsonmsg.getString("username");
					String password = jsonmsg.getString("password");
					usr = username;
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
					}
					else{
						session.getBasicRemote().sendText("error");
					}
				}
				else if(jsonmsg.getString("type").equals("logout")){
					String username = jsonmsg.getString("username");
				
					User user = new User();
					user = user.getUserByUsername(username);
					
					//rest
					ResteasyClient client = new ResteasyClientBuilder().build();
					String val = "http://localhost:8080/ChatAppWeb/rest/user/logout";
					ResteasyWebTarget target = client.target(val);
					Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
					Boolean ret = response.readEntity(Boolean.class);
					if (ret == true){
						session.getBasicRemote().sendText("success");	
					}
					else{
						session.getBasicRemote().sendText("error");
					}
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
		System.out.println("username: " + usr);
		if(!usr.equals("")){
			User user = new User();
			user = user.getUserByUsername(usr);
			ResteasyClient client = new ResteasyClientBuilder().build();
			String val = "http://localhost:8080/ChatAppWeb/rest/user/logout";
			ResteasyWebTarget target = client.target(val);
			target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
		}
		sessions.remove(session);
	}
	
	@OnError
	public void onError(Session session, Throwable t){
		sessions.remove(session);
		t.printStackTrace();
	}
	
	
}