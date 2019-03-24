package de.candycraft.io.server.manager.clan;

import de.candycraft.io.server.manager.Manager;
import de.candycraft.io.server.models.clan.Clan;
import de.candycraft.io.server.models.clan.ClanMember;
import de.candycraft.io.server.rest.responses.IOResponse;
import de.progme.athena.Athena;
import de.progme.athena.db.DBResult;
import de.progme.athena.db.DBRow;
import de.progme.athena.db.serialization.Condition;
import de.progme.athena.query.core.*;
import de.progme.thor.client.cache.PubSubCache;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 24.03.2019
 *
 * @author pauhull
 */
public class ClanManager extends Manager {

    private static String TABLE = "io_clans";
    private static String TABLE_MEMBERS = "io_clans_members";
    private static String CACHE_PREFIX = "io_cache_clans_";

    private Athena athena;
    private PubSubCache cache;
    private int expire;

    public ClanManager(Athena athena, PubSubCache cache, int expire) {

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
                .value("fullName", "varchar(16)")
                .value("tag", "varchar(4)")
                .value("exp", "float")
                .value("level", "int")
                .value("createdAt", "int")
                .build());
        athena.execute(new CreateQuery.Builder()
                .create(TABLE_MEMBERS)
                .ifNotExists(true)
                .primaryKey("id")
                .value("id", "int", "auto_increment")
                .value("clanId", "int")
                .value("clanGroupId", "int")
                .value("uuid", "varchar(36)")
                .value("name", "varchar(16)")
                .value("joinedAt", "bigint")
                .build());
    }

    public IOResponse createClan(Clan clan) {

        SelectQuery query = new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(new Condition("tag", Condition.Operator.EQUAL, clan.getTag()))
                .build();
        DBResult result = athena.query(query);

        if (result.size() > 0) {
            return new IOResponse.Builder()
                    .status(IOResponse.Status.ERROR)
                    .source(IOResponse.Source.MYSQL)
                    .message("Clan with that tag already exists")
                    .time(0)
                    .build();
        }

        JSONObject clanJson = clan.toJSON();

        InsertQuery.Builder builder = new InsertQuery.Builder()
                .into(TABLE);

        for (String key : clanJson.keySet()) {
            if (key.equals("members")) continue;
            builder.column(key);
            builder.value(clanJson.get(key).toString());
        }

        this.athena.execute(builder.build());

        builder = new InsertQuery.Builder()
                .into(TABLE_MEMBERS);

        for (ClanMember member : clan.getMembers()) {
            JSONObject memberJson = member.toJSON();

            for (String key : memberJson.keySet()) {
                builder.column(key);
                builder.value(memberJson.get(key).toString());
            }
        }

        this.athena.execute(builder.build());

        this.cache.put(CACHE_PREFIX + clan.getId(), clanJson, expire);
        this.cache.put(CACHE_PREFIX + clan.getTag(), clanJson, expire);

        return new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .source(IOResponse.Source.MYSQL)
                .time(0)
                .build();
    }

    public IOResponse updateClan(String column, String value, Condition condition) {

        DBResult result = athena.query(new UpdateQuery.Builder()
                .update(TABLE)
                .set(column, value)
                .where(condition)
                .build());

        if (result.rows().size() > 0) {
            return new IOResponse.Builder()
                    .time(0)
                    .status(IOResponse.Status.OK)
                    .source(IOResponse.Source.MYSQL)
                    .build();
        } else {
            return new IOResponse.Builder()
                    .time(0)
                    .status(IOResponse.Status.ERROR)
                    .source(IOResponse.Source.MYSQL)
                    .build();
        }

    }

    public IOResponse getClan(Condition condition) {

        SelectQuery.Builder builder = new SelectQuery.Builder()
                .select("*")
                .from(TABLE)
                .where(condition);

        builder.limit(1);

        DBResult clanResult = athena.query(builder.build());
        Clan clan = null;
        if (clanResult.size() == 1) {
            DBRow row = clanResult.row(0);
            clan = Clan.fromDBRow(row, Clan.class);

            List<ClanMember> members = new ArrayList<>();
            SelectQuery memberQuery = new SelectQuery.Builder()
                    .select("*")
                    .from(TABLE_MEMBERS)
                    .where(new Condition("clanId", Condition.Operator.EQUAL, Integer.toString(clan.getId())))
                    .build();

            DBResult memberResult = athena.query(memberQuery);
            memberResult.rows().forEach(memberRow -> {
                members.add(ClanMember.fromDBRow(memberRow, ClanMember.class));
            });

            for (ClanMember member : members) { // delete old entries
                athena.execute(new DeleteQuery.Builder()
                        .from(TABLE_MEMBERS)
                        .where(new Condition("uuid", Condition.Operator.EQUAL, member.getUuid().toString()))
                        .build());
            }

            clan.setMembers(members);
        }

        return new IOResponse.Builder()
                .status(IOResponse.Status.OK)
                .message(clan)
                .source(IOResponse.Source.MYSQL)
                .time(0)
                .build();
    }

}
