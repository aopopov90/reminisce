package com.home.reminisce.api.controller;

import com.home.reminisce.model.Comment;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
public class WebSocketHandler {
    @MessageMapping("/app/newComment")
    @SendTo("/topic/newComment")
    @CrossOrigin(origins = "http://localhost:3000")
    public Comment handleNewComment(@Payload Comment newComment) {
        // You can process the new comment if needed
        return newComment; // Send the new comment to all connected clients via /topic/newComment
    }
}
