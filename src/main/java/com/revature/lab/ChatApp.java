package com.revature.lab;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.Scanner;
import java.util.UUID;

public class ChatApp {

    private static final String TOPIC = "global-chat";
    private static final String BOOTSTRAP_SERVERS = "localhost:995";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();

      
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);

        
        String groupId = "chat-group-" + UUID.randomUUID().toString();
        
        Thread consumerThread = new Thread(() -> {
            Properties consumerProps = new Properties();
            consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");

            KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
            consumer.subscribe(Collections.singletonList(TOPIC));
            // consume messages (print them to the console)
            try {
                while (true) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                    for (ConsumerRecord<String, String> record : records) {
                        
                        String sender = record.key();
                        String message = record.value();
                        
                        if (sender != null && !sender.equals(username)) {
                            System.out.println("\n[" + sender + "]: " + message);
                            System.out.print("> "); 
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                consumer.close();
            }
        });
        consumerThread.setDaemon(true); 
        consumerThread.start();

        System.out.println("Chat started! Type messages below:");
        System.out.print("> ");
        
        try {
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                
                
                ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC, username, input);
                producer.send(record, (metadata, exception) -> {
                    if (exception != null) {
                        System.err.println("\nError sending message: " + exception.getMessage());
                        exception.printStackTrace();
                    }
                });
                producer.flush();
                System.out.print("> ");
            }
        } finally {
            producer.close();
            scanner.close();
            System.out.println("Chat ended.");
        }
    }
}
