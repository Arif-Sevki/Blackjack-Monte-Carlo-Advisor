
import Game.BlackjackGame;
import Ui.ConsoleUi;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Write your name: ");
        String name = scanner.nextLine();
        System.out.println();
        int balance = 1000;

        BlackjackGame game = new BlackjackGame(name, balance);
        ConsoleUi ui = new ConsoleUi(game);

        ui.start();

    }
    
}
