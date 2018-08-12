package com.fengzhixuan.timoc.websocket.message.game;

public class GameCardPileMessage extends GameMessage
{
    private int[] pile;

    public GameCardPileMessage(int[] pile)
    {
        super(MessageType.Hand);
        this.pile = pile;
    }

    public int[] getPile()
    {
        return pile;
    }
}
