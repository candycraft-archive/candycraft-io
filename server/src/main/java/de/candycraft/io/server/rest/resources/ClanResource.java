package de.candycraft.io.server.rest.resources;

import de.candycraft.io.server.IO;
import de.candycraft.io.server.models.clan.Clan;
import de.candycraft.io.server.rest.responses.IOResponse;
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

/**
 * Created by Paul
 * on 24.03.2019
 *
 * @author pauhull
 */
@Path("/clan")
public class ClanResource {

    @POST
    @Path("/create")
    @Produces(ContentType.APPLICATION_JSON)
    public Response createClan(Request httpRequest) {

        System.out.println(httpRequest.body());

        Clan clan = Clan.fromJSON(new JSONObject(httpRequest.body()), Clan.class);
        IOResponse response = IO.getInstance().getClanManager().createClan(clan);

        return Response.ok().content(response.toJSONString()).build();
    }

    @GET
    @Path("/{key}/{value}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response getClan(Request httpRequest, @PathParam String key, @PathParam String value) {

        Condition condition = new Condition(key, Condition.Operator.EQUAL, value);
        IOResponse response = IO.getInstance().getClanManager().getClan(condition);

        return Response.ok().content(response.toJSONString()).build();
    }

}
