package personal.learning.jms.listener;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import personal.learning.jms.model.OrderHistoryReq;
import personal.learning.jms.model.OrderDetails;
import personal.learning.jms.service.OrderDetailsService;

public class OrderHistReqMessageListener implements MessageListener {
	
	private Session session;
	
	public OrderHistReqMessageListener(Session session) {
		this.session = session;
	}

	@Override
	public void onMessage(Message message) {
		
		try {
			
			OrderHistoryReq requestMessage = message.getBody(OrderHistoryReq.class);
			
			System.out.println("Message received : " + requestMessage);
			
			String fromDate = requestMessage.getFromDate();
			String toDate = requestMessage.getToDate();
			String username = requestMessage.getUsername();
			
			ArrayList<OrderDetails> orderDetailsList = new OrderDetailsService().getOrderDetails(username, fromDate, toDate);
			
			MessageProducer orderDetailProducer = session.createProducer(message.getJMSReplyTo());
			
			ObjectMessage orderDetailsListMsg = session.createObjectMessage();
			orderDetailsListMsg.setObject(orderDetailsList);
			
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			orderDetailProducer.send(orderDetailsListMsg);
			
			message.acknowledge();
			
		} catch(JMSException ex) {
			try {
				if(session.getAcknowledgeMode() == 0) {
					// Rollback the transaction in case of an exception
					session.rollback();
				} else {
					ex.printStackTrace();
				}
            } catch (JMSException e) {
            	e.printStackTrace();
            }
		}
	}

}
