package de.candycraft.io.server.utils;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by marvinerkes on 08.01.17.
 */
public class ShortUUID {

    private final String shortUUID;

    /**
     * ShortUUID constructor
     *
     * @param shortUUID the shortuuid as string
     */
    private ShortUUID(String shortUUID) {

        this.shortUUID = shortUUID;
    }

    /**
     * Get random shortuuid
     * @return shortuuid
     */
    public static ShortUUID randomUUID() {
        long l = ByteBuffer.wrap(UUID.randomUUID().toString().getBytes()).getLong();
        return ShortUUID.fromString(Long.toString(l, Character.MAX_RADIX));
    }

    /**
     * Deserialize the shortuuid string to a shortuuid
     *
     * @param shortUUIDString serialized shortuuid string
     * @return shortuuid
     */
    public static ShortUUID fromString(String shortUUIDString) {
        return new ShortUUID(shortUUIDString);
    }

    /**
     * Serialize the shortuuid to a string
     *
     * @return serialized shortuuid string
     */
    public String toString() {
        return shortUUID;
    }

    /**
     * Check if shortuuid equals to a other shortuuid
     * @param object shortuuid
     * @return if shortuuid match other shortuuid
     */
    @Override
    public boolean equals(Object object) {
        return object instanceof ShortUUID && shortUUID.equals(object.toString());
    }
}
