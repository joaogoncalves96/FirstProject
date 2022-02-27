package academy.mindswap.Server.GameExceptions;

import academy.mindswap.utils.Messages;

public class ServerOfflineException extends GameExceptions {
    public ServerOfflineException() {
        super(Messages.CANT_CONNECT);
    }
}
