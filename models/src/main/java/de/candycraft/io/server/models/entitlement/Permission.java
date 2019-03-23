package de.candycraft.io.server.models.entitlement;


import lombok.Builder;
import lombok.Getter;

@Builder(builderClassName = "Builder")
public class Permission {

    @Getter
    private int id;

    @Getter
    private String name;

    @Getter
    private boolean enabled;

}
