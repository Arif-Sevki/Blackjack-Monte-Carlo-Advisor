package Model;
import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> cards;

    public Hand(){
        this.cards=new ArrayList<>();
    }

    public void addCard(Card card){
        cards.add(card);
    }

    public int getTotal(){
        int total=0;
        int aces=0;

        for (Card card : cards) {
            total=total+card.getValue();
            if (card.getRank()== Card.Rank.A){
                aces++;
            }
        }

        if( aces>0 && total+10 <= 21){
            total = total+10;
        }
        

        return total;
    }

    public boolean isBusted(){
        return getTotal()>21;
    }

    public boolean hasBlackjack(){
        return (cards.size()==2) && (getTotal()==21);
    }

    public int getTotalNotSoft(){
        int total=0;
        
        for (Card card : cards) {
            total=total+card.getValue();           
        }
        
        return total;
    }

    public boolean isSoft(){
        return getTotal() > getTotalNotSoft();
    }

    public ArrayList<Card> getCards(){
        return cards;
    }

    public int getSize(){
        return cards.size();
    }

    public void clear(){
        cards = new ArrayList<>();
    }
    
    @Override
    public String toString(){
        String string ="[";
        for (Card card : cards) {
            string= string + " " + card.toString();
        }
        string=string + "] = " + getTotal() ;

        return string;
    }
}
