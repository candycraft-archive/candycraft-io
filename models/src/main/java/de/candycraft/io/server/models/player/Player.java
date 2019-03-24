package de.candycraft.io.server.models.player;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Created by Marvin Erkes on 2019-03-21.
 */
@Builder(builderClassName = "Builder")
public class Player extends IOModel {

    @Getter
    @Setter
    private int id;
    @Getter
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    private int server;
    @Getter
    private long onlineTime;

    @Override
    public String toString() {
        return uuid.toString();
    }
}
