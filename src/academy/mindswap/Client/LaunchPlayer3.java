/*
 * @(#)LaunchPlayer3.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.Client;

import academy.mindswap.utils.Messages;

import java.io.IOException;

public class LaunchPlayer3 {

    public static void main(String[] args) {

        /**
         * Instance of a new player
         * the Player will connect to the server
         */

        Player player3 = new Player();

        try {
            player3.connectToServer("localhost",8081);
        } catch (IOException e) {
            System.out.println(Messages.CANT_CONNECT);
            System.exit(1);
        }
    }
}
