package me.kaotich00.fwtournament.tournament;

import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.kit.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Tournament {

    private String name;
    private Map<String, UUID> playersList;
    private Set<Bracket> bracketsList;
    private Kit tournamentKit;
    private ChallongeTournament challongeTournament;

    public Tournament(String name) {
        this.name = name;
        this.playersList = new HashMap<>();
        this.bracketsList = new HashSet<>();
        this.tournamentKit = new Kit();
    }

    public void addPlayer(String playerName, UUID uuid) {
        playersList.put(playerName, uuid);
    }

    public void removePlayer(String playerName) { playersList.remove(playerName); }

    public void pushBracket(Bracket bracket) {
        bracketsList.add(bracket);
    }

    public String getName() {
        return name;
    }

    public Map<String, UUID> getPlayersList() {
        return playersList;
    }

    public Set<Bracket> getBracketsList() {
        return bracketsList;
    }

    public Kit getKit() {
        return this.tournamentKit;
    }

    public void setChallongeTournament(ChallongeTournament challongeTournament) {
        this.challongeTournament = challongeTournament;
    }

    public ChallongeTournament getChallongeTournament() {
        return this.challongeTournament;
    }

}
