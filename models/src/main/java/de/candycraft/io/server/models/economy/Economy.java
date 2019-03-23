package de.candycraft.io.server.models.economy;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

@Builder(builderClassName = "Builder")
public class Economy extends IOModel {

    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
    private String characters;


}
