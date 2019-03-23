package de.candycraft.io.server.rest.resources;

import de.candycraft.io.server.IO;
import de.candycraft.io.server.models.chatlog.Chatlog;
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
 * Created by marvinerkes on 23.03.19 with IntelliJ IDEA.
 */
@Path("/chatlog")
public class ChatlogResource {

    @POST
    @Path("/create")
    @Produces(ContentType.APPLICATION_JSON)
    public Response createChatlog(Request httpRequest) {

        Chatlog chatlog = (Chatlog) Chatlog.fromJSON(new JSONObject(httpRequest.body()), Chatlog.class);
        IOResponse response = IO.getInstance().getChatlogManager().insertChatlog(chatlog);

        return Response.ok().content(response.toJSONString()).build();
    }

    @GET
    @Path("/{key}/{value}/{limit}")
    @Produces(ContentType.APPLICATION_JSON)
    public Response getChatlog(Request httpRequest, @PathParam String key, @PathParam String value, @PathParam String sLimit) {

        Condition condition = new Condition(key, Condition.Operator.EQUAL, value);
        int limit = sLimit != null ? Integer.parseInt(sLimit) : 0;

        IOResponse response;
        if(limit == 0) response = IO.getInstance().getChatlogManager().getChatlog(condition);
        else response = IO.getInstance().getChatlogManager().getChatlogs(condition, limit);

        return Response.ok().content(response.toJSONString()).build();
    }
}
