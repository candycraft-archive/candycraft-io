package de.candycraft.io.server.models.chatlog;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class ChatlogEntry extends IOModel {

    @Getter
    private int id;
    @Getter
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    private long time;
    @Getter
    private String message;

}
