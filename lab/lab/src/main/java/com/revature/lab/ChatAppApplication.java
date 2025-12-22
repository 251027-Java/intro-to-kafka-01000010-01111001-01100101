package com.revature.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Scanner;

@SpringBootApplication
public class ChatAppApplication {
    private static ChatProducerService pServ;
    private static ChatConsumerService cServ;

    public ChatAppApplication(ChatProducerService chatProducerService, ChatConsumerService chatConsumerService){
        pServ = chatProducerService;
        cServ = chatConsumerService;
    }

	public static void main(String[] args) {
        SpringApplication.run(ChatAppApplication.class, args);

        IO.println();

        // TODO: Initialize Kafka Producer settings
        pServ.setUsername();

        // TODO: Start a Thread for the Consumer (Polling loop)
        cServ.startConsumer(pServ.getUsername());

        // TODO: Main loop reading Scanner(System.in) and sending messages
        while (pServ.sendMessage());

        // bit of time to get the last consumer message out
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        cServ.endConsumer();
	}

}
