package de.candycraft.io.server.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.progme.athena.db.DBRow;
import org.json.JSONObject;

/**
 * Created by Marvin Erkes on 2019-03-21.
 */
public class IOObject {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JSONObject toJSON() {

        return new JSONObject(toJSONString());
    }

    public String toJSONString() {

        return gson.toJson(this);
    }

    public static IOObject fromJSON(JSONObject json, Class<? extends IOObject> clazz) {

        return gson.fromJson(json.toString(), clazz);
    }

    public static IOObject fromDBRow(DBRow dbRow, Class<? extends IOObject> clazz) {

        JSONObject json = new JSONObject();
        dbRow.entries().forEach((entry) -> json.put(entry.getKey(), entry.getValue()));

        return fromJSON(json, clazz);
    }
}
