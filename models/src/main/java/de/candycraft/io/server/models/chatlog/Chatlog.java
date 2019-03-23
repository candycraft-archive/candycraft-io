package de.candycraft.io.server.models.chatlog;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class Chatlog {

    @Getter
    private int id;
    @Getter
    private List<ChatlogEntry> entries;
    @Getter
    private long createdOn;
    @Getter
    private String chatlogId;

}
