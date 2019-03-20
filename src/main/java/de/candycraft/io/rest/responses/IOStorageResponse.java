package de.candycraft.io.rest.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by marvinerkes on 24.01.17 with IntelliJ IDEA.
 */
@SuppressWarnings("FieldCanBeLocal")
public class IOStorageResponse extends IOResponse {

    @Getter
    @Setter
    Source source;

    @Getter
    @Setter
    double time;

    public IOStorageResponse() {

        super();

        super.setStatus(Status.REQUEST_TIMEOUT);
    }

    public IOStorageResponse(Status status, Object message, Source source, double time) {

        super(status, message);

        if(message == null)
            super.setStatus(Status.REQUEST_TIMEOUT);

        this.source = source;

        this.time = time;
    }

    public enum Source {

        MYSQL,
        CACHE,
        MCAPI,
        NONE
    }
}
