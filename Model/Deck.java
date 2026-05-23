package Model;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private ArrayList<Card> cards;
    private int dealCount;

    public Deck() {
        this.cards = new ArrayList<>();
        this.dealCount = 0;
        reset(); // call reset so deck is ready immediately
    }
    //For copying
    public Deck(Deck other) {
        this.cards = new ArrayList<>(other.cards);
        this.dealCount = other.dealCount;
    }

    public void reset() {
        cards = new ArrayList<>();
        dealCount = 0;
        for (Card.Suit suit : Card.Suit.values()) {
            for (Card.Rank rank : Card.Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card deal() {
        if (isEmpty())
            throw new IllegalStateException("Deck is empty, cannot deal.");
        Card top = cards.get(0);
        cards.remove(0);
        dealCount++;
        return top;
    }

    public void burn() {
        if (!isEmpty())
            cards.remove(0);
    }

    public boolean needReshuffle() {
        return getRemainingCount() <= 15;
    }

    public int getRemainingCount() {
        return cards.size();
    }

    public boolean isEmpty() {
        return cards.size() < 1;
    }

}
