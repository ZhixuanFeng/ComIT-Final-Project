package com.fengzhixuan.timoc.data.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "card_collection")
public class CardCollection
{
    @Id
    @Column(name = "id")
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Player player;

    @OneToMany(mappedBy = "cardCollection", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards = new ArrayList<>();

    @Column(name="max_card_storage", nullable=false, columnDefinition="SMALLINT DEFAULT 52")
    private int maxCardStorage;

    @Column(name="cards_owned", nullable=false, columnDefinition="SMALLINT DEFAULT 0")
    private int cardsOwned;

    public CardCollection(Player player)
    {
        this.player = player;
        maxCardStorage = 52;
        cardsOwned = 0;

        player.setCardCollection(this);
    }

    public CardCollection() {}

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public Player getPlayer()
    {
        return player;
    }

    public void setPlayer(Player player)
    {
        this.player = player;
    }

    public List<Card> getCards()
    {
        return cards;
    }

    public void setCards(List<Card> cards)
    {
        this.cards = cards;
    }

    public int getMaxCardStorage()
    {
        return maxCardStorage;
    }

    public void setMaxCardStorage(int maxCardStorage)
    {
        this.maxCardStorage = maxCardStorage;
    }

    public int getCardsOwned()
    {
        return cardsOwned;
    }

    public void setCardsOwned(int cardsOwned)
    {
        this.cardsOwned = cardsOwned;
    }
}
