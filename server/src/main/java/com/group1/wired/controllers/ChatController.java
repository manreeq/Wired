package com.group1.wired.controllers;

import com.group1.wired.dto.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    //listens to messages
    @MessageMapping("/sendMessage")
    //sends to the topic messages where subscribed users receive the update
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) { return message; }
}
