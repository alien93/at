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
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

/**
 * Message-Driven Bean implementation class for: SenderReceiver
 */
/*@MessageDriven(
		activationConfig = { 
			@ActivationConfigProperty(
				propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
			@ActivationConfigProperty(
				propertyName = "destination", propertyValue = "queue/mojQueue")	
		})
public class UserSenderReceiver implements MessageListener {
*/
	
    /**
     * Default constructor. 
     */
  /*  public UserSenderReceiver() {
    	System.out.println("UserSenderReceiver created");
    }*/
	
    
	/**
     * @see MessageListener#onMessage(Message)
     */
 /*   public void onMessage(Message msg) {
    	if(msg instanceof TextMessage){
    		TextMessage tm = (TextMessage) msg;
    		try{
    			String text = tm.getText();
    			System.out.println("UserSenderReceiver: received new message: " + text);
    		}catch(JMSException e){
    			e.printStackTrace();
    		}
    	}
    }

}
*/