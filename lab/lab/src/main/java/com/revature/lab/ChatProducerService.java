package com.revature.lab;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ChatProducerService {
    private static final String QUIT_STRING = "EXIT";

    private final KafkaTemplate<String, String> template;
    private String user;
    private final Scanner in = new Scanner(System.in);

    public ChatProducerService(KafkaTemplate<String, String> kafkaTemplate){
        template = kafkaTemplate;
    }

    public void setUsername(){
        IO.print("Enter a username to use: ");
        user = in.nextLine();
        IO.println("You joined the chat. Type \"" + QUIT_STRING + "\" to leave.\n");
        template.send("message-topic", "System", "\"" + user + "\" joined the chat!");
    }
    public String getUsername(){
        return user;
    }

    // returns true if still in the chat, false if left the chat
    public boolean sendMessage(){
        String input = in.nextLine();

        if (input.equals(QUIT_STRING)){
            template.send("message-topic", "System", "\"" + user + "\" left the chat.");
            in.close();
            return false;
        }

        template.send("message-topic", user, input);
        return true;
    }
}
