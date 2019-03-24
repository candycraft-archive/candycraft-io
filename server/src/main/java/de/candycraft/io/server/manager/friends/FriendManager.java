package de.candycraft.io.server.manager.friends;

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
                .primaryKey("id")
                .value("id", "int", "auto_increment")
                .value("`from`", "varchar(36)")
                .value("`to`", "varchar(36)")
                .value("time", "bigint")
                .value("accepted", "bool")
                .build());
    }

    public IOResponse getFriendships(UUID uuid) {

        SelectQuery selectQuery = new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(new Condition("`from`", Condition.Operator.EQUAL, uuid.toString()))
                .where(new Condition("`to`", Condition.Operator.EQUAL, uuid.toString()))
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

        String sql = "SELECT * FROM " + TABLE + " WHERE (`from`='" + from + "' AND `to`='" + to + "') OR (`from`='" + to + "' AND `to`='" + from + "')";
        DBResult result = athena.query(sql);

        if (result.size() > 0) {

            DBRow row = result.row(0);
            Friendship friendship = Friendship.fromDBRow(row, Friendship.class);

            if (row.get("accepted")) {

                return new IOResponse.Builder()
                        .status("FRIEND_ALREADY_ADDED")
                        .source(IOResponse.Source.MYSQL)
                        .message(friendship)
                        .build();

            } else {

                result = athena.query("SELECT * FROM " + TABLE + " WHERE `from`='" + to + "' AND `to`='" + from + "'");

                if (result.size() > 0) {

                    return acceptRequest(to, from);
                } else {

                    return new IOResponse.Builder()
                            .status("REQUEST_ALREADY_SENT")
                            .source(IOResponse.Source.MYSQL)
                            .message(friendship)
                            .build();
                }
            }
        }

        InsertQuery query = new InsertQuery.Builder()
                .into(TABLE)
                .column("id").value("0")
                .column("`from`").value(from.toString())
                .column("`to`").value(to.toString())
                .column("time").value(Long.toString(System.currentTimeMillis()))
                .column("accepted").value("false")
                .build();

        athena.execute(query);

        result = athena.query("SELECT * FROM " + TABLE + " WHERE `from`='" + from + "' AND `to`='" + to + "'");
        Friendship friendship = Friendship.fromDBRow(result.row(0), Friendship.class);

        return new IOResponse.Builder()
                .source(IOResponse.Source.MYSQL)
                .status("REQUEST_SENT_SUCCESSFULLY")
                .message(friendship)
                .build();
    }

    public IOResponse acceptRequest(UUID from, UUID to) {

        DBResult result = athena.query("SELECT * FROM " + TABLE + " WHERE `from`='" + from + "' AND `to`='" + to + "'");

        if (result.size() < 1) { // no request received

            return new IOResponse.Builder()
                    .status("NO_REQUEST_RECEIVED")
                    .source(IOResponse.Source.MYSQL)
                    .build();
        }

        DBRow row = result.row(0);

        Friendship friendship = Friendship.fromDBRow(row, Friendship.class);

        if (row.get("accepted")) { // request already accepted

            return new IOResponse.Builder()
                    .status("REQUEST_ALREADY_ACCEPTED")
                    .source(IOResponse.Source.MYSQL)
                    .message(friendship)
                    .build();
        }

        athena.execute("UPDATE " + TABLE + " SET accepted=1 WHERE `from`='" + from + "' AND `to`='" + to + "'");

        return new IOResponse.Builder()
                .status("REQUEST_SUCCESSFULLY_ACCEPTED")
                .source(IOResponse.Source.MYSQL)
                .message(friendship)
                .build();
    }

}
