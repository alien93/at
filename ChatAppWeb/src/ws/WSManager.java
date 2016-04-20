package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

//localhost:8080/ChatAppWeb/websocket
//POJO
//Server endpoint handles incoming WebSocket messages
@ServerEndpoint("/websocket")
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
				String tokens[] = message.split(" ");
				if(tokens[0].equals("login:")){
					//get username and password
					String userpass[] = tokens[1].split(",");
					String username = userpass[0];
					String password = userpass[1];
					
					//create REST or JMS request to UserApp
					
					
					
				}
				
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
		sessions.remove(session);
	}
	
	@OnError
	public void onError(Session session, Throwable t){
		sessions.remove(session);
		t.printStackTrace();
	}
	
	
}