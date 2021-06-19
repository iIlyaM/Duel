package ru.vsu.cs;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class AI extends Player {
    private List<Card> playerDeck; // оставшиеся карты противника
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
        this.prevDroppedCard = null; //todo будет null на первом ходу
    }

    public void rememberRemainingCards(Card droppedCard) {
        predicts.clear();
        int index = playerDeck.indexOf(droppedCard);
        playerDeck.remove(droppedCard);
        prevDroppedCard = droppedCard;
        if(prevDroppedCard != null) {
            fillPredicts(index);
        }
    }

    private void fillPredicts(int index) {
        if (index == 0) {
            for (int i = 0; i < playerDeck.size(); i++) {
                predicts.add(playerDeck.get(i));
            }
        } else if(index == predicts.size() - 1) {
            for (int i = predicts.size() - 1; i > 0; i++) {
                predicts.add(playerDeck.get(i));
            }
        } else {
            for (int i = index - 1; i < index + 1; i++) {
                predicts.add(playerDeck.get(i));
            }
        }
    }
    //todo Поиграть со значениями, взять средней значение;
    public int makeTurn(boolean defence) { //todo А не сделать ли return Card?
        switch (this.tactics) {
            case RANDOM :
                Random rnd = new Random();
                int droppedIndex = rnd.nextInt(this.getDeck().size());
                int cardNumber = this.getDeck().get(droppedIndex).getAmount();
                this.getDeck().remove(droppedIndex);
                return cardNumber;
            case AGGRESSIVE:
                if(defence) {
                    return predicts.get(0).getAmount();
                } else {
                    return predicts.get(1).getAmount();
                }
            case DEFENCIVE:
                if(defence) {
                    return predicts.get(1).getAmount();
                } else {
                    return predicts.get(0).getAmount();
                }
        }
        return -1;
    }
}
