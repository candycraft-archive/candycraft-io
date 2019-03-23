package de.candycraft.io.server.models.punishment;

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
public class PunishmentEntry {

    @Getter
    private int id;
    @Getter
    private UUID uuid;
    @Getter
    private Punishment punishment;
    @Getter
    private long issuedOn;
    @Getter
    private UUID issuedBy;
    @Getter
    private InetAddress address;

}