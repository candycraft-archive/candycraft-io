package de.candycraft.io.server.manager.chatlog;

import de.candycraft.io.server.manager.Manager;
import de.candycraft.io.server.models.chatlog.Chatlog;
import de.candycraft.io.server.models.chatlog.ChatlogEntry;
import de.candycraft.io.server.rest.responses.IOResponse;
import de.progme.athena.Athena;
import de.progme.athena.db.DBResult;
import de.progme.athena.db.serialization.Condition;
import de.progme.athena.query.core.CreateQuery;
import de.progme.athena.query.core.InsertQuery;
import de.progme.athena.query.core.SelectQuery;
import de.progme.thor.client.cache.PubSubCache;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Marvin Erkes on 2019-03-21.
 */
public class ChatlogManager extends Manager {

    private static String TABLE = "io_chatlog";
    private static String TABLE_ENTRIES = "io_chatlog_entries";
    private static String CACHE_PREFIX = "io_cache_chatlog_";

    private Athena athena;
    private PubSubCache cache;
    private int expire;

    public ChatlogManager(Athena athena, PubSubCache cache, int expire) {

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
                .value("identifier", "varchar(255)")
                .value("reporter", "int")
                .value("serverType", "int")
                .value("createdAt", "int")
                .build());
        athena.execute(new CreateQuery.Builder()
                .create(TABLE_ENTRIES)
                .primaryKey("id")
                .value("id", "int", "auto_increment")
                .value("chatlogId", "int")
                .value("player", "int")
                .value("name", "varchar(255)")
                .value("message", "varchar(255)")
                .value("time", "timestamp")
                .build());
    }

    public IOResponse insertChatlog(Chatlog chatlog) {

        JSONObject chatlogJSON = chatlog.toJSON();

        InsertQuery.Builder builder = new InsertQuery.Builder()
                .into(TABLE);
        chatlogJSON.keySet().forEach(key -> {
            if (key.equals("entries")) return;
            builder.column(key);
            builder.value(chatlogJSON.get(key).toString());
        });
        this.athena.execute(builder.build());

        DBResult result = athena.query(new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(new Condition("identifier", Condition.Operator.EQUAL, String.valueOf(chatlog.getIdentifier())))
                .limit(1)
                .build());
        int id = result.row(0).get("id");

        chatlog.getEntries().forEach(entry -> { // TODO: Double unobjection

            entry.setChatlogId(id);

            JSONObject entryJSON = entry.toJSON();

            InsertQuery.Builder insertBuilder = new InsertQuery.Builder()
                    .into(TABLE_ENTRIES);
            entryJSON.keySet().forEach(key -> {
                insertBuilder.column(key);
                insertBuilder.value(entryJSON.get(key).toString());
            });
            this.athena.execute(insertBuilder.build());
        });

        this.cache.put(CACHE_PREFIX + chatlog.getId(), chatlogJSON, expire);
        this.cache.put(CACHE_PREFIX + chatlog.getIdentifier(), chatlogJSON, expire);

        return new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .source(IOResponse.Source.MYSQL)
                .time(0)
                .build();
    }

    public IOResponse getChatlog(Condition condition) {

        IOResponse.Builder builder = new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .time(0);

        AtomicReference<Chatlog> chatlog = new AtomicReference<>();

        CountDownLatch countDownLatch = new CountDownLatch(1);

        this.cache.get(CACHE_PREFIX + condition.value(), (cachedChatlog) -> {
            if (cachedChatlog != null) {
                chatlog.set((Chatlog) Chatlog.fromJSON(cachedChatlog, Chatlog.class));
                builder.source(IOResponse.Source.CACHE);
            }
            countDownLatch.countDown();
        });

        try {
            countDownLatch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }

        if (chatlog.get() == null) {
            DBResult matchedChatlogs = athena.query(new SelectQuery.Builder()
                    .select("*")
                    .from(TABLE)
                    .where(condition)
                    .build());

            if (matchedChatlogs.size() != 0) {
                chatlog.set((Chatlog) Chatlog.fromDBRow(matchedChatlogs.row(0), Chatlog.class));

                chatlog.get().setEntries(new ArrayList<>());

                DBResult matchedEntries = athena.query(new SelectQuery.Builder()
                        .select("*")
                        .from(TABLE_ENTRIES)
                        .where(new Condition("chatlogId", Condition.Operator.EQUAL, String.valueOf(chatlog.get().getId())))
                        .limit(1)
                        .build());

                matchedEntries.rows().forEach(dbRow -> chatlog.get().getEntries().add((ChatlogEntry) ChatlogEntry.fromDBRow(dbRow, ChatlogEntry.class)));
            }

            builder.source(IOResponse.Source.MYSQL);
        }

        if (chatlog.get() != null) {
            this.cache.put(CACHE_PREFIX + chatlog.get().getId(), chatlog.get().toJSON(), expire);
            this.cache.put(CACHE_PREFIX + chatlog.get().getIdentifier(), chatlog.get().toJSON(), expire);
        }

        return builder
                .message(chatlog.get())
                .build();
    }

    public IOResponse getChatlogs(Condition condition, int limit) {

        //TODO: Possible to cache?
        List<Chatlog> chatlogs = new ArrayList<>();

        SelectQuery.Builder builder = new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(condition);

        if (limit != -1) builder.limit(limit);

        DBResult matchedChatlogs = athena.query(builder.build());

        matchedChatlogs.rows().forEach(dbChatlog -> {

            Chatlog chatlog = (Chatlog) Chatlog.fromDBRow(dbChatlog, Chatlog.class);

            chatlog.setEntries(new ArrayList<>());

            DBResult matchedEntries = athena.query(new SelectQuery.Builder()
                    .select("*")
                    .from(TABLE_ENTRIES)
                    .where(new Condition("chatlogId", Condition.Operator.EQUAL, String.valueOf(chatlog.getId())))
                    .limit(1)
                    .build());

            matchedEntries.rows().forEach(dbEntry -> chatlog.getEntries().add((ChatlogEntry) ChatlogEntry.fromDBRow(dbEntry, ChatlogEntry.class)));
            chatlogs.add(chatlog);
        });

        return new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .message(chatlogs)
                .source(IOResponse.Source.MYSQL)
                .time(0)
                .build();
    }
}
