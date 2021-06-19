package ru.vsu.cs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameService {

    public static Player spawnPlayer(int deckSize) {
        List<Card> newDeck = new ArrayList<>();
        for(int i = 0; i < deckSize; i++) {
            newDeck.add(new Card(i));
        }

        return new Player(newDeck);
    }

    public static int searchCard(List<Card> deck, int value) {
        for(int i = 0; i < deck.size(); i++) {
            if(deck.get(i).getAmount() == value) {
                return i;
            }
        }
        return -1;
    }

    public static Card dropCard(Player player , int cardValue) {
        return player.getDeck().remove(searchCard(player.getDeck(), cardValue));
    }

    public static void addPenalty(Player player, int difference) {
        player.setPenalty(player.getPenalty() + difference);
    }

    public static int calcPenalty(int attack, int defence) {
        if(attack > defence) {
            return attack - defence;
        } else {
            return 0;
        }
    }

    public static boolean turnFirstAttacker() {
        Random newRnd = new Random();
        return newRnd.nextBoolean();
    }

    public static void startGame(int amount) {
        Random rnd = new Random();
        Scanner sc = new Scanner(System.in);
        System.out.printf("Amount of cards : %f", amount);
        Player player = spawnPlayer(amount);
        boolean isPlayerTurn = turnFirstAttacker();
        Player ai = spawnPlayer(amount);

        for (int i = amount; i > 0; i--) {
            if(isPlayerTurn) {

            }
        }
    }
}
