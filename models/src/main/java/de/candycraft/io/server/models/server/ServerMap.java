package de.candycraft.io.server.models.server;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Marvin Erkes on 2019-03-23.
 */
@Builder(builderClassName = "Builder")
public class ServerMap extends IOModel {

    @Getter
    private int id;
    @Getter
    private int serverType;
    @Getter
    private String name;
}
