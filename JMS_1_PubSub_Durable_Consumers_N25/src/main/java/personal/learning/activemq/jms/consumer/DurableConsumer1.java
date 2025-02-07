package personal.learning.activemq.jms.consumer;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.Topic;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class DurableConsumer1 {
	
	public static void main(String[] args) {
		String brokerUrl = "tcp://localhost:61616";
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		Connection connection = null;
		Session session = null;
		
		try {
			connection = connectionFactory.createConnection();
			// The client ID must be set before creating the session to avoid runtime exceptions.
			connection.setClientID("firstSubscriberID"); 
			
			session = connection.createSession(Session.AUTO_ACKNOWLEDGE);
			
			Topic topic = session.createTopic("topic");
			
			MessageConsumer consumer = session.createDurableConsumer(topic, "firstSubscription");
			
			connection.start();
			
			System.out.println("Consumer1 subscribed to topic");
			
			while(true) {
				Message message = consumer.receive();
				System.out.println("Message received by consumer1: " + message.getBody(String.class));
			}
			
		} catch(JMSException ex) {
			
			try {
				if(session == null) {
					session.close();
				}
				
				if(connection == null) {
					connection.close();
				}
			} catch(JMSException e) {
				e.printStackTrace();
			}
		}
	}
}
