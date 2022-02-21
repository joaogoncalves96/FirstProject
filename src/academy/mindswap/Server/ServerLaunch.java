package academy.mindswap.Server;

import java.io.IOException;

public class ServerLaunch {

    public static void main(String[] args) {


        Game game = new Game(10);

        try {
            game.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
