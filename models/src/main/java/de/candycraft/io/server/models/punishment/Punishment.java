package de.candycraft.io.server.models.punishment;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class Punishment extends IOModel {

    @Getter
    private int id;
    @Getter
    private String name;
    @Getter
    private PunishmentType type;
    @Getter
    private long duration;

}
