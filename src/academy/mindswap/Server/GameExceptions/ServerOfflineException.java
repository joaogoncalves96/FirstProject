/*
 * @(#)ServerOfflineException.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.Server.GameExceptions;

import academy.mindswap.utils.Messages;

public class ServerOfflineException extends GameExceptions {
    public ServerOfflineException() {
        super(Messages.CANT_CONNECT);
    }
}
