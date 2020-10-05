package me.kaotich00.fwtournament.services;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.kit.Kit;
import me.kaotich00.fwtournament.storage.sqlite.SQLiteConnectionService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SimpleTournamentService {

    private static SimpleTournamentService simpleTournamentService;
    private Tournament currentTournament;

    private SimpleTournamentService() {
        if(simpleTournamentService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static SimpleTournamentService getInstance() {
        if(simpleTournamentService == null) {
            simpleTournamentService = new SimpleTournamentService();
        }
        return simpleTournamentService;
    }

    public boolean newTournament(String name) {
        if(currentTournament != null) {
            return false;
        }

        this.currentTournament = new Tournament(name);

        return true;
    }

    public Optional<Tournament> getTournament() {
        return Optional.ofNullable(this.currentTournament);
    }

    public boolean addPlayerToTournament(UUID playerUUID, String playerName) {
        if(this.currentTournament.getPlayersList().containsKey(playerUUID)) {
            return false;
        }

        this.currentTournament.addPlayer(playerUUID, playerName);
        return true;
    }

    public boolean removePlayerFromTournament(UUID playerUUID) {
        if(!this.currentTournament.getPlayersList().containsKey(playerUUID)) {
            return false;
        }

        this.currentTournament.removePlayer(playerUUID);
        return true;
    }

    public Bracket pushNewBracket(String challongeMatchId, Tournament tournament, String playerOne, UUID playerOneUUID, String playerTwo, UUID playerTwoUUID, String firstPlayerChallongeId, String secondPlayerChallongeId) {
        Bracket bracket = new Bracket(challongeMatchId, tournament.getName(), playerOne, firstPlayerChallongeId, playerOneUUID, playerTwo, secondPlayerChallongeId, playerTwoUUID);
        tournament.pushBracket(bracket);
        return bracket;
    }

    public void checkForNewMatchmakings() {
        Tournament tournament = currentTournament;
        
        if(tournament == null) {
            return;
        }
        
        for(Bracket bracket : tournament.getBracketsList()) {
            if(bracket.getWinner() != null) {
                continue;
            }

            UUID firstPlayerUUID = bracket.getFirstPlayerUUID();
            UUID secondPlayerUUID = bracket.getSecondPlayerUUID();

            if(Bukkit.getPlayer(firstPlayerUUID) != null) {
                checkMatchmakingStatus(Objects.requireNonNull(Bukkit.getPlayer(firstPlayerUUID)));
                continue;
            }

            if(Bukkit.getPlayer(secondPlayerUUID) != null) {
                checkMatchmakingStatus(Objects.requireNonNull(Bukkit.getPlayer(secondPlayerUUID)));
            }
        }
    }

    public void checkMatchmakingStatus(Player player) {

        Tournament tournament = currentTournament;

        if(tournament == null) {
            return;
        }

        // If it is not, put player in spectator mode
        if(!tournament.getPlayersList().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatFormatter.formatSuccessMessage("Hi, you are not part of the tournament. Setting gamemode to spectator."));
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        // Check if player is in any open match
        Bracket playerBracket = null;
        for(Bracket bracket: tournament.getRemainingBrackets()) {
            if(bracket.getFirstPlayerUUID().equals(player.getUniqueId()) || bracket.getSecondPlayerUUID().equals(player.getUniqueId())) {
                playerBracket = bracket;
            }
        }

        // If it is not, put player in spectator mode
        if(playerBracket == null) {
            player.sendMessage(ChatFormatter.formatSuccessMessage("Hi, you are currently part of a tournament, but no match is open at the moment. Gamemode set to spectator mode."));
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        // if player is in a running match, skip
        if(tournament.getActiveBrackets().contains(playerBracket)) {
            return;
        }

        // Check if the opponent is online
        if(Bukkit.getPlayer(playerBracket.getFirstPlayerUUID()) == null || Bukkit.getPlayer(playerBracket.getSecondPlayerUUID()) == null) {
            player.sendMessage(ChatFormatter.formatSuccessMessage("Hi, your match will begin as soon as your opponent comes online. Be patient. Gamemode set to spectator mode."));
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        // Check if there is any free arena
        HashMap<String, Arena> arenas = SimpleArenaService.getInstance().getArenas();
        Arena freeArena = null;
        for(Map.Entry<String,Arena> entry: arenas.entrySet()) {
            Arena arena = entry.getValue();
            if(!arena.isOccupied()) {
                freeArena = arena;
            }
        }

        if(freeArena == null) {
            player.sendMessage(ChatFormatter.formatSuccessMessage("Hi, you and your opponent are ready to play. Unfortunately there is not a free arena at the moment. Be patient. Gamemode set to spectator mode."));
            return;
        }

        Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("The math between " + playerBracket.getFirstPlayerName() + " and " + playerBracket.getSecondPlayerName() + " has been detected. Teleporting players in 10 seconds."));

        Bracket finalPlayerBracket = playerBracket;
        Arena finalFreeArena = freeArena;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Fwtournament.getPlugin(Fwtournament.class), () -> {

            Player firstPlayer = Bukkit.getPlayer(finalPlayerBracket.getFirstPlayerUUID());
            Player secondPlayer = Bukkit.getPlayer(finalPlayerBracket.getSecondPlayerUUID());

            assert firstPlayer != null && secondPlayer != null;
            firstPlayer.teleport(finalFreeArena.getPlayerOneSpawn());
            secondPlayer.teleport(finalFreeArena.getPlayerTwoSpawn());

            Kit playersKit = tournament.getKit();

            firstPlayer.getInventory().clear();
            secondPlayer.getInventory().clear();

            firstPlayer.setGameMode(GameMode.SURVIVAL);
            secondPlayer.setGameMode(GameMode.SURVIVAL);

            for(ItemStack itemStack: playersKit.getItemsList()) {
                firstPlayer.getInventory().addItem(itemStack);
                secondPlayer.getInventory().addItem(itemStack);
            }

            // Set the arena as occupied
            finalFreeArena.setOccupied(true);
            SimpleArenaService.getInstance().addToOccupiedArenas(finalPlayerBracket, finalFreeArena);

            // Add bracket as active
            tournament.startBracket(finalPlayerBracket);

            // Run battle timer
            tournament.startBattleTimer(finalFreeArena, finalPlayerBracket);

        }, 200L);
    }

    public void checkTournamentDeath(Player player) throws ExecutionException, InterruptedException {

        Set<Bracket> activeBrackets = currentTournament.getActiveBrackets();

        if(activeBrackets.isEmpty()) {
            return;
        }

        for(Bracket bracket: activeBrackets) {
            if(bracket.getFirstPlayerUUID().equals(player.getUniqueId())) {
                bracket.setWinner(bracket.getSecondPlayerUUID());
                bracket.setWinnerChallongeId(bracket.getSecondPlayerChallongeId());
            }
            if(bracket.getSecondPlayerUUID().equals(player.getUniqueId())) {
                bracket.setWinner(bracket.getFirstPlayerUUID());
                bracket.setWinnerChallongeId(bracket.getFirstPlayerChallongeId());
            }

            if(bracket.getWinner() != null) {
                Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("The winner of the match is " + Objects.requireNonNull(Bukkit.getServer().getPlayer(bracket.getWinner())).getName()));

                Tournament tournament = SimpleTournamentService.getInstance().getTournament().get();
                Set<Bracket> remainingBrackets = tournament.getRemainingBrackets();

                CompletableFuture.supplyAsync(() -> {
                    try {
                        ChallongeIntegrationFactory.updateMatchResult(player, tournament, bracket);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
                }).thenAccept(result -> {
                    HashMap<Bracket,Arena> occupiedArenas = SimpleArenaService.getInstance().getOccupiedArenas();
                    Arena occupiedArena = occupiedArenas.get(bracket);
                    occupiedArena.setOccupied(false);

                    // This means every bracket has a winner.
                    // Therefore new brackets need to be
                    // generated
                    if (remainingBrackets.isEmpty()) {
                        Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("Tournament round is over"));
                        CompletableFuture.supplyAsync(() -> {
                            boolean isTournamentEnded = false;
                            try {
                                isTournamentEnded = ChallongeIntegrationFactory.getTournamentBrackets(null, tournament);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            return isTournamentEnded;
                        }).thenAccept(isTournamentEnded -> {
                            if(isTournamentEnded) {
                                Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("Congratulation to the winner of the tournament: " + Objects.requireNonNull(Bukkit.getServer().getPlayer(bracket.getWinner())).getName()));
                            }
                        });
                    } else {
                        checkForNewMatchmakings();
                    }
                });
            }
        }
    }

    public void endTournament(Tournament tournament) {
        currentTournament = null;

        SQLiteConnectionService.getInstance().deleteTournament(Fwtournament.getPlugin(Fwtournament.class), "fwtournament", tournament);
    }

}
