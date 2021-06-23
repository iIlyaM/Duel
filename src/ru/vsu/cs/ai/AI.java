package ru.vsu.cs.ai;

import ru.vsu.cs.user.Card;
import ru.vsu.cs.user.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AI extends Player {
    private List<Card> playerDeck;
    private List<Card> predicts;
    private Card prevDroppedCard;
    private Tactics tactics;

    public AI(List<Card> deck, String tactics) {
        super(deck);
        predicts = new LinkedList<>();
        switch(tactics) {
            case "" :
                this.tactics = Tactics.RANDOM;
                break;
            case "1" :
                this.tactics = Tactics.AGGRESSIVE;
                break;
            case "2" :
                this.tactics = Tactics.DEFENCIVE;
                break;
        }
        this.playerDeck = List.copyOf(deck);
        this.prevDroppedCard = null;
    }

    public void rememberRemainingCards(Card droppedCard, List<Card> playerDeck) {
        predicts.clear();
        int index = search(droppedCard.getAmount(), playerDeck);
        prevDroppedCard = droppedCard;
        if(prevDroppedCard != null) {
            fillPredicts(index, this.playerDeck);
        }
    }

    private void fillPredicts(int index, List<Card> playerDeck) {
        if (index == 0) {
            for (int i = 0; i < playerDeck.size(); i++) {
                predicts.add(playerDeck.get(i));
            }
        } else if(index == predicts.size() - 1) {
            for (int i = predicts.size() - 1; i > 0; i++) {
                predicts.add(this.playerDeck.get(i));
            }
        } else {
            for (int i = index - 1; i < index + 1; i++) {
                predicts.add(this.playerDeck.get(i));
            }
        }
    }

    private int search(int value, List<Card> playerDeck) {
        for (int i = 0; i < this.playerDeck.size(); i++) {
            if(this.playerDeck.get(i).getAmount() == value) {
                return i;
            }
        }
        return -1;
    }

    public int makeTurn(boolean defence) {
        Random rnd = new Random();
        switch (this.tactics) {
            case RANDOM :
                int droppedIndex = rnd.nextInt(this.getDeck().size());
                int cardNumber = this.getDeck().get(droppedIndex).getAmount();
                this.getDeck().remove(droppedIndex);
                return cardNumber;
            case AGGRESSIVE:
                if(predicts.size() == 0) {
                    return rnd.nextInt(this.getDeck().size());
                }
                if(defence) {
                    return predicts.get(0).getAmount();
                } else {
                    return predicts.get(1).getAmount();
                }
            case DEFENCIVE:
                if(predicts.size() == 0) {
                    return rnd.nextInt(this.getDeck().size());
                }
                if(defence) {
                    return predicts.get(1).getAmount();
                } else {
                    return predicts.get(0).getAmount();
                }
        }
        return -1;
    }
}
