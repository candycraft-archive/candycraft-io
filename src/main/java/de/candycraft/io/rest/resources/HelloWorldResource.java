package de.candycraft.io.rest.resources;

import com.google.gson.Gson;
import de.progme.hermes.server.http.Request;
import de.progme.hermes.server.http.annotation.Path;
import de.progme.hermes.server.http.annotation.PathParam;
import de.progme.hermes.server.http.annotation.Produces;
import de.progme.hermes.server.http.annotation.method.GET;
import de.progme.hermes.shared.ContentType;
import de.progme.hermes.shared.http.Response;
import de.candycraft.io.IO;
import de.candycraft.io.rest.responses.IOResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Created by marvinerkes on 26.01.17 with IntelliJ IDEA.
 */
@Path("/hello")
public class HelloWorldResource {

    private static Logger logger = LoggerFactory.getLogger(HelloWorldResource.class);

    private static Gson gson = new Gson();

    @GET
    @Path("/world")
    @Produces(ContentType.APPLICATION_JSON)
    public Response helloWorld(Request httpRequest, @PathParam String stringUniqueId, @PathParam String name) {

        JSONObject message = new JSONObject();
        message.put("hello", "world");
        return Response.ok().content(message.toString(4)).build();
    }
}
