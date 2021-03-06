package ru.vsu.cs.user;

import java.util.List;

public class Player {
    private List<Card> deck;
    private int penalty;

    public Player(List<Card> deck) {
        this.deck = deck;
        this.penalty = 0;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public int getPenalty() {
        return penalty;
    }

    public List<Card> getDeck() {
        return deck;
    }
}
