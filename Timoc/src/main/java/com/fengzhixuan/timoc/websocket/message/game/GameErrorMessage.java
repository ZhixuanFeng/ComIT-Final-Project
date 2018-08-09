package com.fengzhixuan.timoc.websocket.message.game;

public class GameErrorMessage extends GameMessage
{
    protected String message;

    public GameErrorMessage(String message)
    {
        type = MessageType.Error;
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
