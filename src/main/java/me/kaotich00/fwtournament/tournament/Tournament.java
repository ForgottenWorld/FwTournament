package me.kaotich00.fwtournament.tournament;

import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.kit.Kit;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Tournament {

    private String name;
    private Set<UUID> playersList;
    private Set<Bracket> bracketsList;
    private Kit tournamentKit;

    public Tournament(String name) {
        this.name = name;
        this.playersList = new HashSet<>();
        this.bracketsList = new HashSet<>();
        this.tournamentKit = new Kit();
    }

    public void addPlayer(UUID uuid) {
        playersList.add(uuid);
    }

    public void removePlayer(UUID uuid) { playersList.remove(uuid); }

    public void pushBracket(Bracket bracket) {
        bracketsList.add(bracket);
    }

    public String getName() {
        return name;
    }

    public Set<UUID> getPlayersList() {
        return playersList;
    }

    public Set<Bracket> getBracketsList() {
        return bracketsList;
    }

    public Kit getKit() {
        return this.tournamentKit;
    }

}
