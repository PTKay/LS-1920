package pt.isel.ls.model.commands.common;

import java.sql.SQLException;

@FunctionalInterface
public interface CommandHandler {
    CommandResult execute(CommandRequest commandRequest) throws CommandException, SQLException;
}
