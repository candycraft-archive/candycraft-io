package de.candycraft.io.manager.player;

import de.candycraft.io.manager.Manager;
import de.progme.athena.Athena;
import de.progme.athena.db.DBResult;
import de.progme.athena.db.serialization.Condition;
import de.progme.athena.query.core.CreateQuery;
import de.progme.athena.query.core.InsertQuery;
import de.progme.athena.query.core.SelectQuery;
import de.progme.thor.client.cache.PubSubCache;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.PortableInterceptor.LOCATION_FORWARD;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marvin Erkes on 2019-03-21.
 */
public class PlayerManager extends Manager {

    private static String TABLE = "io_players";

    private Athena athena;
    private PubSubCache cache;

    public PlayerManager(Athena athena, PubSubCache cache) {

        super();

        this.athena = athena;
        this.cache = cache;
    }

    public void createTables() {

        athena.execute(new CreateQuery.Builder()
                .create(TABLE)
                .primaryKey("id")
                .value("id", "int", "auto_increment")
                .value("uuid", "varchar(255)")
                .value("name", "varchar(255)")
                .value("server", "int")
                .value("onlineTime", "int")
                .build());
    }

    public void insertPlayer(Player player) {

        JSONObject json = player.toJSON();

        InsertQuery.Builder builder = new InsertQuery.Builder()
                .into("io_players");

        json.keySet().forEach(key -> {
            builder.column(key);
            builder.value(json.get(key).toString());
        });

        this.athena.execute(builder.build());
    }

    public Player getPlayer(Condition condition) {

        DBResult matchedPlayers = athena.query(new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(condition)
                .build());

        if(matchedPlayers.size() == 0) return null;

        return (Player)Player.fromDBRow(matchedPlayers.row(0), Player.class);
    }

    public List<Player> getPlayers(Condition condition) {

        List<Player> players = new ArrayList<>();

        DBResult matchedPlayers = athena.query(new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(condition)
                .build());

        matchedPlayers.rows().forEach(dbRow -> players.add((Player) Player.fromDBRow(dbRow, Player.class)));

        return players;
    }
}
