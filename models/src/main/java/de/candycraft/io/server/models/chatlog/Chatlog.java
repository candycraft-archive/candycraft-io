package de.candycraft.io.server.models.chatlog;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class Chatlog extends IOModel {

    @Getter
    private int id;
    @Getter
    @Setter
    private String identifier;
    @Getter
    private int reporter;
    @Getter
    private int serverType;
    @Getter
    @Setter
    private List<ChatlogEntry> entries;
    @Getter
    private long createdAt;
}
