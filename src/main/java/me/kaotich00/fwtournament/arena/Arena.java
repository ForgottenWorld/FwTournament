package me.kaotich00.fwtournament.arena;

import org.bukkit.Location;

import java.util.Objects;

public class Arena {

    private String arenaName;

    private Location playerOneSpawn;
    private Location playerTwoSpawn;

    private Location playerOneBattle;
    private Location playerTwoBattle;

    public Arena(String arenaName, Location playerOneSpawn, Location playerTwoSpawn, Location playerOneBattle, Location playerTwoBattle) {
        this.arenaName = arenaName;
        this.playerOneSpawn = playerOneSpawn;
        this.playerTwoSpawn = playerTwoSpawn;
        this.playerOneBattle = playerOneBattle;
        this.playerTwoBattle = playerTwoBattle;
    }

    public Location getPlayerOneSpawn() {
        return playerOneSpawn;
    }

    public void setPlayerOneSpawn(Location playerOneSpawn) {
        this.playerOneSpawn = playerOneSpawn;
    }

    public Location getPlayerTwoSpawn() {
        return playerTwoSpawn;
    }

    public void setPlayerTwoSpawn(Location playerTwoSpawn) {
        this.playerTwoSpawn = playerTwoSpawn;
    }

    public Location getPlayerOneBattle() {
        return playerOneBattle;
    }

    public void setPlayerOneBattle(Location playerOneBattle) {
        this.playerOneBattle = playerOneBattle;
    }

    public Location getPlayerTwoBattle() {
        return playerTwoBattle;
    }

    public void setPlayerTwoBattle(Location playerTwoBattle) {
        this.playerTwoBattle = playerTwoBattle;
    }

    public String getArenaName() { return this.arenaName; }

    public void setArenaName(String arenaName) { this.arenaName = arenaName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return Objects.equals(playerOneSpawn, arena.playerOneSpawn) &&
                Objects.equals(playerTwoSpawn, arena.playerTwoSpawn) &&
                Objects.equals(playerOneBattle, arena.playerOneBattle) &&
                Objects.equals(playerTwoBattle, arena.playerTwoBattle);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerOneSpawn, playerTwoSpawn, playerOneBattle, playerTwoBattle);
    }

}
