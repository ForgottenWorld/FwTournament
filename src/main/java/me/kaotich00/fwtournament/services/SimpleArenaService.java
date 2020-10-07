package me.kaotich00.fwtournament.services;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.storage.sqlite.SQLiteConnectionService;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SimpleArenaService {

    private final int SET_PLAYER_ONE_SPAWN = 1;
    private final int SET_PLAYER_TWO_SPAWN = 2;
    private final int SET_PLAYER_ONE_BATTLE = 3;
    private final int SET_PLAYER_TWO_BATTLE = 4;

    private static SimpleArenaService simpleArenaService;
    private HashMap<String, Arena> arenas;
    private HashMap<UUID, Integer> playerArenaCreation;
    private HashMap<UUID, String> playerArenaNameCreation;
    private HashMap<UUID, HashMap<Integer, Location>> playerArenaCoordinates;

    private SimpleArenaService() {
        if(simpleArenaService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.arenas = new HashMap<>();
        this.playerArenaCreation = new HashMap<>();
        this.playerArenaCoordinates = new HashMap<>();
        this.playerArenaNameCreation = new HashMap<>();
    }

    public static SimpleArenaService getInstance() {
        if(simpleArenaService == null) {
            simpleArenaService = new SimpleArenaService();
        }
        return simpleArenaService;
    }

    public boolean newArena(String arenaName, Location playerOneSpawn, Location playerTwoSpawn, Location playerOneBattle, Location playerTwoBattle) {
        if(arenas.containsKey(arenaName)) {
            return false;
        }

        Arena arena = new Arena(arenaName, playerOneSpawn, playerTwoSpawn, playerOneBattle, playerTwoBattle);
        arenas.put(arenaName, arena);

        return true;
    }

    public Optional<Arena> getArena(String name) {
        return arenas.containsKey(name) ? Optional.of(arenas.get(name)) : Optional.empty();
    }

    public void arenaCreationHandler(Player sender, Location location) {
        if(!playerArenaCreation.containsKey(sender.getUniqueId())) {
            return;
        }

        int senderStep = playerArenaCreation.get(sender.getUniqueId());
        switch(senderStep) {
            case SET_PLAYER_ONE_SPAWN:
                playerArenaCoordinates.put(sender.getUniqueId(), new HashMap<>());
                playerArenaCoordinates.get(sender.getUniqueId()).put(SET_PLAYER_ONE_SPAWN, location.add(0,1,0));
                Message.ARENA_CREATION_STEP_COMPLETED.send(sender, "First player spawn", location.getBlockX(), location.getBlockY());
                Message.ARENA_CREATION_STEP.send(sender, "SECOND PLAYER SPAWN");
                break;
            case SET_PLAYER_TWO_SPAWN:
                playerArenaCoordinates.get(sender.getUniqueId()).put(SET_PLAYER_TWO_SPAWN, location.add(0,1,0));
                Message.ARENA_CREATION_STEP_COMPLETED.send(sender, "Second player spawn", location.getBlockX(), location.getBlockY());
                Message.ARENA_CREATION_STEP.send(sender, "FIRST PLAYER BATTLE LOCATION");
                break;
            case SET_PLAYER_ONE_BATTLE:
                playerArenaCoordinates.get(sender.getUniqueId()).put(SET_PLAYER_ONE_BATTLE, location.add(0,1,0));
                Message.ARENA_CREATION_STEP_COMPLETED.send(sender, "First player battle location", location.getBlockX(), location.getBlockY());
                Message.ARENA_CREATION_STEP.send(sender, "SECOND PLAYER BATTLE SPAWN");
                break;
            case SET_PLAYER_TWO_BATTLE:
                playerArenaCoordinates.get(sender.getUniqueId()).put(SET_PLAYER_TWO_BATTLE, location.add(0,1,0));
                Message.ARENA_CREATION_STEP_COMPLETED.send(sender, "Second player battle location", location.getBlockX(), location.getBlockY());
                Message.ARENA_CREATION_COMPLETED.send(sender);

                Location playerOneSpawn = this.playerArenaCoordinates.get(sender.getUniqueId()).get(SET_PLAYER_ONE_SPAWN);
                Location playerTwoSpawn = this.playerArenaCoordinates.get(sender.getUniqueId()).get(SET_PLAYER_TWO_SPAWN);
                Location playerOneBattle = this.playerArenaCoordinates.get(sender.getUniqueId()).get(SET_PLAYER_ONE_BATTLE);
                Location playerTwoBattle = this.playerArenaCoordinates.get(sender.getUniqueId()).get(SET_PLAYER_TWO_BATTLE);

                newArena(this.playerArenaNameCreation.get(sender.getUniqueId()), playerOneSpawn, playerTwoSpawn, playerOneBattle, playerTwoBattle);

                this.playerArenaNameCreation.remove(sender.getUniqueId());
                this.playerArenaCoordinates.remove(sender.getUniqueId());
                this.playerArenaCreation.remove(sender.getUniqueId());

                break;
        }
        playerArenaCreation.put(sender.getUniqueId(), senderStep+1);
    }

    public void addPlayerToArenaCreation(Player player, String arenaName) {
        this.playerArenaCreation.put(player.getUniqueId(), SET_PLAYER_ONE_SPAWN);
        this.playerArenaNameCreation.put(player.getUniqueId(), arenaName);
    }

    public boolean isPlayerInCreationMode(Player player) {
        return this.playerArenaCreation.containsKey(player.getUniqueId());
    }

    public HashMap<String,Arena> getArenas() {
        return this.arenas;
    }

    public void deleteArena(String arenaName) {
        Arena arenaToDelete = this.arenas.get(arenaName);
        if(arenaToDelete != null) {
            this.arenas.remove(arenaName);
            CompletableFuture.runAsync(() -> {
                SQLiteConnectionService.getInstance().deleteArena(Fwtournament.getPlugin(Fwtournament.class), "fwtournament", arenaName);
            });
        }
    }

}
