package me.kaotich00.fwtournament.tournament;

import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.kit.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class Tournament {

    private String name;
    private Map<UUID, String> playersList;
    private Set<Bracket> bracketsList;
    private Kit tournamentKit;
    private ChallongeTournament challongeTournament;

    private boolean isGenerated = false;
    private boolean isStarted = false;

    public Tournament(String name) {
        this.name = name;
        this.playersList = new HashMap<>();
        this.bracketsList = new HashSet<>();
        this.tournamentKit = new Kit();
    }

    public void addPlayer(UUID uuid, String playerName) {
        playersList.put(uuid, playerName);
    }

    public void removePlayer(String playerName) { playersList.remove(playerName); }

    public void pushBracket(Bracket bracket) {
        bracketsList.add(bracket);
    }

    public String getName() {
        return name;
    }

    public Map<UUID, String> getPlayersList() {
        return playersList;
    }

    public Set<Bracket> getBracketsList() {
        return bracketsList;
    }

    public Set<Bracket> getRemainingBrackets() { return bracketsList.stream().filter(bracket -> bracket.getWinner() == null).collect(Collectors.toSet()); }

    public Kit getKit() {
        return this.tournamentKit;
    }

    public void setChallongeTournament(ChallongeTournament challongeTournament) {
        this.challongeTournament = challongeTournament;
    }

    public ChallongeTournament getChallongeTournament() {
        return this.challongeTournament;
    }

    public boolean isGenerated() {
        return isGenerated;
    }

    public void setGenerated(boolean generated) {
        isGenerated = generated;
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

    public void clearBrackets() { this.bracketsList.clear(); }

}
