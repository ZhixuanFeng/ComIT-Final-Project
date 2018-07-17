package com.fengzhixuan.timoc.data.entity;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "offer")
@SequenceGenerator(name = "offer_seq", sequenceName = "offer_seq", initialValue = 10000)
public class Offer
{
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offer_seq")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_name")
    private Player player;

    // using @ManyToOne to avoid querying offer object when querying card object.  card and offer is actually one-to-one
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id")
    private Card card;

    @Column(name = "indecks", columnDefinition = "TINYINT DEFAULT 0")
    private int indecks;

    @Column(name = "suit", columnDefinition = "TINYINT DEFAULT 0")
    private int suit;

    @Column(name = "rank", columnDefinition = "TINYINT DEFAULT 0")
    private int rank;

    @Column(name="attack", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int attack;

    @Column(name="block", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int block;

    @Column(name="heal", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int heal;

    @Column(name="mana", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int mana;

    @Column(name="draw", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int draw;

    @Column(name="taunt", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int taunt;

    @Column(name="revive", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int revive;

    @Column(name="aoe", nullable=false, columnDefinition = "TINYINT(8) UNSIGNED DEFAULT 0")
    private int aoe;

    @Column(name = "price", columnDefinition = "INT DEFAULT 0")
    private int price;

    @Column(name = "expiry_date")
    private Date expDate;

    public Offer(Player player, Card card, int price)
    {
        this.player = player;
        this.card = card;
        this.indecks = card.getIndecks();
        this.price = price;
    }

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

    public Card getCard()
    {
        return card;
    }

    public void setCard(Card card)
    {
        this.card = card;
    }

    public int getIndecks()
    {
        return indecks;
    }

    public void setIndecks(int indecks)
    {
        this.indecks = indecks;
    }

    public int getSuit()
    {
        return suit;
    }

    public void setSuit(int suit)
    {
        this.suit = suit;
    }

    public int getRank()
    {
        return rank;
    }

    public void setRank(int rank)
    {
        this.rank = rank;
    }

    public int getAttack()
    {
        return attack;
    }

    public void setAttack(int attack)
    {
        this.attack = attack;
    }

    public int getBlock()
    {
        return block;
    }

    public void setBlock(int block)
    {
        this.block = block;
    }

    public int getHeal()
    {
        return heal;
    }

    public void setHeal(int heal)
    {
        this.heal = heal;
    }

    public int getMana()
    {
        return mana;
    }

    public void setMana(int mana)
    {
        this.mana = mana;
    }

    public int getDraw()
    {
        return draw;
    }

    public void setDraw(int draw)
    {
        this.draw = draw;
    }

    public int getTaunt()
    {
        return taunt;
    }

    public void setTaunt(int taunt)
    {
        this.taunt = taunt;
    }

    public int getRevive()
    {
        return revive;
    }

    public void setRevive(int revive)
    {
        this.revive = revive;
    }

    public int getAoe()
    {
        return aoe;
    }

    public void setAoe(int aoe)
    {
        this.aoe = aoe;
    }

    public int getPrice()
    {
        return price;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public Date getExpDate()
    {
        return expDate;
    }

    public void setExpDate(Date expDate)
    {
        this.expDate = expDate;
    }

    public static Specification<Offer> findByCriteria(final Map<String, Integer> searchCriteria) {

        return new Specification<Offer>() {

            @Override
            public Predicate toPredicate(
                    Root<Offer> root,
                    CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (searchCriteria.get("suit") != null) {
                    predicates.add(cb.equal(root.get("suit"), searchCriteria.get("suit")));
                }
                if (searchCriteria.get("rank") != null) {
                    predicates.add(cb.equal(root.get("rank"), searchCriteria.get("rank")));
                }
                if (searchCriteria.get("attack") != null) {
                    predicates.add(cb.equal(root.get("attack"), searchCriteria.get("attack")));
                }
                if (searchCriteria.get("block") != null) {
                    predicates.add(cb.equal(root.get("block"), searchCriteria.get("block")));
                }
                if (searchCriteria.get("heal") != null) {
                    predicates.add(cb.equal(root.get("heal"), searchCriteria.get("heal")));
                }
                if (searchCriteria.get("mana") != null) {
                    predicates.add(cb.equal(root.get("mana"), searchCriteria.get("mana")));
                }
                if (searchCriteria.get("aoe") != null) {
                    predicates.add(cb.equal(root.get("aoe"), searchCriteria.get("aoe")));
                }
                if (searchCriteria.get("taunt") != null) {
                    predicates.add(cb.equal(root.get("taunt"), searchCriteria.get("taunt")));
                }
                if (searchCriteria.get("draw") != null) {
                    predicates.add(cb.equal(root.get("draw"), searchCriteria.get("draw")));
                }
                if (searchCriteria.get("revive") != null) {
                    predicates.add(cb.equal(root.get("revive"), searchCriteria.get("revive")));
                }

                return cb.and(predicates.toArray(new Predicate[] {}));
            }
        };
    }
}
