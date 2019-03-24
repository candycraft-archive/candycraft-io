package de.candycraft.io.server.utils;

/**
 * Created by Paul
 * on 24.03.2019
 *
 * @author pauhull
 */
public class UUIDUtils {

    public static String undashUuid(String dashedUuid) {
        return dashedUuid.replace("-", "");
    }

    public static String dashUuid(String undashedUuid) {
        StringBuffer stringBuffer = new StringBuffer(undashedUuid);
        stringBuffer.insert(8, "-");
        stringBuffer = new StringBuffer(stringBuffer.toString());
        stringBuffer.insert(13, "-");
        stringBuffer = new StringBuffer(stringBuffer.toString());
        stringBuffer.insert(18, "-");
        stringBuffer = new StringBuffer(stringBuffer.toString());
        stringBuffer.insert(23, "-");
        return stringBuffer.toString();
    }

}
