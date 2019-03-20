package de.candycraft.io.rest.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by marvinerkes on 24.01.17 with IntelliJ IDEA.
 */
public class IOResponse {

    @Getter
    @Setter
    private Status status;

    @Getter
    @Setter
    private Object message;

    public IOResponse() {}

    public IOResponse(Status status, Object message) {

        this.status = status;
        this.message = message;
    }

    public enum Status {

        OK,
        ERROR,
        REQUEST_TIMEOUT,
        MYSQL_NOT_CONNECTED,
        THOR_NOT_CONNECTED,
        ACTION_NOT_FOUND
    }
}
