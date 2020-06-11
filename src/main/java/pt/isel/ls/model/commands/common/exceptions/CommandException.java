package pt.isel.ls.model.commands.common.exceptions;

public abstract class CommandException extends Exception {

    public enum ExceptionType {
        ExitException,
        InvalidIdException,
        MissingArgumentsException,
        OverlapException,
        ParseArgumentException,
        ServerException
    }

    public CommandException(String message) {
        super("Error: " + message);
    }

    public abstract ExceptionType getExceptionType();
}
