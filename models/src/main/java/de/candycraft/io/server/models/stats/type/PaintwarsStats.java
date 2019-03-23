package de.candycraft.io.server.models.stats.type;

import de.candycraft.io.server.models.stats.Stats;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
@Builder(builderClassName = "Builder")
public class PaintwarsStats extends Stats {

    @Getter
    private int id;
    @Getter
    private int player;
    @Getter
    private int wins;
    @Getter
    private int kills;
    @Getter
    private int deaths;
    @Getter
    private int collectedPowerups;
    @Getter
    private int coloredBlocks;

}
