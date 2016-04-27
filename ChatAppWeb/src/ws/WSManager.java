package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.jms.JMSException;
import javax.jms.TextMessage;
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
import org.json.JSONObject;

import com.sun.corba.se.impl.orbutil.ObjectWriter;

import model.Host;
import model.Message;
import model.User;
import session.MySender;
import session.MySenderLocal;
import session.UserList;


//localhost:8080/ChatAppWeb/websocket
//POJO
//Server endpoint handles incoming WebSocket messages
@ServerEndpoint(value="/websocket", encoders={UserEncoder.class})
public class WSManager {

	Timer t = new Timer();
	String usr = "";
	private static WSManager instance  = new WSManager();
	String master = "";
	String address = "";
	String alias = "";
	boolean sameNode = false;
	public transient static List<Session> sessions = new ArrayList<Session>();
	MySenderLocal sender = new MySender();


	@SuppressWarnings("unchecked")
	public WSManager(){
		//send register request if you're not a master node
				PropertiesReader pr = new PropertiesReader();
				master = pr.getMaster();					//master node address
				address = pr.getLocal();
				alias = address;							//TODO: something else?
				if(!master.equals("")){						//chat and user app are not on the same node
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
				else{	//chat and user app are on the same node
					sameNode = true;
				}
	}
	
	public static WSManager getInstance(){
		return instance;
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
	}
	
	@OnMessage
	public void onMessage(Session session, String message, boolean last){
		System.out.println("Message from " + session.getId() + ":" + message);
		try{
			if(session.isOpen()){
				if(!sameNode){
					//check if login			
					JSONObject jsonmsg = new JSONObject(message);
					
					//login
					if(jsonmsg.getString("type").equals("login")){
						String username = jsonmsg.getString("username");
						String password = jsonmsg.getString("password");
						usr = username;
						//rest
						ResteasyClient client = new ResteasyClientBuilder().build();
						String val = "http://localhost:8080/ChatAppWeb/rest/user/login/"+ username + "/"+password+ "/" + session.getId() + ";username=" + username + ";password=" + password + ";session=" + session.getId();
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
					//register
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
					//logout
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
							addUsers();
						}
						else{
							session.getBasicRemote().sendText("error");
						}
					}
					//message
					else if(jsonmsg.getString("type").equals("message")){
						String to = jsonmsg.getString("to");
						String from = jsonmsg.getString("from");
						String date = jsonmsg.getString("date");
						String subject = jsonmsg.getString("subject");
						String content = jsonmsg.getString("message");
						User user = new User();
						User fromUser = user.getUserByUsername(from);
						System.out.println("From user: " + fromUser);
						User toUser = null;
						if(!to.equals(""))							//ukoliko je primalac definisan, prosledi ga, u suprotnom posalji null
							toUser = user.getUserByUsername(to);
						System.out.println("To user: " + toUser);
						System.out.println(date);
						System.out.println(subject);
						System.out.println(content);
						
						//publish message
						//rest/message/publish
						//rest
						if(toUser!=null){
							//poruka je privatna, proveri da li je host primaoca trenutni cvor
							//TODO
							//if(!address.equals("") && toUser.getHost().getAddress().equals(address)){
								//ukoliko host primaoca jeste trenutni cvor, putem websocket-a prikazi poruku datom korisniku
								Session fromUserSession = null;
								for(Session s: sessions){
									if(s.getId().equals(fromUser.getSessionID())){
										fromUserSession = s;
										break;
									}
								}
							
							
								for(Session ses : sessions){
									if(ses.getId().equals(toUser.getSessionID())){	
										//publish it
										Message msg = new Message(fromUser, toUser, date, subject, content);
										//String ip = toUser.getHost().getAddress();
										String ip = "localhost";
										ResteasyClient client = new ResteasyClientBuilder().build();
										String val = "http://"+ ip +":8080/ChatAppWeb/rest/message/publish";
										ResteasyWebTarget target = client.target(val);
										Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(msg, MediaType.APPLICATION_JSON));
										Boolean ret = response.readEntity(Boolean.class);
										if (ret == true){
											//session.getBasicRemote().sendText("success");	
											//addUsers();
											ses.getBasicRemote().sendText("success_message");
											sendAMessage(toUser, ses);
											if(fromUserSession!=null)
												sendAMessage(fromUser, fromUserSession);
											System.out.println("[WS] true");
										}
										else{
											//session.getBasicRemote().sendText("error");
											ses.getBasicRemote().sendText("error_message");	
											System.out.println("[WS] false");
										}
									}
								}
								
							//}
							
						}
						else{
							//formiraj publish zahtev za svaki cvor u cluster-u
							//for(int i=0; i<Host.hosts.size(); i++){
								Message msg = new Message(fromUser, toUser, date, subject, content);
								//String ip = Host.hosts.get(i).getAddress();
								String ip = "localhost";
								ResteasyClient client = new ResteasyClientBuilder().build();
								String val = "http://"+ ip +":8080/ChatAppWeb/rest/message/publish";
								ResteasyWebTarget target = client.target(val);
								Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(msg, MediaType.APPLICATION_JSON));
								Boolean ret = response.readEntity(Boolean.class);
								if (ret == true){
									//session.getBasicRemote().sendText("success");	
									//addUsers();
									session.getBasicRemote().sendText("success_message");
									sendMessages();
									System.out.println("[WS] true");
								}
								else{
									//session.getBasicRemote().sendText("error");
									session.getBasicRemote().sendText("error_message");	
									System.out.println("[WS] false");
								}
						}
					}
					//get loggedUsers
					else if(jsonmsg.getString("type").equals("getLoggedUsers")){
						addUsers();
					}
					
				}
				else{	//userapp and chatapp are not on the same node, using jms...
					System.out.println("Sending as jms...");
					//check if login			
					JSONObject jsonmsg = new JSONObject(message);
					
					//login
					if(jsonmsg.getString("type").equals("login")){
						String username = jsonmsg.getString("username");
						String password = jsonmsg.getString("password");
						usr = username;
						User user = new User(username, password, new Host(address, address), session.getId());
						
						try {
							sender.sendMessage(user, session.getId(), "login_jms");
							//addUsers();
						} catch (JMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					//register
					else if(jsonmsg.getString("type").equals("register")){
						String username = jsonmsg.getString("username");
						String password = jsonmsg.getString("password");						
						User user = new User(username, password);
						try{
							sender.sendMessage(user, session.getId(), "register_jms");
						}catch(JMSException e){
							e.printStackTrace();
						}
						
					}
					//logout
					else if(jsonmsg.getString("type").equals("logout")){
						String username = jsonmsg.getString("username");
					
						User user = new User();
						user = user.getUserByUsername(username);
						
						//rest
						/*ResteasyClient client = new ResteasyClientBuilder().build();
						String val = "http://localhost:8080/ChatAppWeb/rest/user/logout";
						ResteasyWebTarget target = client.target(val);
						Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(user, MediaType.APPLICATION_JSON));
						Boolean ret = response.readEntity(Boolean.class);
						if (ret == true){
							session.getBasicRemote().sendText("success");	
							addUsers();
						}
						else{
							session.getBasicRemote().sendText("error");
						}*/
						try{
							sender.sendMessage(user, session.getId(), "logout_jms");
						}catch(JMSException e){
							e.printStackTrace();
						}
						
						
					}
					//message
					else if(jsonmsg.getString("type").equals("message")){
						String to = jsonmsg.getString("to");
						String from = jsonmsg.getString("from");
						String date = jsonmsg.getString("date");
						String subject = jsonmsg.getString("subject");
						String content = jsonmsg.getString("message");
						User user = new User();
						User fromUser = user.getUserByUsername(from);
						System.out.println("From user: " + fromUser);
						User toUser = null;
						if(!to.equals(""))							//ukoliko je primalac definisan, prosledi ga, u suprotnom posalji null
							toUser = user.getUserByUsername(to);
						System.out.println("To user: " + toUser);
						System.out.println(date);
						System.out.println(subject);
						System.out.println(content);
						
						//publish message
						//rest/message/publish
						//rest
						if(toUser!=null){
							//poruka je privatna, proveri da li je host primaoca trenutni cvor
							//TODO
							//if(!address.equals("") && toUser.getHost().getAddress().equals(address)){
								//ukoliko host primaoca jeste trenutni cvor, putem websocket-a prikazi poruku datom korisniku
								Session fromUserSession = null;
								for(Session s: sessions){
									if(s.getId().equals(fromUser.getSessionID())){
										fromUserSession = s;
										break;
									}
								}
							
								System.out.println("Saljem poruku svima");
								System.out.println("Broj postojecih sesija je: " + sessions.size());
								for(Session ses : sessions){
									if(ses.getId().equals(toUser.getSessionID())){	
										//publish it
										Message msg = new Message(fromUser, toUser, date, subject, content);
										//String ip = toUser.getHost().getAddress();
										String ip = "localhost";
										ResteasyClient client = new ResteasyClientBuilder().build();
										String val = "http://"+ ip +":8080/ChatAppWeb/rest/message/publish";
										ResteasyWebTarget target = client.target(val);
										Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(msg, MediaType.APPLICATION_JSON));
										Boolean ret = response.readEntity(Boolean.class);
										if (ret == true){
											//session.getBasicRemote().sendText("success");	
											//addUsers();
											ses.getBasicRemote().sendText("success_message");
											sendAMessage(toUser, ses);
											if(fromUserSession!=null)
												sendAMessage(fromUser, fromUserSession);
											System.out.println("[WS] true");
										}
										else{
											//session.getBasicRemote().sendText("error");
											ses.getBasicRemote().sendText("error_message");	
											System.out.println("[WS] false");
										}
									}
								}
								
							//}
							
						}
						else{
							//formiraj publish zahtev za svaki cvor u cluster-u
							//for(int i=0; i<Host.hosts.size(); i++){
								Message msg = new Message(fromUser, toUser, date, subject, content);
								//String ip = Host.hosts.get(i).getAddress();
								String ip = "localhost";
								ResteasyClient client = new ResteasyClientBuilder().build();
								String val = "http://"+ ip +":8080/ChatAppWeb/rest/message/publish";
								ResteasyWebTarget target = client.target(val);
								Response response = target.request(MediaType.APPLICATION_JSON).post(Entity.entity(msg, MediaType.APPLICATION_JSON));
								Boolean ret = response.readEntity(Boolean.class);
								if (ret == true){
									//session.getBasicRemote().sendText("success");	
									//addUsers();
									session.getBasicRemote().sendText("success_message");
									sendMessages();
									System.out.println("[WS] true");
								}
								else{
									//session.getBasicRemote().sendText("error");
									session.getBasicRemote().sendText("error_message");	
									System.out.println("[WS] false");
								}
						}
					}
					//get loggedUsers
					else if(jsonmsg.getString("type").equals("getLoggedUsers")){
						addUsers();
					}
								
					/*for(Session s: sessions){
						if(!s.getId().equals(session.getId())){
							s.getBasicRemote().sendText(message, last);
						}
					}*/
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
	
	public void addUsers() throws IOException{
		for (Session session : sessions){
			ResteasyClient client = new ResteasyClientBuilder().build();
			String val = "http://localhost:8080/ChatAppWeb/rest/user/loggedUsers";
			ResteasyWebTarget target = client.target(val);
			Response response = target.request().get();
			UserList ret1 = response.readEntity(UserList.class);
			System.out.println("creating list for session: " + session.getId());
			if (ret1 != null){
				session.getBasicRemote().sendText("success_loggedUsers");	
				try {
					session.getBasicRemote().sendObject(ret1);
				} catch (EncodeException e) {
					e.printStackTrace();
				}
			}
			else{
				session.getBasicRemote().sendText("error_loggedUsers");
			}
		}
	}
	
	public void sendMessages() throws IOException{
		for(Session session: sessions){
			ResteasyClient client = new ResteasyClientBuilder().build();
			String val = "http://localhost:8080/ChatAppWeb/rest/message/messagesForSession/"+ session.getId() +";session=" + session.getId();
			ResteasyWebTarget target = client.target(val);
			Response response = target.request().get();
			System.out.println("Response: " + response);
			MessageList ret1 = response.readEntity(MessageList.class);
			System.out.println("creating list for session: " + session.getId());
			if (ret1 != null){
				session.getBasicRemote().sendText("success_message");	
				try {
					session.getBasicRemote().sendObject(ret1);
				} catch (EncodeException e) {
					e.printStackTrace();
				}
			}
			else{
				session.getBasicRemote().sendText("error_message");
			}
		}
	}
	
	public void sendAMessage(User toUser, Session ses){
		try{
			ResteasyClient client = new ResteasyClientBuilder().build();
			String val = "http://localhost:8080/ChatAppWeb/rest/message/messagesForSession/"+ toUser.getSessionID() +";session=" + toUser.getSessionID();
			ResteasyWebTarget target = client.target(val);
			Response response = target.request().get();
			System.out.println("Response: " + response);
			MessageList ret1 = response.readEntity(MessageList.class);
			System.out.println("creating list for session: " + ses.getId());
			if(ses!=null){
				if (ret1 != null){
					ses.getBasicRemote().sendText("success_message");	
					try {
						ses.getBasicRemote().sendObject(ret1);
					} catch (EncodeException e) {
						e.printStackTrace();
					}
				}
				else{
					ses.getBasicRemote().sendText("error_message");
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}