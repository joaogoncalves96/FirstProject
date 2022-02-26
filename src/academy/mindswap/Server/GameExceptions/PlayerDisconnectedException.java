package academy.mindswap.Server.GameExceptions;


import academy.mindswap.utils.ColorCodes;

public class PlayerDisconnectedException extends GameExceptions {
    public PlayerDisconnectedException() {
        super(ColorCodes.RED_BOLD_BRIGHT + "PLAYER DISCONNECTED, UPDATING LIST..." + ColorCodes.RESET);

    }
}
