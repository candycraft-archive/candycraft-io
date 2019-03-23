package de.candycraft.io.server.models.entitlement;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(builderClassName = "Builder")
public class Group {

    @Getter
    private int id;

    @Getter
    private Group parent;

    @Getter
    private String name;

    @Getter
    private List<Permission> permissions;

    @Getter
    private String tabprefix;

    @Getter
    private String tabsuffix;

    @Getter
    private String chatprefix;

    @Getter
    private String chatsuffix;

}
