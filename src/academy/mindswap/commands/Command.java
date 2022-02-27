/*
 * @(#)Command.java        1.0 26/02/2022
 *
 * Copyright (c) MindSwap Academy - David Millasseau, Tiago Correia & João Gonçalves
 * All rights reserved.
 *
 * This software was produced to become our first group project.
 */

package academy.mindswap.commands;
/**
 * Commands that are available to be used by the poker game
 */


public enum Command {

    CALL("/call", new CallHandler()),
    BET("/bet", new BetHandler()),
    FOLD("/fold", new FoldHandler()),
    ALLIN("/allin", new AllinHandler()),
    CHECK("/check", new CheckHandler()),
    HELP("/help", new HelpHandler()),
    RAISE("/raise", new RaiseHandler());

    private final String description;
    private final CommandHandler commandHandler;

    /**
     * Method constructor of the enum command this accept two arguments
     * description represents the name of the command
     * handler represents the command that will receive and need to be handler
     */

    Command(String description, CommandHandler commandHandler) {
        this.description = description;
        this.commandHandler = commandHandler;
    }

    /**
     * For each command we assign a value
     * We compare the description with the description of the command and if it is the same, it returns the command. If not return null
     */

    public static Command getCommandFromDescription(String description) {
        for (Command command : values()) {
            if (description.equals(command.description)) {
                return command;
            }
        }
        return null;
    }

    /**
     * Lets you know what the description is
     * @return the command type
     */
    public String getDescription() {
        return description;
    }

    /**
     * Lets you know which command will be used
     * @return the enum command
     */
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
