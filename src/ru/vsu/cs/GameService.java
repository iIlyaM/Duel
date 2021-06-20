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
        String botType;
        int playerCardValue;
        int botCardValue;
        Card droppedCard;
        boolean isGameContinue = true;

        while(isGameContinue) {
        Scanner sc = new Scanner(System.in);

        Player player = spawnPlayer(amount);
        botType = readBotType(sc);
        AI ai = (AI) spawnAI(amount, botType);

        printSelectedBotType(botType);

        boolean isPlayerTurn = turnFirstAttacker();

            for (int i = amount; i > 0; i--) {

                printPenaltyStats(player, ai);
                printRemainingCards(player);

                if (isPlayerTurn) {
                    printCurrentTurn(isPlayerTurn);
                    droppedCard = consoleCardDrop(sc, player);
                    playerCardValue = droppedCard.getAmount();

                    botCardValue = ai.makeTurn(isPlayerTurn);
                    ai.rememberRemainingCards(droppedCard, player.getDeck());

                    addPenalty(ai, calcPenalty(playerCardValue, botCardValue));

                    printAttackResults(playerCardValue, botCardValue, isPlayerTurn);
                } else {
                    printCurrentTurn(isPlayerTurn);

                    droppedCard = consoleCardDrop(sc, player);
                    playerCardValue = droppedCard.getAmount();

                    botCardValue = ai.makeTurn(isPlayerTurn);
                    ai.rememberRemainingCards(droppedCard, player.getDeck());
                    addPenalty(player, calcPenalty(botCardValue, playerCardValue));

                    printAttackResults(playerCardValue, botCardValue, isPlayerTurn);
                }

                isPlayerTurn = switchAttacker(isPlayerTurn);
            }
            printResults(player, ai);
            isGameContinue = checkAnswer(askReplay());
        }
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
        if(botType.equals("1")) {
            System.out.println("Выбранный тип бота - " + ConsoleUtils.RED + "AGGRESSIVE" + ConsoleUtils.RESET);
        } else if(botType.equals("2")) {
            System.out.println("Выбранный тип бота - " + ConsoleUtils.BLUE + "DEFENCIVE" + ConsoleUtils.RESET);
        } else {
            System.out.println("Выбранный тип бота - " + ConsoleUtils.YELLOW + "RANDOM" + ConsoleUtils.RESET);
        }
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
        System.out.println(ConsoleUtils.RESET + "Тип бота : " + ConsoleUtils.RED + "\n 1 - Атакующий \n" +
              ConsoleUtils.BLUE + " 2 - Обороняющийся\n" + ConsoleUtils.YELLOW + "Enter - Random " +
                ConsoleUtils.RESET + "\n Выберете тип бота : ");
        return sc.nextLine();
    }

    private static String askReplay() {
        Scanner scan = new Scanner(System.in);
        System.out.println(ConsoleUtils.RESET + "Хотите сыграть ещё раз? \n"
                + ConsoleUtils.GREEN + "y - Да, ещё бы! \n" + ConsoleUtils.RED +
                "n - Нет, воздержусь..." + ConsoleUtils.RESET +"\n Ваш выбор : " );
        return scan.nextLine();
    }

    private static boolean checkAnswer(String answer) {
        if(answer.equalsIgnoreCase("y")) {
            return true;
        }
        if(answer.equalsIgnoreCase("n")) {
            return false;
        }
        return false;
    }
}
