package de.candycraft.io.server.models.stats;

import lombok.Builder;
import lombok.Getter;
import org.json.JSONObject;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class Stats {

    @Getter
    private JSONObject stats;

    public Stats() {
    }

}
