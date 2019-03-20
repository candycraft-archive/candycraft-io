package de.candycraft.io.rest.filters;

import de.progme.hermes.server.filter.FilteredRequest;
import de.progme.hermes.server.filter.RequestFilter;
import de.progme.hermes.shared.Status;
import de.progme.hermes.shared.http.Response;
import de.progme.iris.Iris;
import de.progme.iris.IrisConfig;
import de.progme.iris.config.Key;
import de.progme.iris.exception.IrisException;
import de.candycraft.io.Main;
import de.candycraft.io.rest.filters.authentication.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by marvinerkes on 09.10.16.
 */
public class AuthenticationFilter implements RequestFilter {

    private static Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private IrisConfig config;

    private final Map<String, Token> tokens;

    public AuthenticationFilter() {

        File fileConfig = new File("authentication.iris");

        if (!fileConfig.exists())

            try {

                Files.copy(Main.class.getClassLoader().getResourceAsStream("authentication.iris"), fileConfig.toPath());
            } catch (IOException exception) {

                logger.error("Unable to copy default authentication config! No write permissions?", exception);
            }

        try {

            config = Iris.from("authentication.iris").build();
        } catch (IrisException exception) {

            logger.error("Error while parsing config!", exception);
        }

        tokens = new HashMap<>();

        loadTokens();
    }

    public void loadTokens() {

        logger.info("Loading tokens...");

        for(Key key : config.getHeader("tokens").getKeys())

            tokens.put(key.getName(), new Token(key.getName(), key.getValues()));

        logger.info("Loaded tokens!");

        logger.info("Tokens:");

        this.tokens.values().forEach((Token token) -> {

            logger.info(" - " + token.getToken());

            token.getResources().forEach((resource) -> logger.info("  - " + resource));
        });
    }

    @Override
    public void filter(FilteredRequest request) {

        String token = request.header("token");

        String location = request.location().replaceFirst("/", "");

        if(token == null) {

            request.abortWith(Response.status(Status.FORBIDDEN).build());

            return;
        }

        if(!tokens.keySet().contains(token)) {

            request.abortWith(Response.status(Status.FORBIDDEN).build());

            return;
        }

        if(!tokens.get(token).canAccess(location)) {

            request.abortWith(Response.status(Status.FORBIDDEN).build());

            return;
        }
    }
}