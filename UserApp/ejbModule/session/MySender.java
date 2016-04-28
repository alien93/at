package session;


import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

import model.User;

/**
 * Session Bean implementation class MySender
 */
@Stateless
public class MySender implements MySenderLocal {
    /**
     * Default constructor. 
     */
    public MySender() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public void sendMessage(String txt) throws JMSException {
		try{
			Context context = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) context.lookup("java:/ConnectionFactory");
			final Queue target = (Queue) context.lookup("java:jboss/exported/jms/queue/mojQueue");
			context.close();
			
			System.out.println(factory);
			System.out.println(target);
			Connection con = factory.createConnection();
				try{
					Session session  = con.createSession(false,  Session.AUTO_ACKNOWLEDGE);
					MessageProducer producer = session.createProducer(target);
					producer.send(session.createTextMessage(txt));
				}
				finally{
					con.close();
				}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void sendMessage(Object obj, String msgType) throws JMSException {
		try{
			Context context = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) context.lookup("java:/ConnectionFactory");
			final Queue target = (Queue) context.lookup("java:jboss/exported/jms/queue/mojQueue");
			context.close();
			
			System.out.println(factory);
			System.out.println(target);
			Connection con = factory.createConnection();
				try{
					Session session  = con.createSession(false,  Session.AUTO_ACKNOWLEDGE);
					MessageProducer producer = session.createProducer(target);
					if(obj instanceof User){
						if(msgType.equals("login")){
							ObjectMessage message = session.createObjectMessage((User)obj);
							message.setJMSType("login");
							producer.send(message);
						}
						else if(msgType.equals("logout")){
							ObjectMessage message = session.createObjectMessage((User)obj);
							message.setJMSType("logout");
							producer.send(message);
						}
					}
				}
				finally{
					con.close();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		
	}

	@Override
	public void sendMessage(Object obj, String sessionId, String msgType) throws JMSException {
		try{
			Context context = new InitialContext();
			ConnectionFactory factory = (ConnectionFactory) context.lookup("java:/ConnectionFactory");
			final Queue target = (Queue) context.lookup("java:jboss/exported/jms/queue/mojQueue");
			context.close();
			
			System.out.println(factory);
			System.out.println(target);
			Connection con = factory.createConnection();
				try{
					Session session  = con.createSession(false,  Session.AUTO_ACKNOWLEDGE);
					MessageProducer producer = session.createProducer(target);
					if(obj instanceof User){
						if(msgType.equals("login_jms")){
							ObjectMessage message = session.createObjectMessage((User)obj);
							message.setJMSType("login_jms");
							message.setStringProperty("json", sessionId);
							producer.send(message);
						}
						else if(msgType.equals("logout_jms")){
							ObjectMessage message = session.createObjectMessage((User)obj);
							message.setJMSType("logout_jms");
							message.setStringProperty("json", sessionId);
							producer.send(message);
						}
						else if(msgType.equals("register_jms")){
							ObjectMessage message = session.createObjectMessage((User)obj);
							message.setJMSType("register_jms");
							message.setStringProperty("json", sessionId);
							producer.send(message);
						}
					}
				}
				finally{
					con.close();
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
		
	}

}
