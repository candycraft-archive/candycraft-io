package de.candycraft.io.server.models.player;

import de.candycraft.io.server.models.IOModel;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Marvin Erkes on 2019-03-21.
 */
public class Player extends IOModel {

    @Getter
    private int id;
    @Getter
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    private int server;
    @Getter
    private long onlineTime;

    private Player(Builder builder) {

        this.id = builder.id;
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.server = builder.server;
        this.onlineTime = builder.onlineTime;
    }

    /**
     * Represents the builder for a player.
     */
    public static class Builder {

        private int id;
        private UUID uuid;
        private String name;
        private int server;
        private long onlineTime;

        /**
         * Sets the id.
         *
         * @param id the id.
         * @return the builder.
         */
        public Builder id(int id) {

            this.id = id;

            return this;
        }

        /**
         * Sets the uuid.
         *
         * @param uuid the uuid.
         * @return the builder.
         */
        public Builder uuid(UUID uuid) {

            this.uuid = uuid;

            return this;
        }

        /**
         * Sets the name.
         *
         * @param name the name.
         * @return the builder.
         */
        public Builder name(String name) {

            this.name = name;

            return this;
        }

        /**
         * Sets the server.
         *
         * @param server the server.
         * @return the builder.
         */
        public Builder server(int server) {

            this.server = server;

            return this;
        }

        /**
         * Sets the online time.
         *
         * @param onlineTime the online time.
         * @return the builder.
         */
        public Builder onlineTime(long onlineTime) {

            this.onlineTime = onlineTime;

            return this;
        }

        /**
         * Gets the finished Player.
         *
         * @return the Player.
         */
        public Player build() {

            return new Player(this);
        }
    }
}
