package pt.isel.ls.view.commandviews.html;

import pt.isel.ls.model.commands.common.CommandResult;
import pt.isel.ls.model.commands.results.GetUsersResult;
import pt.isel.ls.model.entities.User;
import pt.isel.ls.utils.html.elements.Element;
import pt.isel.ls.view.View;

import static pt.isel.ls.utils.html.HtmlDsl.a;
import static pt.isel.ls.utils.html.HtmlDsl.body;
import static pt.isel.ls.utils.html.HtmlDsl.h1;
import static pt.isel.ls.utils.html.HtmlDsl.head;
import static pt.isel.ls.utils.html.HtmlDsl.html;
import static pt.isel.ls.utils.html.HtmlDsl.p;
import static pt.isel.ls.utils.html.HtmlDsl.table;
import static pt.isel.ls.utils.html.HtmlDsl.td;
import static pt.isel.ls.utils.html.HtmlDsl.th;
import static pt.isel.ls.utils.html.HtmlDsl.title;
import static pt.isel.ls.utils.html.HtmlDsl.tr;

public class GetUsersHtmlView extends View {

    private GetUsersResult result;

    public GetUsersHtmlView(CommandResult commandResult) {
        result = (GetUsersResult) commandResult;
    }

    @Override
    public String display() {
        Iterable<User> users = result.getUsers();
        Element html =
                html(
                        head(
                                title("Information of all Users")
                        ),
                        body(
                                a("/", "Home"),
                                h1("Information of all Users"),
                                buildUsersTable(users)
                        )
                );
        return html.toString();
    }

    private Element buildUsersTable(Iterable<User> users) {
        if (!users.iterator().hasNext()) {
            return p("No Users found in database.");
        }

        Element usersHeaders = tr();
        usersHeaders.addChild(th("User ID"));
        usersHeaders.addChild(th("Name"));
        usersHeaders.addChild(th("Email"));
        Element usersInfo = table();
        usersInfo.addChild(usersHeaders);

        for (User user : users) {
            usersInfo.addChild(buildUserData(user));
        }

        return usersInfo;
    }

    private Element buildUserData(User user) {
        Element tableRowData = tr();
        tableRowData.addChild(td(a("/users/" + user.getUid(), String.valueOf(user.getUid()))));
        tableRowData.addChild(td(user.getName()));
        tableRowData.addChild(td(user.getEmail()));
        return tableRowData;
    }
}
