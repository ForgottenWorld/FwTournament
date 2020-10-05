package me.kaotich00.fwtournament.utils;

public class UUIDUtils {

    public static String parseUUID(String unparsedUUID) {
        return unparsedUUID.substring(0, 8) + "-" + unparsedUUID.substring(8, 12) + "-" + unparsedUUID.substring(12, 16) + "-" + unparsedUUID.substring(16, 20) + "-" + unparsedUUID.substring(20, 32);
    }

}
