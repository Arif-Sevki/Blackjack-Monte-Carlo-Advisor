package Model;

public class Card {

    public enum Suit {
        Hearts, Diamonds, Clubs, Spades
    }

    public enum Rank {
        A, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, J, Q, K
    }

    private final Suit suit;
    private final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public int getValue() {

        switch (this.rank) {

            case Ten:
            case Q:
            case J:
            case K:
                return 10;
            case A:
                return 1;
            case Two:
                return 2;
            case Three:
                return 3;
            case Four:
                return 4;
            case Five:
                return 5;
            case Six:
                return 6;
            case Seven:
                return 7;
            case Eight:
                return 8;
            case Nine:
                return 9;
        }
        throw new IllegalArgumentException("Unknown rank: " + this.rank);

    }

    @Override
    public String toString() {
        return this.rank + " of " + this.suit;
    }

    public Suit getSuit() {
        return suit;
    }

    public Rank getRank() {
        return rank;
    }

}
