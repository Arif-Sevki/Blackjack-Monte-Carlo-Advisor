package Game;

import Model.Card;
import Model.Deck;
import Model.Hand;

public class MonteCarloAdvisor {
    public static final int SIMULATIONS = 10_000;

    public static class Advice {
        public double hitWinRate;
        public double standWinRate;
        public double doubleWinRate;
        public String bestAction;

        public Advice(double hitWinRate, double standWinRate, double doubleWinRate) {

            this.hitWinRate = hitWinRate;
            this.standWinRate = standWinRate;
            this.doubleWinRate = doubleWinRate;
            // If double down win rate is biggest and bigger than 0
            if (doubleWinRate >= 0 && doubleWinRate > standWinRate && doubleWinRate > hitWinRate) {
                bestAction = "DOUBLE DOWN";
            } else if (hitWinRate > standWinRate) {
                bestAction = "HIT";
            } else {
                bestAction = "STAND";
            }
        }
    }

    public static Advice analyze(Hand playerHand, Deck remainingDeck, boolean canDouble,
            Card dealerUpCard) {

        double hitWins = 0, standWins = 0, doubleWins = 0;

        for (int i = 0; i < SIMULATIONS; i++) {
            // For stand create a deck and copy remaningDeck than shuffle and simulate
            Deck deckStand = new Deck(remainingDeck);
            deckStand.shuffle();
            if (simulateStand(playerHand.getTotal(), dealerUpCard, deckStand) > 0) {
                standWins++;
            }

            // For hit create a deck and copy remaningDeck than shuffle and simulate
            Deck deckHit = new Deck(remainingDeck);
            deckHit.shuffle();
            if (simulateHit(playerHand, dealerUpCard, deckHit) > 0) {
                hitWins++;
            }

            if (canDouble) {
                Deck deckDouble = new Deck(remainingDeck);
                deckDouble.shuffle();
                if (simulateDouble(playerHand, dealerUpCard, deckDouble) > 0) {
                    doubleWins++;
                }
            }
        }

        double standRate = (double) standWins / SIMULATIONS * 100.0;
        double hitRate = (double) hitWins / SIMULATIONS * 100.0;
        double doubleRate = canDouble ? (double) doubleWins / SIMULATIONS * 100.0 : -1.0;

        return new Advice(hitRate, standRate, doubleRate);
    }

    private static int simulateStand(int playerTotal, Card dealerUpCard, Deck deck) {
        // return (1) Player Win, (0) Tie, (-1) Player Lost,

        if (playerTotal > 21) {
            return -1;
        }
        Hand dealerHand = new Hand();
        dealerHand.addCard(dealerUpCard);
        dealerHand.addCard(deck.deal());

        while (shouldDealerHit(dealerHand)) {
            dealerHand.addCard(deck.deal());
        }

        int dealerTotal = dealerHand.getTotal();
        if (dealerTotal > 21 || playerTotal > dealerTotal) {
            return 1;
        }
        if (playerTotal == dealerTotal) {
            return 0;
        }

        else {
            return -1;
        }
    }

    private static int simulateHit(Hand playerHand, Card dealerUpCard, Deck deck) {
        // Create a temporary hand for the simulation
        Hand simHand = new Hand();
        for (Card c : playerHand.getCards()) {
            simHand.addCard(c);
        }

        simHand.addCard(deck.deal());
        int total = simHand.getTotal(); // This accurately handles real Aces!

        if (total > 21) {
            return -1;
        }
        return simulateStand(total, dealerUpCard, deck);
    }

    private static int simulateDouble(Hand playerHand, Card dealerUpCard, Deck deck) {
        // Double down is just drawing one card and then standing
        Hand simHand = new Hand();
        for (Card c : playerHand.getCards()) {
            simHand.addCard(c);
        }

        simHand.addCard(deck.deal());
        int total = simHand.getTotal();

        if (total > 21) {
            return -1;
        }
        return simulateStand(total, dealerUpCard, deck);
    }

    private static boolean shouldDealerHit(Hand hand) {
        // If hand less than 17 or it is 17 and hand is soft than hit.
        return (hand.getTotal() < 17) || (hand.getTotal() == 17 && hand.isSoft());
    }

    private static int calcTotal(int currentTotal, Card newCard) {
        int cardValue = newCard.getValue(); // Ace returns 1
        int total = currentTotal + cardValue;

        // If the new card is an Ace and treating it as 11 doesn't bust, use 11
        if (newCard.getRank() == Card.Rank.A && total + 10 <= 21) {
            total += 10;
        }
        // If we busted but we have a soft ace, convert it
        else if (total > 21 && total - 10 >= 1) {
            total -= 10;
        }

        return total;
    }
}
