package de.candycraft.io.server.models.punishment;

import lombok.Builder;
import lombok.Getter;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class Punishment {

    @Getter
    private int id;
    @Getter
    private String name;
    @Getter
    private PunishmentType type;
    @Getter
    private long duration;

}
