package Model;

public class Player {
    private String name;
    private Hand hand;
    private int balance;
    private int currentBet;

    public Player(String name, int balance) {
        this.name = name;
        this.hand = new Hand();
        this.balance = balance;
        this.currentBet = 0;
    }

    public void placeBet(int amount) {
        if (amount <= balance) {
            currentBet = amount;
            balance = balance - amount;
        }
        else
            throw new IllegalArgumentException("Player can not bet more than his balance");
    }

    public void winBlackJack() {
        balance = (balance + (currentBet * 3));
        currentBet = 0;
    }

    public void winBet() {
        balance = (balance + (currentBet * 2));
        currentBet = 0;
    }

    public void loseBet(){
        //balance already decreased in placeBet()
        currentBet=0;
    }

    public void pushBet(){
        balance = balance+ currentBet;
        currentBet=0;
    }

    //Can be used only in first two cards.(Control on BlackjackGame.java)
    public void doubleDown(){
        balance = balance - currentBet;
        currentBet = currentBet*2;
    }

    public Hand getHand(){
        return hand;
    }

    public String getName(){
        return name;
    }

    public int getBalance(){
        return balance;
    }

    public int getCurrentBet(){
        return currentBet;
    }

    public boolean isBroke(){
        return balance<=0;
    }

    public void resetHand(){
        hand.clear();
    }



}
