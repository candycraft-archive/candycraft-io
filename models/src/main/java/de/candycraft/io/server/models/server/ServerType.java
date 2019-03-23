package de.candycraft.io.server.models.server;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by Marvin Erkes on 2019-03-23.
 */
@Builder(builderClassName = "Builder")
public class ServerType {

    @Getter
    private String id;
    @Getter
    private String name;
    @Getter
    private int minimumServers;
    @Getter
    private int maxServers;
    @Getter
    private int availableServers;
    @Getter
    private int reservedRAM;
    @Getter
    private int maxRAM;
    @Getter
    private int maxPlayers;
}
