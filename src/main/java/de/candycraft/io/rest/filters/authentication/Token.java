package de.candycraft.io.rest.filters.authentication;

import de.progme.iris.config.Value;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marvinerkes on 23.01.17 with IntelliJ IDEA.
 */
public class Token {

    @Getter
    private final String token;

    @Getter
    private final List<String> resources;

    public Token(String token, List<Value> resources) {

        this.token = token;

        this.resources = new ArrayList<>();

        resources.forEach((resource) -> this.resources.add(resource.asString()));
    }

    public boolean canAccess(String resource) {

        if(resources.equals("*"))

            return true;

        for(String tokenResource : resources) {

            if(tokenResource.contains("*"))

                return resource.startsWith(tokenResource.replace("*", ""));

            if(resource.contains(tokenResource))

                return true;
        }

        return false;
    }
}
