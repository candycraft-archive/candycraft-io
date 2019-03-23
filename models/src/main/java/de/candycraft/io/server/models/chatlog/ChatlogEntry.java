package de.candycraft.io.server.models.chatlog;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
    @Setter
    private int chatlogId;
    @Getter
    private int player;
    @Getter
    private String name;
    @Getter
    private String message;
    @Getter
    private long time;
}
