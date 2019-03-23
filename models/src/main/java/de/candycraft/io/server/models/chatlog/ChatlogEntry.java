package de.candycraft.io.server.models.chatlog;

import de.candycraft.io.server.models.IOModel;
import lombok.Getter;

import java.util.UUID;

/**
 * Created by Paul
 * on 23.03.2019
 *
 * @author pauhull
 */
public class ChatlogEntry extends IOModel {

    @Getter
    private int id;
    @Getter
    private UUID uuid;
    @Getter
    private String name;
    @Getter
    private long time;
    @Getter
    private String message;

    public ChatlogEntry(Builder builder) {
        this.id = builder.id;
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.time = builder.time;
        this.message = builder.message;
    }

    public static class Builder {

        private int id;
        private UUID uuid;
        private String name;
        private long time;
        private String message;

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
         * Sets the id.
         *
         * @param time the time.
         * @return the builder.
         */
        public Builder time(long time) {

            this.time = time;

            return this;
        }

        /**
         * Sets the id.
         *
         * @param message the message.
         * @return the builder.
         */
        public Builder message(String message) {

            this.message = message;

            return this;
        }
    }
}
