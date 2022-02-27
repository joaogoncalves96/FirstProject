/*
 * @(#)LaunchPlayer2.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.Client;

import java.io.IOException;

public class LaunchPlayer2 {
    public static void main(String[] args) {

        /**
         * Instance of a new player
         * the Player will connect to the server
         */

        Player player2 = new Player();

        try {
            player2.connectToServer("localhost",8081);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
