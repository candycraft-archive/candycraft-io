package de.candycraft.io.server.models.punishment;

import de.candycraft.io.server.models.IOModel;
import lombok.Builder;
import lombok.Getter;

import java.net.InetAddress;
import java.util.UUID;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class PunishmentEntry extends IOModel {

    @Getter
    private int id;
    @Getter
    private int player;
    @Getter
    private Punishment punishment;
    @Getter
    private long issuedOn;
    @Getter
    private int issuedBy;
    @Getter
    private InetAddress address;

}