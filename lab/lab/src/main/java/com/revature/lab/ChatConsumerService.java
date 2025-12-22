package com.revature.lab;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

@Service
public class ChatConsumerService {
    private final static String consumerId = "consumer";

    private final KafkaListenerEndpointRegistry registry;
    String user;

    public ChatConsumerService(KafkaListenerEndpointRegistry endpointRegistry){
        registry = endpointRegistry;
    }

    public void startConsumer(String username) {
        user = username;
        registry.getListenerContainer(consumerId).start();
    }
    public void endConsumer() {
        registry.getListenerContainer(consumerId).stop();
    }

    @KafkaListener(id = consumerId, topics = "message-topic", groupId = "#{T(java.util.UUID).randomUUID().toString()}", autoStartup = "false") // what are we listening for?
    public void consume(ConsumerRecord<String, String> record){ // when a new message is produced, it triggers our method with that message
        String key = record.key();
        String message = record.value();

        if (!user.equals(key)) System.out.println("[" + key + "]: " + message);
    }
}
