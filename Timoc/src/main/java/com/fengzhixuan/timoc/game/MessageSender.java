package com.fengzhixuan.timoc.game;

import com.fengzhixuan.timoc.websocket.message.game.GameMessage;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageSender
{
    // value passed by Controller via gameStart(), used for sending messages to players
    private SimpMessageSendingOperations messagingTemplate;
    private String codeString;

    // message buffers
    private List<GameMessage> displayMessages = new ArrayList<>();
    private List<GameMessage> controllerMessages = new ArrayList<>();
    private Map<String, List<GameMessage>> usernameControllerMsgsMap = new HashMap<>();

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

    public void addTargetedControllerMessage(GameMessage message, String name)
    {
        if (!usernameControllerMsgsMap.containsKey(name))
            usernameControllerMsgsMap.put(name, new ArrayList<>());

        usernameControllerMsgsMap.get(name).add(message);
    }

    // send out all messages and clear buffers
    public void flush()
    {
        if (displayMessages.size() > 0)
        {
            messagingTemplate.convertAndSend("/topic/display/" + codeString, displayMessages);
            displayMessages.clear();
        }
        if (controllerMessages.size() > 0)
        {
            messagingTemplate.convertAndSend("/topic/controller/" + codeString, controllerMessages);
            controllerMessages.clear();
        }

        for (Map.Entry<String, List<GameMessage>> entry : usernameControllerMsgsMap.entrySet())
        {
            String name = entry.getKey();
            List<GameMessage> messages = entry.getValue();
            messagingTemplate.convertAndSendToUser(name, "/topic/controller/" + codeString, messages);
            messages.clear();
        }
    }
}
