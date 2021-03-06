package me.kaotich00.fwtournament.tournament;

import com.destroystokyo.paper.Title;
import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.kit.Kit;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import me.kaotich00.fwtournament.services.SimpleMojangApiService;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.task.BattleInitTimer;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import me.kaotich00.fwtournament.utils.ColorUtil;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.awt.*;
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

        String bossBarName = "fwtournament." + bracket.getChallongeMatchId();
        BossBar bossBar = Bukkit.getServer().createBossBar(
                NamespacedKey.minecraft(bossBarName),
                ChatFormatter.parseColorMessage("The match will began in 30 seconds", ColorUtil.colorSecondary),
                BarColor.RED,
                BarStyle.SEGMENTED_10
        );
        bossBar.setProgress(1.0);
        bossBar.addPlayer(playerOne);
        bossBar.addPlayer(playerTwo);

        BattleInitTimer timer = new BattleInitTimer(Fwtournament.getPlugin(Fwtournament.class),
                30,
                () -> {

                    String matchMessage = ChatFormatter.pluginPrefix() +
                            ChatFormatter.formatSuccessMessage("The match between ") +
                            ChatFormatter.parseColorMessage(bracket.getFirstPlayerName(), ColorUtil.colorSub2) +
                            ChatFormatter.formatSuccessMessage(" and ") +
                            ChatFormatter.parseColorMessage(bracket.getSecondPlayerName(), ColorUtil.colorSub2) +
                            ChatFormatter.formatSuccessMessage(" will began in ") +
                            ChatFormatter.parseColorMessage("30 ", ColorUtil.colorSub2) +
                            ChatFormatter.formatSuccessMessage("seconds");
                    Bukkit.getServer().broadcastMessage(ChatFormatter.chatFillerTop());
                    Bukkit.getServer().broadcastMessage(matchMessage);

                    TextComponent clickMessage = new TextComponent("[ CLICK HERE TO TELEPORT TO THE ARENA ]");
                    clickMessage.setColor(ChatColor.GREEN);
                    clickMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/arena join " + arena.getArenaName()   ));
                    clickMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to teleport").color(ChatColor.GREEN).italic(true).create()));

                    ComponentBuilder message = new ComponentBuilder();
                    message
                            .append("[").color(ChatColor.of(ColorUtil.colorSecondary))
                            .append("FwTournament").color(ChatColor.of(ColorUtil.colorSub1))
                            .append("] ").color(ChatColor.of(ColorUtil.colorSecondary))
                            .append(clickMessage);

                    for(Player player: Bukkit.getOnlinePlayers()) {
                        boolean canSend = true;
                        for(Bracket b: this.activeBrackets.values()) {
                            if(b.getFirstPlayerUUID().equals(player.getUniqueId()) || b.getSecondPlayerUUID().equals(player.getUniqueId())) {
                                canSend = false;
                            }
                        }
                        if(canSend) {
                            player.spigot().sendMessage(message.create());
                        }
                    }

                    Bukkit.getServer().broadcastMessage(ChatFormatter.chatFillerBottom());
                },
                () -> {
                    bossBar.removePlayer(playerOne);
                    bossBar.removePlayer(playerTwo);
                    Bukkit.getServer().removeBossBar(NamespacedKey.minecraft(bossBarName));

                    Player assertPlayerOne = Bukkit.getPlayer(playerOne.getUniqueId());
                    Player assertPlayerTwo = Bukkit.getPlayer(playerTwo.getUniqueId());

                    if( assertPlayerOne == null || assertPlayerTwo == null ) {
                        String occupiedArenaName = bracket.getOccupiedArenaName();
                        Optional<Arena> optOccupiedArena = SimpleArenaService.getInstance().getArena(occupiedArenaName);
                        if(optOccupiedArena.isPresent()) {
                            Arena occupiedArena = optOccupiedArena.get();
                            occupiedArena.setOccupied(false);
                        }

                        Tournament tournament = SimpleTournamentService.getInstance().getTournament().get();
                        tournament.stopBracket(bracket);
                    }

                    if(assertPlayerOne == null) {
                        playerTwo.setGameMode(GameMode.SPECTATOR);
                        Message.PLAYER_DISCONNECTED_DURING_LOADING.send(playerTwo);
                        return;
                    }

                    if(assertPlayerTwo == null) {
                        playerOne.setGameMode(GameMode.SPECTATOR);
                        Message.PLAYER_DISCONNECTED_DURING_LOADING.send(playerOne);
                        return;
                    }

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

                    bossBar.setTitle(ChatFormatter.parseColorMessage("The match will began in " + t.getSecondsLeft() + " seconds", ColorUtil.colorSecondary));
                    double progress = bossBar.getProgress() - 0.03 < 0.0 ? 0.0 : bossBar.getProgress() - 0.03;
                    bossBar.setProgress(progress);
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

    public void fixPlayerName(Bracket bracket, String playerChallongeId, String newName, UUID newUUID) {
        if(bracket.getFirstPlayerChallongeId().equals(playerChallongeId)) {
            bracket.setFirstPlayerName(newName);
            bracket.setFirstPlayerUUID(newUUID);
        }
        if(bracket.getSecondPlayerChallongeId().equals(playerChallongeId)) {
            bracket.setSecondPlayerName(newName);
            bracket.setSecondPlayerUUID(newUUID);
        }
        this.bracketsList.put(bracket.getChallongeMatchId(), bracket);
    }

}
