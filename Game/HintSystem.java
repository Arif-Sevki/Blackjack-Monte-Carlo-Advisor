package Game;

import Game.MonteCarloAdvisor.Advice;
import Model.Card;
import Model.Deck;
import Model.Hand;

public class HintSystem {

    public static final int MAX_HINTS = 3;
    private int hintsRemaining;

    public HintSystem() {
        this.hintsRemaining = MAX_HINTS;
    }

    public Advice requestHint(Hand playerHand,Card dealerUpCard,Deck remainingDeck,boolean canDouble) {

        if(hintsRemaining<=0){
            return null;
        }
        hintsRemaining--;

        return MonteCarloAdvisor.analyze(playerHand, remainingDeck, canDouble, dealerUpCard);
    }

    public int getRemainingHints(){
        return hintsRemaining;
    }

    public boolean hasHints(){
        return hintsRemaining>0;
    }

    
}
