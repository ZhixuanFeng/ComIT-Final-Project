package com.fengzhixuan.timoc.websocket.message.game;

public class DisplayStateMessage extends GameMessage
{
    private int[] states;

    public DisplayStateMessage(int[] states)
    {
        super(MessageType.DState);
        this.states = states;
    }

    public int[] getStates()
    {
        return states;
    }
}
