package de.candycraft.io.server.models.entitlement;


import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

@Builder(builderClassName = "Builder")
public class Permission extends IOModel {

    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
    private boolean enabled;

}
