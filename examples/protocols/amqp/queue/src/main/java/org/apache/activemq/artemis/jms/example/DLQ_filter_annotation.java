package org.apache.activemq.artemis.jms.example;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.qpid.jms.JmsConnectionFactory;

public class DLQ_filter_annotation {
    public static void main(String[] args) throws Exception {
        Connection connection = null;
        ConnectionFactory connectionFactory = new JmsConnectionFactory("amqp://localhost:1414");
  
        try {
  
           // Step 1. Create an amqp qpid 1.0 connection
           connection = connectionFactory.createConnection();
  
           // Step 2. Create a session
           Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
  
           connection.start();
  
           Queue queue = session.createQueue("DLQ");
  
           // Step 5. create a moving receiver, this means the message will be removed from the queue
           MessageConsumer consumer = session.createConsumer(queue, "\"m.x-opt-ORIG-QUEUE\"=Queue1");

           //Search instead using internal property, instead of annotations ->
           //MessageConsumer consumer = session.createConsumer(queue, "_AMQ_ORIG_ADDRESS='Queue1'");
           
           // Step 7. receive the simple message
           TextMessage m = (TextMessage) consumer.receive(1000); //1000 here is number of milliseconds to wait for a message, before coming out
           System.out.println("message = " + m.getText());
        } finally {
           if (connection != null) {
              // Step 9. close the connection
              connection.close();
           }
        }
     }
}
