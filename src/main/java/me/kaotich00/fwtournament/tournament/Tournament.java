package me.kaotich00.fwtournament.tournament;

import com.destroystokyo.paper.Title;
import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.kit.Kit;
import me.kaotich00.fwtournament.tournament.task.BattleInitTimer;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Tournament {

    private String name;
    private Map<UUID, String> playersList;
    private HashMap<String, Bracket> bracketsList;
    private HashMap<String, Bracket> activeBrackets;
    private Kit tournamentKit;
    private ChallongeTournament challongeTournament;

    private int currentRound;

    private boolean isGenerated = false;
    private boolean isStarted = false;

    public Tournament(String name) {
        this.name = name;
        this.playersList = new HashMap<>();
        this.bracketsList = new HashMap<>();
        this.tournamentKit = new Kit();
        this.activeBrackets = new HashMap<>();
        this.currentRound = 0;
    }

    public void addPlayer(UUID uuid, String playerName) {
        playersList.put(uuid, playerName);
    }

    public void removePlayer(UUID playerUUID) { playersList.remove(playerUUID); }

    public void pushBracket(Bracket bracket) {
        bracketsList.put(bracket.getChallongeMatchId(), bracket);
    }

    public String getName() {
        return name;
    }

    public Map<UUID, String> getPlayersList() {
        return playersList;
    }

    public Set<Bracket> getBracketsList() {
        return new HashSet<>(bracketsList.values());
    }

    public Set<Bracket> getRemainingBrackets() { return bracketsList.values().stream().filter(bracket -> bracket.getWinner() == null).collect(Collectors.toSet()); }

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

    public void startBattleTimer(Arena arena, Bracket bracket) {
        Player playerOne = Bukkit.getPlayer(bracket.getFirstPlayerUUID());
        Player playerTwo = Bukkit.getPlayer(bracket.getSecondPlayerUUID());

        BattleInitTimer timer = new BattleInitTimer(Fwtournament.getPlugin(Fwtournament.class),
                30,
                () -> Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("The match between " + bracket.getFirstPlayerName() + " and " + bracket.getSecondPlayerName() + " will began in 30 seconds")),
                () -> {
                    assert playerOne != null && playerTwo != null;

                    playerOne.sendTitle(new Title(ChatFormatter.formatSuccessMessage("Go!"), "", 1, 18, 1));
                    playerTwo.sendTitle(new Title(ChatFormatter.formatSuccessMessage("Go!"), "", 1, 18, 1));

                    playerOne.teleport(arena.getPlayerOneBattle());
                    playerTwo.teleport(arena.getPlayerTwoBattle());
                },
                (t) -> {
                    if( t.getSecondsLeft() <= 5) {
                        playerOne.sendTitle(new Title(ChatFormatter.formatSuccessMessage(String.valueOf(t.getSecondsLeft())), "", 1, 18, 1));
                        playerTwo.sendTitle(new Title(ChatFormatter.formatSuccessMessage(String.valueOf(t.getSecondsLeft())), "", 1, 18, 1));
                    }
                });
        timer.scheduleTimer();
    }

    public void startBracket(Bracket bracket) {
        this.activeBrackets.put(bracket.getChallongeMatchId(), bracket);
    }

    public void stopBracket(Bracket bracket) { this.activeBrackets.remove(bracket.getChallongeMatchId()); }

    public Set<Bracket> getActiveBrackets() {
        return new HashSet<>(this.activeBrackets.values());
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }

}
