package de.candycraft.io.rest.resources;

import com.google.gson.Gson;
import de.candycraft.io.IO;
import de.candycraft.io.manager.player.Player;
import de.progme.athena.db.serialization.Condition;
import de.progme.hermes.server.http.Request;
import de.progme.hermes.server.http.annotation.Path;
import de.progme.hermes.server.http.annotation.PathParam;
import de.progme.hermes.server.http.annotation.Produces;
import de.progme.hermes.server.http.annotation.method.GET;
import de.progme.hermes.server.http.annotation.method.POST;
import de.progme.hermes.shared.ContentType;
import de.progme.hermes.shared.http.Response;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by marvinerkes on 26.01.17 with IntelliJ IDEA.
 */
@Path("/player")
public class PlayerResource {

    private static Logger logger = LoggerFactory.getLogger(PlayerResource.class);

    private static Gson gson = new Gson();

    @POST
    @Path("/create")
    @Produces(ContentType.APPLICATION_JSON)
    public Response createPlayer(Request httpRequest) {

        Player player = (Player)Player.fromJSON(new JSONObject(httpRequest.body()), Player.class);
        IO.getInstance().getPlayerManager().insertPlayer(player);

        return Response.ok().content("{}").build();
    }

    @GET
    @Path("/{key}/{value}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response getPlayer(Request httpRequest, @PathParam String key, @PathParam String value) {

        Player player = IO.getInstance().getPlayerManager().getPlayer(new Condition(key, Condition.Operator.EQUAL, value));

        String jsonString = player != null ? player.toJSONString() : "{}";

        return Response.ok().content(jsonString).build();
    }
}
