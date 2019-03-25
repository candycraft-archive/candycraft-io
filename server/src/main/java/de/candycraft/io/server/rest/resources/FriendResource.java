package de.candycraft.io.server.rest.resources;

import de.candycraft.io.server.IO;
import de.candycraft.io.server.rest.responses.IOResponse;
import de.progme.hermes.server.http.Request;
import de.progme.hermes.server.http.annotation.Path;
import de.progme.hermes.server.http.annotation.PathParam;
import de.progme.hermes.server.http.annotation.Produces;
import de.progme.hermes.server.http.annotation.method.GET;
import de.progme.hermes.shared.ContentType;
import de.progme.hermes.shared.http.Response;

import java.util.UUID;

/**
 * Created by Paul
 * on 24.03.2019
 *
 * @author pauhull
 */
@Path("/friends")
public class FriendResource {

    @GET
    @Path("/request/{from}/{to}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response requestFriendship(Request httpRequest, @PathParam String from, @PathParam String to) {

        IOResponse response = IO.getInstance().getFriendManager().requestFriendship(UUID.fromString(from), UUID.fromString(to));

        return Response.ok().content(response.toJSONString()).build();
    }

    @GET
    @Path("/accept/{from}/{to}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response acceptRequest(Request httpRequest, @PathParam String from, @PathParam String to) {

        IOResponse response = IO.getInstance().getFriendManager().acceptRequest(UUID.fromString(from), UUID.fromString(to));

        return Response.ok().content(response.toJSONString()).build();
    }

    @GET
    @Path("/friendship/{a}/{b}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response getFriendship(Request httpRequest, @PathParam String a, @PathParam String b) {

        IOResponse response = IO.getInstance().getFriendManager().getFriendship(UUID.fromString(a), UUID.fromString(b));

        return Response.ok().content(response.toJSONString()).build();
    }

    @GET
    @Path("/friendships/{uuid}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response getFriendships(Request httpRequest, @PathParam String uuid) {

        IOResponse response = IO.getInstance().getFriendManager().getFriendships(UUID.fromString(uuid));

        return Response.ok().content(response.toJSONString()).build();
    }
}
