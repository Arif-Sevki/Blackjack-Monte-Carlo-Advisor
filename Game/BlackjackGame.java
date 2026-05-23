package Game;

import Game.Rules.Result;
import Model.Dealer;
import Model.Deck;
import Model.Player;

public class BlackjackGame {

    public enum GameState {
        BETTING, DEALING, PLAYER_TURN, DEALER_TURN, PAYOUT, GAME_OVER
    }

    private final HintSystem hintSystem;
    private Deck deck;
    private Player player;
    private Dealer dealer;
    private GameState state;
    private Result lastResult;

    public BlackjackGame(String playerName, int startingBalance) {
        this.deck = new Deck();
        this.player = new Player(playerName, startingBalance);
        this.dealer = new Dealer();
        this.hintSystem = new HintSystem();
        this.state = GameState.BETTING;
    }

    public void startRound() {
        if (state != GameState.BETTING) {
            throw new IllegalArgumentException("State should be BETTING");
        }
        player.resetHand();
        dealer.resetHand();

        if (deck.needReshuffle() || deck.isEmpty()) {
            deck.reset();
        }

        state = GameState.DEALING;
    }

    public void deal() {
        // Add two card each with order
        for (int i = 0; i < 2; i++) {
            player.getHand().addCard(deck.deal());
            dealer.getHand().addCard(deck.deal());
        }
        // if there is Blackjack move into payout
        if (player.getHand().hasBlackjack() || dealer.getHand().hasBlackjack()) {
            dealer.revealHoleCard();
            state = GameState.PAYOUT;
            payout();
        } else {
            state = GameState.PLAYER_TURN;
        }
    }

    public void playerHit() {
        if (state != GameState.PLAYER_TURN) {
            throw new IllegalArgumentException("State should be PLAYER_TURN");
        }
        // Add card to player
        player.getHand().addCard(deck.deal());
        // Is Busted
        if (player.getHand().isBusted()) {
            dealer.revealHoleCard();
            state = GameState.PAYOUT;
            payout();
        }
    }

    public void playerStand() {
        if (state != GameState.PLAYER_TURN) {
            throw new IllegalArgumentException("State should be PLAYER_TURN");
        }
        dealer.revealHoleCard();
        state = GameState.DEALER_TURN;
        dealerPlay();
    }

    public void playerDoubleDown() {
        if (state != GameState.PLAYER_TURN) {
            throw new IllegalArgumentException("State should be PLAYER_TURN");
        }
        if (player.getHand().getSize() > 2) {
            throw new IllegalArgumentException("Can be used only in first two cards.");
        }
        // Double the bet, draw a card than state
        player.doubleDown();
        player.getHand().addCard(deck.deal());
        playerStand();
    }

    public void dealerPlay() {
        if (state != GameState.DEALER_TURN) {
            throw new IllegalArgumentException("State should be DEALER_TURN");
        }
        while (dealer.shouldHit()) {
            dealer.getHand().addCard(deck.deal());
        }
        state = GameState.PAYOUT;
        payout();
    }

    public void payout() {
        lastResult = Rules.determineWinner(player, dealer);

        if (lastResult == Result.PLAYER_BLACKJACK) {
            player.winBlackJack();
        } else if (lastResult == Result.PLAYER_WIN || lastResult == Result.DEALER_BUST) {
            player.winBet();
        } else if (lastResult == Result.DEALER_WIN || lastResult == Result.PLAYER_BUST
                || lastResult == Result.DEALER_BLACKJACK) {
            player.loseBet();
        } else {
            player.pushBet();
        }

        if (player.isBroke()) {
            state = GameState.GAME_OVER;
        } else {
            state = GameState.BETTING;
        }

    }

    public void placeBet(int amount) {
        if (state != GameState.BETTING) {
            throw new IllegalArgumentException("State should be BETTING");
        }
        player.placeBet(amount);
        startRound();
        deal();
    }

    public GameState getState() {
        return state;
    }

    public Player getPlayer() {
        return player;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public Result getLastResult() {
        return lastResult;
    }

    public HintSystem getHintSystem() {
        return hintSystem;
    }

    public Deck getDeck() {
        return deck;
    }
}