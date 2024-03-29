package de.candycraft.io.server.models.clan;

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
public class Clan extends IOModel {

    @Getter
    private int id;
    @Getter
    private String fullName;
    @Getter
    private String tag;
    @Getter
    @Setter
    private List<ClanMember> members;
    @Getter
    private double exp;
    @Getter
    private int level;
    @Getter
    private int createdAt;

}
