package de.candycraft.io.server.models.clan;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Paul
 * on 24.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class ClanMember extends IOModel {

    @Getter
    private int id;
    @Getter
    private int clanId;
    @Getter
    private int clanGroupId;
    @Getter
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    private long joinedAt;

}
