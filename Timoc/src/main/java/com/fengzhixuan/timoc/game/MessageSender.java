package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.websocket.message.game.GameMessage;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageSender
{
    // value passed by Controller via gameStart(), used for sending messages to players
    private SimpMessageSendingOperations messagingTemplate;
    private String codeString;

    // message buffers
    private List<GameMessage> displayMessages = new ArrayList<>();
    private List<GameMessage> controllerMessages = new ArrayList<>();

    public MessageSender(SimpMessageSendingOperations messagingTemplate, String codeString)
    {
        this.messagingTemplate = messagingTemplate;
        this.codeString = codeString;
    }

    public void addDisplayMessage(GameMessage message)
    {
        displayMessages.add(message);
    }

    public void addControllerMessage(GameMessage message)
    {
        controllerMessages.add(message);
    }

    // send out all messages and clear buffers
    public void flush()
    {
        if (displayMessages.size() > 0)
        {
            messagingTemplate.convertAndSend("/topic/display/" + codeString, displayMessages);
            displayMessages = new ArrayList<>();
        }
        if (controllerMessages.size() > 0)
        {
            messagingTemplate.convertAndSend("/topic/controller/" + codeString, controllerMessages);
            controllerMessages = new ArrayList<>();
        }
    }
}
