package de.candycraft.io.server.models.server;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Marvin Erkes on 2019-03-23.
 */
@Builder(builderClassName = "Builder")
public class Server extends IOModel {

    @Getter
    private int id;
    @Getter
    private String name;
    @Getter
    private int serverType;
    @Getter
    private int players;
    @Getter
    private int maxPlayers;
    @Getter
    private double tps;
}
