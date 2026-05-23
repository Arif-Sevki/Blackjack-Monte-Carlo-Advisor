package Model;

public class Dealer {
    
    private Hand hand;
    private boolean holeCardHidden;

    public Dealer (){
        this.hand = new Hand();
        this.holeCardHidden = true;
    }

    public Hand getHand(){
        return hand;
    }

    public boolean shouldHit(){
        //If hand less than 17 or it is 17 and hand is soft than hit.
        return (hand.getTotal() < 17) || (hand.getTotal() == 17 && hand.isSoft());
    }

    public void revealHoleCard (){
        holeCardHidden = false;
    }

    public boolean isHoldCardHidden(){
        return holeCardHidden;
    }

    public int  getVisibleTotal(){
        // Only get the value of first card
        return hand.getCards().get(0).getValue();
    }

    public void resetHand(){
        hand.clear();
        holeCardHidden = true;
    }
    
}
