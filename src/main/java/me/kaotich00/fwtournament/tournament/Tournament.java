package me.kaotich00.fwtournament.tournament;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Tournament {

    private String name;
    private Map<UUID, String> playersList;
    private Set<Bracket> bracketsList;
    private Kit tournamentKit;
    private ChallongeTournament challongeTournament;

    private boolean isGenerated = false;
    private boolean isStarted = false;

    private int battleTimerTaskId;

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

    public void startBattleTimer(Arena arena, Bracket bracket) {
        Player playerOne = Bukkit.getPlayer(bracket.getFirstPlayerUUID());
        Player playerTwo = Bukkit.getPlayer(bracket.getSecondPlayerUUID());

        BattleInitTimer timer = new BattleInitTimer(Fwtournament.getPlugin(Fwtournament.class),
                60,
                () -> {
                    Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("The match between " + bracket.getFirstPlayerName() + " and " + bracket.getSecondPlayerName() + " will began in 60 seconds"));
                },
                () -> {
                    playerOne.sendMessage(ChatFormatter.formatSuccessMessage("Go!"));
                    playerTwo.sendMessage(ChatFormatter.formatSuccessMessage("Go!"));

                    playerOne.teleport(arena.getPlayerOneBattle());
                    playerTwo.teleport(arena.getPlayerTwoBattle());
                },
                (t) -> {
                    if( t.getSecondsLeft() <= 5) {
                        playerOne.sendMessage(ChatFormatter.formatSuccessMessage(String.valueOf(t.getSecondsLeft())));
                        playerTwo.sendMessage(ChatFormatter.formatSuccessMessage(String.valueOf(t.getSecondsLeft())));
                    }
                });
        timer.scheduleTimer();
    }

}
