package de.candycraft.io.rest.responses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

/**
 * Created by marvinerkes on 24.01.17 with IntelliJ IDEA.
 */
@SuppressWarnings("FieldCanBeLocal")
public class IOResponse {

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    Source source;

    @Getter
    @Setter
    double time;

    @Getter
    @Setter
    private Object message;


    public IOResponse(Builder builder) {

        this.status = builder.status;
        this.source = builder.source;
        this.time = builder.time;
        this.message = builder.message;
    }

    public JSONObject toJSON() {

        return new JSONObject(toJSONString());
    }

    public String toJSONString() {

        return gson.toJson(this);
    }

    public enum Status {

        OK,
        ERROR,
        REQUEST_TIMEOUT,
        ATHENA_NOT_CONNECTED,
        THOR_NOT_CONNECTED
    }

    public enum Source {

        MYSQL,
        CACHE
    }

    /**
     * Represents the builder for a io storage response.
     */
    public static class Builder {

        private Status status;
        private Object message;
        private Source source;
        private double time;

        /**
         * Sets the status.
         *
         * @param status the status.
         * @return the builder.
         */
        public IOResponse.Builder status(Status status) {

            this.status = status;

            return this;
        }

        /**
         * Sets the message.
         *
         * @param message the message.
         * @return the builder.
         */
        public IOResponse.Builder message(Object message) {

            this.message = message;

            return this;
        }

        /**
         * Sets the source.
         *
         * @param source the source.
         * @return the builder.
         */
        public IOResponse.Builder source(Source source) {

            this.source = source;

            return this;
        }

        /**
         * Sets the needed time.
         *
         * @param time the time.
         * @return the builder.
         */
        public IOResponse.Builder time(double time) {

            this.time = time;

            return this;
        }

        /**
         * Gets the finished io storage response.
         *
         * @return the io storage response.
         */
        public IOResponse build() {

            return new IOResponse(this);
        }
    }
}
