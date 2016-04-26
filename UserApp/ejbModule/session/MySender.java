package session;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;

/**
 * Session Bean implementation class MySender
 */
@Stateless
public class MySender implements MySenderLocal {

	/*@Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory factory;
	
	@Resource(mappedName="java:jboss/exported/jms/queue/mojQueue")
	private Queue target;*/
	
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

}
