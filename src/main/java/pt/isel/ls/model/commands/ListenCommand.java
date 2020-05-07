package pt.isel.ls.model.commands;

import org.eclipse.jetty.plus.servlet.ServletHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import pt.isel.ls.model.commands.common.CommandException;
import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.common.Parameters;
import pt.isel.ls.model.entities.Message;
import pt.isel.ls.CommandServlet;

import java.sql.SQLException;

public class ListenCommand implements CommandHandler {
    @Override
    public CommandResult execute(CommandRequest commandRequest) throws CommandException, SQLException {
        Parameters params = commandRequest.getParams();
        if (params == null) {
            throw new CommandException("No parameters specified");
        }

        Integer port = params.getInt("port");
        if (port == null) {
            throw new CommandException("No port specified");
        }
        Server server = new Server(port);
        ServletHandler handler = new ServletHandler();
        CommandServlet servlet = new CommandServlet(commandRequest.getRouter(), commandRequest.getTransactionHandler());

        handler.addServletWithMapping(new ServletHolder(servlet), "/*");

        server.setHandler(handler);
        try {
            server.start();
            server.join();
        } catch (Exception e) {
            throw new CommandException("Failed to start server");
        }

        CommandResult result = new CommandResult();
        result.addResult(new Message("Server started on port " + port));
        return result;
    }
}