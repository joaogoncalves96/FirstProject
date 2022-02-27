/*
 * @(#)PlayerDisconnectedException.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.Server.GameExceptions;

import academy.mindswap.utils.ColorCodes;

public class PlayerDisconnectedException extends GameExceptions {
    public PlayerDisconnectedException() {
        super(ColorCodes.RED_BOLD_BRIGHT + "PLAYER DISCONNECTED, UPDATING LIST..." + ColorCodes.RESET);
    }
}
