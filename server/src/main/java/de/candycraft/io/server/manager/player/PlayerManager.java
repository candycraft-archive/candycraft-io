package de.candycraft.io.server.manager.player;

import de.candycraft.io.server.manager.Manager;
import de.candycraft.io.server.models.player.Player;
import de.candycraft.io.server.rest.responses.IOResponse;
import de.candycraft.io.server.utils.UUIDUtils;
import de.progme.athena.Athena;
import de.progme.athena.db.DBResult;
import de.progme.athena.db.serialization.Condition;
import de.progme.athena.query.core.CreateQuery;
import de.progme.athena.query.core.InsertQuery;
import de.progme.athena.query.core.SelectQuery;
import de.progme.athena.query.core.UpdateQuery;
import de.progme.thor.client.cache.PubSubCache;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Marvin Erkes on 2019-03-21.
 */
public class PlayerManager extends Manager {

    private static String TABLE = "io_player";
    private static String CACHE_PREFIX = "io_cache_player_";

    private Athena athena;
    private PubSubCache cache;
    private int expire;

    public PlayerManager(Athena athena, PubSubCache cache, int expire) {

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
                .value("uuid", "varchar(255)")
                .value("name", "varchar(255)")
                .value("server", "int")
                .value("onlineTime", "int")
                .build());
    }

    public IOResponse insertPlayer(Player player) {

        JSONObject playerJSON = player.toJSON();

        InsertQuery.Builder builder = new InsertQuery.Builder()
                .into(TABLE);

        playerJSON.keySet().forEach(key -> {
            builder.column(key);
            builder.value(playerJSON.get(key).toString());
        });
        this.athena.execute(builder.build());

        this.cache.put(CACHE_PREFIX + player.getUuid(), playerJSON, expire);
        this.cache.put(CACHE_PREFIX + player.getName(), playerJSON, expire);

        return new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .source(IOResponse.Source.MYSQL)
                .time(0)
                .build();
    }

    public IOResponse updatePlayer(Condition condition, JSONObject updates) {

        if(condition.column().equals("uuid") || condition.column().equals("name")) {
            this.cache.get(CACHE_PREFIX + condition.value(), (player) -> {
                if(player == null) return;
                this.cache.remove(CACHE_PREFIX + player.getString("uuid"));
                this.cache.remove(CACHE_PREFIX + player.getString("name"));
            });
        } else {
            List<Player> players = (List<Player>) this.getPlayers(condition, -1).getMessage();
            players.forEach(player -> {
                this.cache.remove(CACHE_PREFIX + player.getUuid());
                this.cache.remove(CACHE_PREFIX + player.getName());
            });
        }

        UpdateQuery.Builder builder = new UpdateQuery.Builder()
                .update(TABLE)
                .where(condition);

        updates.keySet().forEach(key -> builder.set(key, updates.get(key).toString()));

        this.athena.execute(builder.build());

        return new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .source(IOResponse.Source.MYSQL)
                .time(0)
                .build();
    }

    public IOResponse getPlayer(Condition condition) {

        IOResponse.Builder builder = new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .time(0);

        AtomicReference<Player> playerReference = new AtomicReference<>();

        if (condition.column().equals("uuid") || condition.column().equals("name")) {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            this.cache.get(CACHE_PREFIX + condition.value(), (cachedPlayer) -> {
                if(cachedPlayer != null) {
                    playerReference.set(Player.fromJSON(cachedPlayer, Player.class));
                    builder.source(IOResponse.Source.CACHE);
                }
                countDownLatch.countDown();
            });

            try {
                countDownLatch.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException ignore) {
            }
        }

        if (playerReference.get() == null) {
            DBResult matchedPlayers = athena.query(new SelectQuery.Builder()
                    .select("*")
                    .from(TABLE)
                    .where(condition)
                    .limit(1)
                    .build());

            if (matchedPlayers.size() != 0) playerReference.set(Player.fromDBRow(matchedPlayers.row(0), Player.class));

            builder.source(IOResponse.Source.MYSQL);
        }

        if (playerReference.get() == null) {
            if (condition.column().equals("uuid")) {

                try {

                    String undashedUuid = condition.value().replace("-", "");
                    URL url = new URL("https://api.mojang.com/user/profiles/" + undashedUuid + "/names");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    if (stringBuilder.length() > 0) {

                        JSONArray array = new JSONArray(stringBuilder.toString());
                        if (array.length() > 0) {
                            JSONObject object = array.getJSONObject(0);
                            String name = object.getString("name");
                            UUID uuid = UUID.fromString(UUIDUtils.dashUuid(undashedUuid));

                            Player player = Player.builder()
                                    .name(name)
                                    .uuid(uuid)
                                    .build();

                            playerReference.set(player);
                            builder.source(IOResponse.Source.MOJANG);
                        }
                    }

                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (condition.column().equals("name")) {

                try {

                    String name = condition.value();
                    URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }

                    if (stringBuilder.length() > 0) {

                        JSONObject object = new JSONObject(stringBuilder.toString());
                        if (object.has("id")) {
                            String undashedUuid = object.getString("id");
                            UUID uuid = UUID.fromString(UUIDUtils.dashUuid(undashedUuid));

                            Player player = Player.builder()
                                    .uuid(uuid)
                                    .name(name)
                                    .build();

                            playerReference.set(player);
                            builder.source(IOResponse.Source.MOJANG);
                        }
                    }

                    reader.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        if (playerReference.get() != null) {
            this.cache.put(CACHE_PREFIX + playerReference.get().getUuid(), playerReference.get().toJSON(), expire);
            this.cache.put(CACHE_PREFIX + playerReference.get().getName(), playerReference.get().toJSON(), expire);

            return builder
                    .message(playerReference.get())
                    .build();
        } else {

            return new IOResponse.Builder()
                    .source(IOResponse.Source.MOJANG)
                    .status(IOResponse.Status.ERROR)
                    .message("Player doesn't exist or rate limit is reached.")
                    .build();

        }
    }

    public IOResponse getPlayers(Condition condition, int limit) {

        //TODO: Possible to cache?

        List<Player> players = new ArrayList<>();

        SelectQuery.Builder builder = new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(condition);

        if(limit != -1) builder.limit(limit);

        DBResult matchedPlayers = athena.query(builder.build());

        matchedPlayers.rows().forEach(dbRow -> players.add(Player.fromDBRow(dbRow, Player.class)));

        return new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .message(players)
                .source(IOResponse.Source.MYSQL)
                .time(0)
                .build();
    }

    public Player getPlayer(UUID uuid) {

        IOResponse response = getPlayer(new Condition("uuid", Condition.Operator.EQUAL, uuid.toString()));

        return Player.fromJSON(new JSONObject(response.getMessage()), Player.class);
    }

}
