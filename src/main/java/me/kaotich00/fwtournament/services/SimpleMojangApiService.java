package me.kaotich00.fwtournament.services;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.storage.sqlite.SQLiteConnectionService;
import me.kaotich00.fwtournament.utils.UUIDUtils;
import org.bukkit.Bukkit;
import org.shanerx.mojang.Mojang;

import java.util.UUID;

public class SimpleMojangApiService {

    private static SimpleMojangApiService simpleMojangApiService;

    private SimpleMojangApiService() {
        if(simpleMojangApiService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SimpleMojangApiService getInstance() {
        if(simpleMojangApiService == null) {
            simpleMojangApiService = new SimpleMojangApiService();
        }
        return simpleMojangApiService;
    }

    public UUID getPlayerUUID(String playerName) {
        if(Bukkit.getPlayer(playerName) != null) {
            return Bukkit.getPlayer(playerName).getUniqueId();
        }

        UUID playerUUID = SQLiteConnectionService.getInstance().getCachedUUID(Fwtournament.getPlugin(Fwtournament.class), "fwtournament", playerName);
        if(playerUUID != null) {
            return playerUUID;
        }

        Mojang api = new Mojang().connect();
        String mojangApiUUID = api.getUUIDOfUsername(playerName);

        if(mojangApiUUID != null) {
            playerUUID = UUID.fromString(UUIDUtils.parseUUID(mojangApiUUID));
            SQLiteConnectionService.getInstance().cacheUUID(Fwtournament.getPlugin(Fwtournament.class), "fwtournament", playerName, playerUUID);
            return UUID.fromString(UUIDUtils.parseUUID(mojangApiUUID));
        }

        return null;
    }

}
