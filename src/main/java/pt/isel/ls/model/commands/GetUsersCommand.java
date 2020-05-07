package pt.isel.ls.model.commands;

import pt.isel.ls.model.commands.common.CommandException;
import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.sql.TransactionManager;
import pt.isel.ls.model.entities.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GetUsersCommand implements CommandHandler {
    @Override
    public CommandResult execute(CommandRequest commandRequest) throws CommandException, SQLException {
        CommandResult result = new CommandResult();
        TransactionManager trans = commandRequest.getTransactionHandler();
        trans.executeTransaction(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT uid, name "
                    + "FROM USERS");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.addResult(new User(
                        rs.getInt("uid"),
                        rs.getString("name")
                ));
            }
            rs.close();
            ps.close();
        });
        return result;
    }

    @Override
    public String toString() {
        return "returns all users in database";
    }
}