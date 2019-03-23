package de.candycraft.io.server.models.clan;

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
public class ClanGroup extends IOModel {

    @Getter
    private int id;
    @Getter
    private String name;
    @Getter
    private int rank;
    @Getter
    private char color;

}
