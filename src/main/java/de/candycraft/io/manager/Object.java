package de.candycraft.io.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.progme.athena.db.DBRow;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Marvin Erkes on 2019-03-21.
 */
public class Object {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JSONObject toJSON() {

        return new JSONObject(toJSONString());
    }

    public String toJSONString() {

        return gson.toJson(this);
    }

    public static Object fromJSON(JSONObject json, Class<? extends Object> clazz) {

        return gson.fromJson(json.toString(), clazz);
    }

    public static Object fromDBRow(DBRow dbRow, Class<? extends Object> clazz) {

        JSONObject json = new JSONObject();
        dbRow.entries().forEach((entry) -> json.put(entry.getKey(), entry.getValue()));

        return fromJSON(json, clazz);
    }
}
