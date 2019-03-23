package de.candycraft.io.server.models.clan;

import de.candycraft.io.server.models.IOModel;
import de.candycraft.io.server.models.player.Player;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class Clan extends IOModel {

    @Getter
    private int id;
    @Getter
    private String fullName;
    @Getter
    private String tag;
    @Getter
    private Map<Player, ClanGroup> members;
    @Getter
    private double exp;
    @Getter
    private int level;

}
