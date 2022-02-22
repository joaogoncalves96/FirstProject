package academy.mindswap.server;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {


        Game game = new Game(4);

        try {
            game.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
