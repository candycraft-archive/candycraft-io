package de.candycraft.io.server.rest.resources;

import de.candycraft.io.server.IO;
import de.candycraft.io.server.models.player.Player;
import de.candycraft.io.server.rest.responses.IOResponse;
import de.progme.athena.db.serialization.Condition;
import de.progme.hermes.server.http.Request;
import de.progme.hermes.server.http.annotation.Path;
import de.progme.hermes.server.http.annotation.PathParam;
import de.progme.hermes.server.http.annotation.Produces;
import de.progme.hermes.server.http.annotation.method.GET;
import de.progme.hermes.server.http.annotation.method.POST;
import de.progme.hermes.server.http.annotation.method.PUT;
import de.progme.hermes.shared.ContentType;
import de.progme.hermes.shared.http.Response;
import org.json.JSONObject;

/**
 * Created by marvinerkes on 26.01.17 with IntelliJ IDEA.
 */
@Path("/player")
public class PlayerResource {

    @POST
    @Path("/create")
    @Produces(ContentType.APPLICATION_JSON)
    public Response createPlayer(Request httpRequest) {

        Player player = (Player)Player.fromJSON(new JSONObject(httpRequest.body()), Player.class);
        IOResponse response = IO.getInstance().getPlayerManager().insertPlayer(player);

        return Response.ok().content(response.toJSONString()).build();
    }

    @GET
    @Path("/{key}/{value}/{limit}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response getPlayer(Request httpRequest, @PathParam String key, @PathParam String value, @PathParam String sLimit) {

        Condition condition = new Condition(key, Condition.Operator.EQUAL, value);
        int limit = sLimit != null ? Integer.parseInt(sLimit) : 0;

        IOResponse response;
        if(limit == 0) response = IO.getInstance().getPlayerManager().getPlayer(condition);
        else response = IO.getInstance().getPlayerManager().getPlayers(condition, limit);

        return Response.ok().content(response.toJSONString()).build();
    }

    @PUT
    @Path("/update/{uuid}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response updatePlayer(Request httpRequest, @PathParam String uuid) {

        Condition condition = new Condition("uuid", Condition.Operator.EQUAL, uuid);
        IOResponse response = IO.getInstance().getPlayerManager().updatePlayer(condition, new JSONObject(httpRequest.body()));

        return Response.ok().content(response.toJSONString()).build();
    }
}
