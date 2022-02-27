/*
 * @(#)Main.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.Server;
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
