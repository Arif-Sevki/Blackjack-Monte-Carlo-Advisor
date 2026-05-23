package Ui;

import Game.BlackjackGame;
import Game.BlackjackGame.GameState;
import Game.HintSystem;
import Game.MonteCarloAdvisor;
import Game.Rules.Result;
import Model.Card;
import Model.Dealer;
import Model.Hand;
import Model.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleUi {

    // ─── Card dimensions
    private static final int CARD_WIDTH = 9; // inner width (excluding border pipes)
    private static final int CARD_HEIGHT = 7; // total rows per card including borders

    private final BlackjackGame game;
    private final Scanner scanner;

    public ConsoleUi(BlackjackGame game) {
        this.game = game;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        showWelcome();

        while (game.getState() != GameState.GAME_OVER) {

            getBet();

            showHands(false);

            // 3. Player action loop
            while (game.getState() == GameState.PLAYER_TURN) {
                getPlayerAction();
                if (game.getState() == GameState.PLAYER_TURN) {
                    showHands(false);
                }
            }

            // 4. Show final hands
            if (game.getState() == GameState.PAYOUT ||
                    game.getState() == GameState.BETTING ||
                    game.getState() == GameState.GAME_OVER) {
                showHands(true);
                showResult();
            }
        }

        showGameOver();
        scanner.close();
    }

    // DISPLAY METHODS
    /**
     * Prints both hands side-by-side
     *
     * @param revealDealer true → show all dealer cards
     *                     false → hide dealer's first card (hole card)
     */
    public void showHands(boolean revealDealer) {
        Player player = game.getPlayer();
        Dealer dealer = game.getDealer();

        System.out.println();
        System.out.println("┌─────────────────────────────────────────┐");

        // Dealer line
        int dealerTotal = revealDealer
                ? dealer.getHand().getTotal()
                : dealer.getVisibleTotal();

        String dealerHeader = revealDealer
                ? "  DEALER  (total: " + dealerTotal + ")"
                : "  DEALER  (showing: " + dealerTotal + ")";
        System.out.println(dealerHeader);

        printHandHorizontal(dealer.getHand(), !revealDealer);

        System.out.println();

        // Player line
        System.out.println("  " + player.getName() +
                "  (total: " + player.getHand().getTotal() +
                "  |  balance: $" + player.getBalance() + ")");

        printHandHorizontal(player.getHand(), false);

        System.out.println("└─────────────────────────────────────────┘");
        System.out.println();
    }

    public void showWelcome() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║      ♠  BLACKJACK  ♥             ║");
        System.out.println("║      ♦  Welcome!  ♣             ║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println();
    }

    public void showGameOver() {
        System.out.println("╔══════════════════════════════════╗");
        System.out.println("║        GAME OVER — Broke!        ║");
        System.out.println("╚══════════════════════════════════╝");
    }

    public void showResult() {
        Result result = game.getLastResult();
        if (result == null)
            return;

        String msg;
        switch (result) {
            case PLAYER_BLACKJACK:
                msg = "🃏  BLACKJACK! You win 3:1!";
                break;
            case PLAYER_WIN:
                msg = "✔  You win!";
                break;
            case DEALER_BUST:
                msg = "✔  Dealer busted — you win!";
                break;
            case DEALER_BLACKJACK:
                msg = "✘  Dealer has Blackjack — you lose.";
                break;
            case DEALER_WIN:
                msg = "✘  Dealer wins.";
                break;
            case PLAYER_BUST:
                msg = "✘  Bust! You lose.";
                break;
            case PUSH:
                msg = "↔  Push — bet returned.";
                break;
            default:
                msg = "Round over.";
        }

        System.out.println("  >> " + msg);
        System.out.println("  Balance: $" + game.getPlayer().getBalance());
        System.out.println();
    }

    // INPUT METHODS
    public void getBet() {
        Player player = game.getPlayer();
        System.out.println("Your balance: $" + player.getBalance());
        System.out.print("Place your bet: $");

        while (true) {
            String input = scanner.nextLine().trim();
            try {
                int amount = Integer.parseInt(input);
                if (amount <= 0) {
                    System.out.print("Bet must be positive. Try again: $");
                } else if (amount > player.getBalance()) {
                    System.out.print("Not enough balance ($" + player.getBalance() +
                            "). Try again: $");
                } else {
                    game.placeBet(amount);
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Enter a number: $");
            }
        }
    }

    public void getPlayerAction() {
        boolean canDouble = game.getPlayer().getHand().getSize() == 2
                && game.getPlayer().getBalance() >= game.getPlayer().getCurrentBet();

        System.out.println("  Actions:  [H] Hit    [S] Stand" +
                (canDouble ? "    [D] Double Down" : "") +
                "    [?] Hint (" + game.getHintSystem().getRemainingHints() + " left)");
        System.out.print("  Your choice: ");

        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();
            switch (input) {
                case "H":
                    game.playerHit();
                    return;
                case "S":
                    game.playerStand();
                    return;
                case "D":
                    if (canDouble) {
                        game.playerDoubleDown();
                        return;
                    }
                    System.out.print("  Double Down not available. Choose H or S: ");
                    break;
                case "?":
                    showHint();
                    break;
                default:
                    System.out.print("  Invalid. Enter H, S" + (canDouble ? " or D" : "") + ": ");
            }
        }
    }

    private void showHint() {
        HintSystem hints = game.getHintSystem();

        boolean canDouble = game.getPlayer().getHand().getSize() == 2
                && game.getPlayer().getBalance() >= game.getPlayer().getCurrentBet();

        Card dealerUpCard = game.getDealer().getHand().getCards().get(0);

        MonteCarloAdvisor.Advice advice = hints.requestHint(
                game.getPlayer().getHand(),
                dealerUpCard,
                game.getDeck(),
                canDouble);

        if (advice == null) {
            System.out.println("  ✘  No hints remaining!");
            return;
        }

        // print
        System.out.println();
        System.out.println("  ┌─── HINT (" + hints.getRemainingHints() + " remaining) ───────────────┐");
        System.out.printf("  │  Stand:  %5.1f%%                          │%n", advice.standWinRate);
        System.out.printf("  │  Hit:    %5.1f%%                          │%n", advice.hitWinRate);
        if (advice.doubleWinRate >= 0) {
            System.out.printf("  │  Double: %5.1f%%                          │%n", advice.doubleWinRate);
        }
        System.out.println("  │  ➤  Best action: " + advice.bestAction);
        System.out.println("  └──────────────────────────────────────────┘");
        System.out.println();
    }

    // ASCII CARD RENDERING
    /**
     * Prints all cards in a hand side-by-side.
     *
     * @param hand          the hand to render
     * @param hideFirstCard if true the first card is drawn face-down
     */
    private void printHandHorizontal(Hand hand, boolean hideFirstCard) {
        List<Card> cards = hand.getCards();
        if (cards.isEmpty())
            return;

        // Build a 2-D array: rows[cardIndex][rowIndex] = one text line
        List<String[]> cardRows = new ArrayList<>();
        for (int i = 0; i < cards.size(); i++) {
            boolean hidden = (i == 0) && hideFirstCard;
            cardRows.add(hidden ? buildHiddenCard() : buildCard(cards.get(i)));
        }

        // Print row by row across all cards
        for (int row = 0; row < CARD_HEIGHT; row++) {
            System.out.print("  ");
            for (String[] card : cardRows) {
                System.out.print(card[row]);
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    /**
     * Builds a 7-row String array representing one face-up card.
     *
     * .--------.
     * |A |
     * | ♠ |
     * | |
     * | ♠ |
     * | A|
     * '--------'
     */
    public String[] buildCard(Card card) {
        String rank = rankLabel(card.getRank());
        String suit = suitSymbol(card.getSuit());

        // left-aligned rank+suit, right-aligned rank+suit
        // rank can be 1 or 2 chars → pad accordingly
        String topLeft = rank + suit; // e.g. "A♠" or "10♠"
        String botRight = suit + rank;

        // inner content width = CARD_WIDTH (9 chars between the pipes)
        String topLine = padRight(topLeft, CARD_WIDTH);
        String botLine = padLeft(botRight, CARD_WIDTH);

        String border = "." + repeat("-", CARD_WIDTH) + ".";
        String emptyRow = "|" + repeat(" ", CARD_WIDTH) + "|";
        String suitRow = "|" + center(suit, CARD_WIDTH) + "|";

        return new String[] {
                border,
                "|" + topLine + "|",
                emptyRow,
                suitRow,
                emptyRow,
                "|" + botLine + "|",
                "'" + repeat("-", CARD_WIDTH) + "'"
        };
    }

    /**
     * Builds a 7-row String array for a face-down (hidden) card.
     *
     * .--------.
     * |########|
     * |########|
     * |########|
     * |########|
     * |########|
     * '--------'
     */
    private String[] buildHiddenCard() {
        String border = "." + repeat("-", CARD_WIDTH) + ".";
        String fillRow = "|" + repeat("#", CARD_WIDTH) + "|";
        String bottom = "'" + repeat("-", CARD_WIDTH) + "'";

        return new String[] {
                border,
                fillRow,
                fillRow,
                fillRow,
                fillRow,
                fillRow,
                bottom
        };
    }

    // HELPERS
    private String rankLabel(Card.Rank rank) {
        switch (rank) {
            case A:
                return "A";
            case Two:
                return "2";
            case Three:
                return "3";
            case Four:
                return "4";
            case Five:
                return "5";
            case Six:
                return "6";
            case Seven:
                return "7";
            case Eight:
                return "8";
            case Nine:
                return "9";
            case Ten:
                return "10";
            case J:
                return "J";
            case Q:
                return "Q";
            case K:
                return "K";
            default:
                return "?";
        }
    }

    private String suitSymbol(Card.Suit suit) {
        switch (suit) {
            case Spades:
                return "♠";
            case Hearts:
                return "♥";
            case Diamonds:
                return "♦";
            case Clubs:
                return "♣";
            default:
                return "?";
        }
    }

    // Pad string to width, aligning content to the left.
    private String padRight(String s, int width) {
        if (s.length() >= width)
            return s;
        return s + repeat(" ", width - s.length());
    }

    // Pad string to width, aligning content to the right.
    private String padLeft(String s, int width) {
        if (s.length() >= width)
            return s;
        return repeat(" ", width - s.length()) + s;
    }

    // Centre a string within a fixed width.
    private String center(String s, int width) {
        int total = width - s.length();
        if (total <= 0)
            return s;
        int left = total / 2;
        int right = total - left;
        return repeat(" ", left) + s + repeat(" ", right);
    }

    private String repeat(String ch, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++)
            sb.append(ch);
        return sb.toString();
    }
}