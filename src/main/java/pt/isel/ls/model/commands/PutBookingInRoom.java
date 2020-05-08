package pt.isel.ls.model.commands;

import pt.isel.ls.model.commands.common.CommandException;
import pt.isel.ls.model.commands.common.CommandHandler;
import pt.isel.ls.model.commands.common.CommandRequest;
import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.common.Parameters;
import pt.isel.ls.model.commands.sql.TransactionManager;
import pt.isel.ls.model.paths.Path;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import static pt.isel.ls.utils.DateUtils.parseTime;
import static pt.isel.ls.utils.DateUtils.parseTimeWithTimezone;

public class PutBookingInRoom implements CommandHandler {
    @Override
    public CommandResult execute(CommandRequest commandRequest) throws CommandException, SQLException {
        CommandResult result = new CommandResult();
        TransactionManager trans = commandRequest.getTransactionHandler();
        trans.executeTransaction(con -> {
            PreparedStatement ps = con.prepareStatement("UPDATE BOOKING "
                            + "SET uid = ?, begin_inst = ?, end_inst = ? "
                            + "WHERE rid = ? AND bid = ?"
            );
            Parameters params = commandRequest.getParams();
            Path path = commandRequest.getPath();
            if (params == null) {
                throw new CommandException("No parameters specified");
            }
            Integer uid;
            Integer rid;
            Integer bid;
            try {
                uid = params.getInt("uid");
                rid = path.getInt("rid");
                bid = path.getInt("bid");
            } catch (NumberFormatException e) {
                throw new CommandException("Invalid Room, Booking or User ID");
            }
            String duration = params.getString("duration");
            String begin = params.getString("begin");
            if (uid != null && rid != null && bid != null && duration != null && begin != null) {
                ps.setInt(1, uid);
                ps.setInt(4, rid);
                ps.setInt(5, bid);

                Date beginDate;
                Date durationDate;
                try {
                    beginDate = parseTimeWithTimezone(begin, "yyyy-MM-dd HH:mm");
                    //Parsed time without timezone because the duration is independent of Timezones
                    durationDate = parseTime(duration, "HH:mm");
                } catch (ParseException e) {
                    throw new CommandException("Failed to parse dates");
                }
                Date endDate = new Date(beginDate.getTime() + durationDate.getTime());

                if (!hasOverlaps(beginDate, endDate, rid, bid, con)) {
                    ps.setTimestamp(2, new java.sql.Timestamp(beginDate.getTime()));
                    ps.setTimestamp(3, new java.sql.Timestamp(endDate.getTime()));

                    ps.executeUpdate();
                } else {
                    throw new CommandException("Could not modify Booking, as it overlaps with an existing one");
                }
            } else {
                throw new CommandException("No arguments found / Invalid arguments");
            }
        });
        return result;
    }

    private boolean hasOverlaps(Date begin, Date end, int rid, int bid, Connection con) throws SQLException {
        PreparedStatement ps = con.prepareStatement("select rid, begin_inst, end_inst"
                + " from booking"
                + " where (?, ?) overlaps (begin_inst, end_inst)"
                + " and ? = rid and ? != bid");
        ps.setTimestamp(1, new java.sql.Timestamp(begin.getTime()));
        ps.setTimestamp(2, new java.sql.Timestamp(end.getTime()));
        ps.setInt(3, rid);
        ps.setInt(4, bid);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }

    @Override
    public String toString() {
        return "Changes the identified booking, given the following additional parameters\n"
                + "- begin - the begin instant for the booking period.\n"
                + "- duration - the booking duration.\n"
                + "- uid - the identifier of the user making the booking.";
    }
}
