package de.candycraft.io.server.manager.friends;

import de.candycraft.io.server.IO;
import de.candycraft.io.server.manager.Manager;
import de.candycraft.io.server.models.friends.Friendship;
import de.candycraft.io.server.rest.responses.IOResponse;
import de.progme.athena.Athena;
import de.progme.athena.db.DBResult;
import de.progme.athena.db.DBRow;
import de.progme.athena.db.serialization.Condition;
import de.progme.athena.query.core.CreateQuery;
import de.progme.athena.query.core.InsertQuery;
import de.progme.athena.query.core.SelectQuery;
import de.progme.thor.client.cache.PubSubCache;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Paul
 * on 24.03.2019
 *
 * @author pauhull
 */
public class FriendManager extends Manager {

    private static String TABLE = "io_friends";

    private Athena athena;
    private PubSubCache cache;
    private int expire;

    public FriendManager(Athena athena, PubSubCache cache, int expire) {

        super();

        this.athena = athena;
        this.cache = cache.async();
        this.expire = expire;
    }

    public void createTables() {

        athena.execute(new CreateQuery.Builder()
                .create(TABLE)
                .ifNotExists(true)
                .primaryKey("id")
                .value("id", "int", "auto_increment")
                .value("from", "varchar(36)")
                .value("to", "varchar(36)")
                .value("time", "bigint")
                .value("accepted", "bool")
                .build());
    }

    public IOResponse getFriendships(UUID uuid) {

        SelectQuery selectQuery = new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(new Condition("from", Condition.Operator.EQUAL, uuid.toString()))
                .where(new Condition("to", Condition.Operator.EQUAL, uuid.toString()))
                .build();
        DBResult result = athena.query(selectQuery);

        List<Friendship> friendships = new ArrayList<>();
        result.rows().forEach(row -> {
            friendships.add(Friendship.fromDBRow(row, Friendship.class));
        });

        return new IOResponse.Builder()
                .source(IOResponse.Source.MYSQL)
                .status(IOResponse.Status.OK)
                .time(System.currentTimeMillis())
                .message(friendships)
                .build();
    }

    public IOResponse requestFriendship(UUID from, UUID to) {

        String fromName = IO.getInstance().getPlayerManager().getPlayer(from).getName();
        String toName = IO.getInstance().getPlayerManager().getPlayer(to).getName();

        String sql = "SELECT * FROM " + TABLE + " WHERE from='" + from + "' OR from='" + to + "' OR to='" + to + "' OR to='" + from + "'";
        System.out.println(sql);
        DBResult result = athena.query(sql);

        if (result.size() > 0) {
            DBRow row = result.row(0);
            if (row.get("accepted")) {

                return new IOResponse.Builder()
                        .status(IOResponse.Status.ERROR)
                        .source(IOResponse.Source.MYSQL)
                        .time(System.currentTimeMillis())
                        .message("&cDu bist bereits mit " + toName + " befreundet.")
                        .build();

            } else {

                return new IOResponse.Builder()
                        .status(IOResponse.Status.ERROR)
                        .source(IOResponse.Source.MYSQL)
                        .time(System.currentTimeMillis())
                        .message("&cDu hast &f" + toName + "&c bereits eine Freundschaftsanfrage gesendet.")
                        .build();

            }
        }

        InsertQuery query = new InsertQuery.Builder()
                .into(TABLE)
                .column("id").value("0")
                .column("from").value(from.toString())
                .column("to").value(to.toString())
                .column("time").value(Long.toString(System.currentTimeMillis()))
                .build();

        athena.query(query);

        return new IOResponse.Builder()
                .source(IOResponse.Source.MYSQL)
                .time(System.currentTimeMillis())
                .status(IOResponse.Status.OK)
                .message("&aDu hast &f" + toName + "&a eine Freundschaftsanfrage gesendet.")
                .build();
    }

    public IOResponse acceptFriendship() {
        return null;
    }


}
