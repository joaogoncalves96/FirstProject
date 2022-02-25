package academy.mindswap.commands;

public enum Command {

    CALL("/call", new CallHandler()),
    BET("/bet", new BetHandler()),
    FOLD("/fold", new FoldHandler()),
    ALLIN("/allin", new AllinHandler()),
    CHECK("/check", new CheckHandler());

    private String description;
    private CommandHandler commandHandler;

    Command(String description, CommandHandler commandHandler) {
        this.description = description;
        this.commandHandler = commandHandler;
    }

    public static Command getCommandFromDescription(String description) {
        for (Command command : values()) {
            if (description.equals(command.description)) {
                return command;
            }
        }
        return null;
    }


    public String getDescription() {
        return description;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
