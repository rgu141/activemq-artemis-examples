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
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.MessageListener;
import javax.jms.JMSException;

import org.apache.qpid.jms.JmsConnectionFactory;

public class Consumer {
    private static final Object lock = new Object();

    public static void main(String[] args) {
        Connection connection = null;
        MessageConsumer consumer = null;
        Session session = null;
        
        ConnectionFactory connectionFactory = new JmsConnectionFactory("amqp://localhost:1414");
        
        try {
            // Step 1. Create an amqp qpid 1.0 connection
            connection = connectionFactory.createConnection();

            // Step 2. Create a session
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            // Step 3. Create a sender
            Queue queue = session.createQueue("NonDurableSubQueue");
            
            // Create connection
            connection.start();

            // Create consumer
            consumer = session.createConsumer(queue);

            // Set up a message listener
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        if (message instanceof TextMessage) {
                            System.out.println("Received message: " + ((TextMessage) message).getText());
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Wait until the program is manually stopped
            synchronized (lock) {
                lock.wait();
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            try {
                if (consumer != null) consumer.close();
                if (session != null) session.close();
                if (connection != null) connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
