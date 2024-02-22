/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.artemis.jms.example;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.MessageConsumer;
import javax.naming.InitialContext;

/**
 * A simple JMS example that shows how to use a durable subscription.
 */
public class FilterQueueTopic {

   public static void main(final String[] args) throws Exception {
      Connection connection = null;
      InitialContext initialContext = null;
      try {
         // Step 1. Create an initial context to perform the JNDI lookup.
         initialContext = new InitialContext();

         // Step 2. Look-up the JMS topic
         Topic topic = (Topic) initialContext.lookup("topic/filterTopic");

         // Step 3. Look-up the JMS connection factory
         ConnectionFactory cf = (ConnectionFactory) initialContext.lookup("ConnectionFactory");

         // Step 4. Create a JMS connection
         connection = cf.createConnection();

         // Step 5. Set the client-id on the connection
         connection.setClientID("color");

         // Step 6. Start the connection
         connection.start();

         // Step 7. Create a JMS session
         Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

         // Step 8. Create a JMS message producer
         MessageProducer messageProducer = session.createProducer(topic);

         // Step 9. Create the subscription and the subscriber.
         TopicSubscriber subscriber = session.createDurableSubscriber(topic, "multi");
         TopicSubscriber subscriber2 = session.createDurableSubscriber(topic, "red", "color='red'", false);
         MessageConsumer consumer = session.createConsumer(topic, "multix");
         //The false parameter for noLocal means that the subscriber will receive messages published by other clients on the same topic.

         // Step 10. Create a text message
         for (int i = 0; i < 3; i ++) {
            TextMessage redMessage = session.createTextMessage("Red");
            redMessage.setStringProperty("color", "red");
            messageProducer.send(redMessage);
            TextMessage greenMessage = session.createTextMessage("Green");
            greenMessage.setStringProperty("color", "green");
            messageProducer.send(greenMessage);
         }
         System.out.println("Sent messages");

         // Step 11. Close the subscriber - the server could even be stopped at this point!
         subscriber.close();

         // Step 12. Delete the durable subscription
         //session.unsubscribe("subscriber-1");
      } finally {
         if (connection != null) {
            // Step 13. Be sure to close our JMS resources!
            connection.close();
         }
         if (initialContext != null) {
            // Step 14. Also close the initialContext!
            initialContext.close();
         }
      }
   }
}
