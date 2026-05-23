package Game;

import Model.Dealer;
import Model.Player;

public class Rules {

    public enum Result {
        PLAYER_BLACKJACK, PLAYER_WIN, DEALER_BUST, // Player win
        DEALER_BLACKJACK, DEALER_WIN, PLAYER_BUST, // Dealer win
        PUSH // Tie
    }
    
    public static Result determineWinner(Player player, Dealer dealer){
        Result result;

        //Player busted
        if(player.getHand().isBusted()){
            result = Result.PLAYER_BUST;
        }
        //Dealer busted
        else if (dealer.getHand().isBusted()){
            result = Result.DEALER_BUST;
        }
        //Both blackjack 
        else if (player.getHand().hasBlackjack() && dealer.getHand().hasBlackjack()){
            result = Result.PUSH;
        }
        //Only player blackjack
        else if (player.getHand().hasBlackjack()){
            result = Result.PLAYER_BLACKJACK;
        }
        //Only dealer blackjack
        else if (dealer.getHand().hasBlackjack()){
            result = Result.DEALER_BLACKJACK;
        }
        //Normal player win
        else if (player.getHand().getTotal() > dealer.getHand().getTotal()){
            result = Result.PLAYER_WIN;
        }
        //Dealer normal win
        else if (dealer.getHand().getTotal() > player.getHand().getTotal()){
            result = Result.DEALER_WIN;
        }
        //Tie
        else{
            result = Result.PUSH;
        }
         
        return result;
    }
}
