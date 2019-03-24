package de.candycraft.io.server.models.friends;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Paul
 * on 24.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class Friendship extends IOModel {

    @Getter
    private int id;
    @Getter
    private UUID from;
    @Getter
    private UUID to;
    @Getter
    private long time;
    @Getter
    private boolean accepted;

}
