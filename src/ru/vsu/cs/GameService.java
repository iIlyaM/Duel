package ru.vsu.cs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class GameService {

    public static Player spawnPlayer(int deckSize) {
        List<Card> newDeck = new ArrayList<>();
        for (int i = 0; i < deckSize; i++) {
            newDeck.add(new Card(i));
        }

        return new Player(newDeck);
    }

    public static Player spawnAI(int deckSize, String botType) {
        List<Card> newDeck = new ArrayList<>();
        for (int i = 0; i < deckSize; i++) {
            newDeck.add(new Card(i));
        }

        return new AI(newDeck, botType);
    }

    public static int searchCard(List<Card> deck, int value) {
        for (int i = 0; i < deck.size(); i++) {
            if (deck.get(i).getAmount() == value) {
                return i;
            }
        }
        return -1;
    }

    public static Card dropCard(Player player, int cardValue) {
        return player.getDeck().remove(searchCard(player.getDeck(), cardValue));
    }

    public static void addPenalty(Player player, int difference) {
        player.setPenalty(player.getPenalty() + difference);
    }

    public static int calcPenalty(int attack, int defence) {
        if (attack > defence) {
            return attack - defence;
        } else {
            return 0;
        }
    }

    public static boolean turnFirstAttacker() {
        Random newRnd = new Random();
        return newRnd.nextBoolean();
    }

    private static boolean switchAttacker(boolean b) {
        if (b) {
            return false;
        } else {
            return true;
        }
    }


    public static Card consoleCardDrop(Scanner scn, Player player){
        int cardNum;
        boolean isFound = false;

        while (!isFound) {
            System.out.print("Введите карточку: ");
            cardNum = scn.nextInt();
            int index = searchCard(player.getDeck(), cardNum);

            if(index != -1) {
                return dropCard(player, cardNum);
            }

            System.out.println("Такой карты не существует! Повторите ввод!");
        }

        return null;
    }


    public static void startGame(int amount) {
        Scanner sc = new Scanner(System.in);


        Player player = spawnPlayer(amount);
        AI ai = (AI) spawnAI(amount, readBotType(sc));

        boolean isPlayerTurn = turnFirstAttacker();

        int playerCardValue;
        int botCardValue;
        Card droppedCard;

        for (int i = amount; i > 0; i--) {
//            System.out.println("\n\nКоличество штрафных очков: ");
//            System.out.println(ConsoleUtils.YELLOW + "Игрок: " + player.getPenalty() + ConsoleUtils.RESET);
//            System.out.println(ConsoleUtils.PURPLE + "Бот: " + ai.getPenalty() + ConsoleUtils.RESET);
            printPenaltyStats(player, ai);

              printRemainingCards(player);
//            System.out.print("Карты у вас на руках: | ");
//            for (int j = 0; j < player.getDeck().size(); j++) {
//                System.out.print(ConsoleUtils.GREEN + player.getDeck().get(j).getAmount() + ConsoleUtils.RESET + " | ");
//            }
//            System.out.print('\n');


            if (isPlayerTurn) {
                //toDO выполняется логика хода бота, его дамаг/защита записываются в отдельную переменную. В теории можно считать в отдельном методе
                //под логикой я подразумеваю обновление инфы о колоде игрока в "голове" у бота и предикты

                printCurrentTurn(isPlayerTurn);
//                System.out.print(ConsoleUtils.RED + "Вы сейчас атакуете. " + ConsoleUtils.RESET);
                droppedCard = consoleCardDrop(sc, player);
                playerCardValue = droppedCard.getAmount();


                botCardValue = ai.makeTurn(isPlayerTurn);
                ai.rememberRemainingCards(droppedCard, player.getDeck());

                addPenalty(ai, calcPenalty(playerCardValue, botCardValue));

//                System.out.print("Вы атаковали на " + playerCardValue + "\nБот защитился на " + botCardValue);
//                System.out.print('\n');
                printAttackResults(playerCardValue, botCardValue, isPlayerTurn);
            } else {
                //toDO выполняется логика хода бота, его дамаг/защита записываются в отдельную переменную. В теории можно считать в отдельном методе


                  printCurrentTurn(isPlayerTurn);
//                System.out.print(ConsoleUtils.BLUE + "Вы сейчас защищаетесь. " + ConsoleUtils.RESET);
                droppedCard = consoleCardDrop(sc, player);
                playerCardValue = droppedCard.getAmount();

                botCardValue = ai.makeTurn(isPlayerTurn);
                ai.rememberRemainingCards(droppedCard, player.getDeck());

                addPenalty(player, calcPenalty(botCardValue, playerCardValue));
//                System.out.print("Вы защитились на " + playerCardValue + "\nБот атаковал на " + botCardValue);
//                System.out.print('\n');
                printAttackResults(playerCardValue, botCardValue, isPlayerTurn);
            }

            isPlayerTurn = switchAttacker(isPlayerTurn);
        }
       printResults(player, ai);
    }

    private static void printResults(Player player, AI ai) {
        System.out.println("\n\n\n\n\n\n");
        if(player.getPenalty() > ai.getPenalty())
            System.out.println(ConsoleUtils.RED + "Вы проиграли!");
        else if (player.getPenalty() < ai.getPenalty())
            System.out.println(ConsoleUtils.CYAN + "Вы выиграли!");
        else
            System.out.println(ConsoleUtils.WHITE + "Ничья!");

        System.out.println("Кол-во штрафных очков за игру: \n" + ConsoleUtils.YELLOW + "Игрок: " + player.getPenalty()
                + ConsoleUtils.PURPLE + "\nБот: " + ai.getPenalty() + ConsoleUtils.RESET);
    }

    private static void printAttackResults(int playerCardValue, int botCardValue, boolean isPlayerTurn) {
        if(isPlayerTurn) {
            System.out.print("Вы атаковали на " + playerCardValue + "\nБот защитился на " + botCardValue);
            System.out.print('\n');
        }
        if(!isPlayerTurn) {
            System.out.print("Вы защитились на " + playerCardValue + "\nБот атаковал на " + botCardValue);
            System.out.print('\n');
        }
    }

    private static void printPenaltyStats(Player player, AI ai) {
        System.out.println("\n\nКоличество штрафных очков: ");
        System.out.println(ConsoleUtils.YELLOW + "Игрок: " + player.getPenalty() + ConsoleUtils.RESET);
        System.out.println(ConsoleUtils.PURPLE + "Бот: " + ai.getPenalty() + ConsoleUtils.RESET);
    }

    private  static void printSelectedBotType(String botType) {
        System.out.println("Выбранный тип бота - " + botType);
    }

    private static void printCurrentTurn(boolean isPlayerTurn) {
        if(isPlayerTurn) {
            System.out.print(ConsoleUtils.RED + "Вы сейчас атакуете. " + ConsoleUtils.RESET);
        }
        if(!isPlayerTurn) {
            System.out.print(ConsoleUtils.BLUE + "Вы сейчас защищаетесь. " + ConsoleUtils.RESET);
        }
    }

    private static void printRemainingCards(Player player) {
        System.out.print("Карты у вас на руках: | ");
        for (int j = 0; j < player.getDeck().size(); j++) {
            System.out.print(ConsoleUtils.GREEN + player.getDeck().get(j).getAmount() + ConsoleUtils.RESET + " | ");
        }
        System.out.print('\n');
    }

    private static String readBotType(Scanner sc) {
        System.out.println("Тип бота : " + ConsoleUtils.RED + "\n 1 - Атакующий \n" +
              ConsoleUtils.YELLOW + " 2 - Обороняющийся \n По умолчанию - Random " +
                "\n Выберете тип бота : ");
        return sc.nextLine();
    }
}
