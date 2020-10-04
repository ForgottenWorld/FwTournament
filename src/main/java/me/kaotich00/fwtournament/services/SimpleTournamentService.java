package me.kaotich00.fwtournament.services;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.kit.Kit;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

import java.util.*;

public class SimpleTournamentService {

    private static SimpleTournamentService simpleTournamentService;
    private HashMap<String, Tournament> tournamentList;
    private HashMap<UUID, Tournament> currentModifyingPlayer;
    private Set<Bracket> activeBrackets;
    private List<UUID> matchmakingQueue;

    private SimpleTournamentService() {
        if(simpleTournamentService != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
        this.tournamentList = new HashMap<>();
        this.currentModifyingPlayer = new HashMap<>();
        this.activeBrackets = new HashSet<>();
        this.matchmakingQueue = new ArrayList<>();
    }

    public static SimpleTournamentService getInstance() {
        if(simpleTournamentService == null) {
            simpleTournamentService = new SimpleTournamentService();
        }
        return simpleTournamentService;
    }

    public boolean newTournament(String name) {
        if(tournamentList.containsKey(name)) {
            return false;
        }

        Tournament tournament = new Tournament(name);
        tournamentList.put(name, tournament);

        return true;
    }

    public Optional<Tournament> getTournament(String name) {
        return tournamentList.containsKey(name) ? Optional.of(tournamentList.get(name)) : Optional.empty();
    }

    public boolean addPlayerToTournament(String tournamentName, UUID playerUUID, String playerName) {
        Tournament tournament = tournamentList.get(tournamentName);
        if(tournament.getPlayersList().containsKey(playerName)) {
            return false;
        }

        tournament.addPlayer(playerUUID, playerName);
        return true;
    }

    public boolean removePlayerFromTournament(String tournamentName, String playerName) {
        Tournament tournament = tournamentList.get(tournamentName);
        if(!tournament.getPlayersList().containsKey(playerName)) {
            return false;
        }

        tournament.removePlayer(playerName);
        return true;
    }

    public void addModifyingPlayer(UUID player, Tournament tournament) {
        this.currentModifyingPlayer.put(player, tournament);
    }

    public void removeModifyingPlayer(UUID player) {
        this.currentModifyingPlayer.remove(player);
    }

    public Tournament getTournamentByModifyingPlayer(UUID player) {
        return this.currentModifyingPlayer.get(player);
    }

    public HashMap<String, Tournament> getTournamentList() {
        return this.tournamentList;
    }

    public Bracket pushNewBracket(String challongeMatchId, Tournament tournament, String playerOne, UUID playerOneUUID, String playerTwo, UUID playerTwoUUID, String firstPlayerChallongeId, String secondPlayerChallongeId) {
        Bracket bracket = new Bracket(challongeMatchId, tournament.getName(), playerOne, firstPlayerChallongeId, playerOneUUID, playerTwo, secondPlayerChallongeId, playerTwoUUID);
        tournament.pushBracket(bracket);
        return bracket;
    }

    public List<Tournament> getStartedTournaments() {
        List<Tournament> availableTournaments = new ArrayList<>();
        for(Map.Entry<String,Tournament> entry : this.tournamentList.entrySet()) {
            Tournament tournament = entry.getValue();
            if(tournament.isStarted()) {
                availableTournaments.add(tournament);
            }
        }
        return availableTournaments;
    }

    public void startBracket(Bracket bracket) {
        this.activeBrackets.add(bracket);
    }

    public void stopBracket(Bracket bracket) { this.activeBrackets.remove(bracket); }

    public Set<Bracket> getActiveBrackets() {
        return this.activeBrackets;
    }

    public boolean isInMatchmaking(UUID player) {
        return this.matchmakingQueue.contains(player);
    }

    public void addToMatchmaking(UUID player) {
        this.matchmakingQueue.add(player);
    }

    public void removeFromMatchmaking(UUID player) {
        this.matchmakingQueue.remove(player);
    }

    public void checkForNewMatchmakings() {
        HashMap<String, Tournament> tournamentsList = SimpleTournamentService.getInstance().getTournamentList();
        for(Map.Entry<String,Tournament> entry : tournamentsList.entrySet()) {
            Tournament tournament = entry.getValue();
            for(UUID playerUUID : tournament.getPlayersList().keySet()) {
                if(Bukkit.getPlayer(playerUUID) != null) {
                    checkMatchmakingStatus(Bukkit.getPlayer(playerUUID));
                }
            }
        }
    }

    public void checkMatchmakingStatus(Player player) {
        // Check if there is any started tournament
        List<Tournament> availableTournaments = SimpleTournamentService.getInstance().getStartedTournaments();

        if(availableTournaments.isEmpty()) {
            player.sendMessage(ChatFormatter.formatSuccessMessage("Hi, there are no tournament available at the moment. Gamemode set to spectator mode."));
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        // Check if the player is in started tournament
        Tournament playerTournament = null;
        for(Tournament tournament: availableTournaments) {
            if(tournament.getPlayersList().containsKey(player.getUniqueId())) {
                playerTournament = tournament;
            }
        }

        // If it is not, put player in spectator mode
        if(playerTournament == null) {
            player.sendMessage(ChatFormatter.formatSuccessMessage("Hi, it seems like you don't belong to any tournament. Gamemode set to spectator mode."));
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        // Check if player is in any open match
        Bracket playerBracket = null;
        for(Bracket bracket: playerTournament.getRemainingBrackets()) {
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
        if(SimpleTournamentService.getInstance().getActiveBrackets().contains(playerBracket)) {
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

        Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("The math between " + playerBracket.getFirstPlayerName() + " and " + playerBracket.getSecondPlayerName() + " will start in 10 seconds"));

        Bracket finalPlayerBracket = playerBracket;
        Arena finalFreeArena = freeArena;
        Tournament finalPlayerTournament = playerTournament;
        Bukkit.getScheduler().scheduleSyncDelayedTask(Fwtournament.getPlugin(Fwtournament.class), () -> {

            Player firstPlayer = Bukkit.getPlayer(finalPlayerBracket.getFirstPlayerUUID());
            Player secondPlayer = Bukkit.getPlayer(finalPlayerBracket.getSecondPlayerUUID());

            firstPlayer.teleport(finalFreeArena.getPlayerOneSpawn());
            secondPlayer.teleport(finalFreeArena.getPlayerTwoSpawn());

            Kit playersKit = finalPlayerTournament.getKit();

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

            // Add bracket as active
            SimpleTournamentService.getInstance().startBracket(finalPlayerBracket);

            // Run battle timer
            finalPlayerTournament.startBattleTimer(finalFreeArena, finalPlayerBracket);

        }, 200L);
    }

    public void checkTournamentDeath(Player player) throws ParseException {

        for(Tournament tournament : SimpleTournamentService.getInstance().getStartedTournaments()) {
            for(Bracket bracket: tournament.getBracketsList()) {
                SimpleTournamentService.getInstance().startBracket(bracket);
            }
        }

        Set<Bracket> activeBrackets = SimpleTournamentService.getInstance().getActiveBrackets();

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
                Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("The winner of the match is " + Bukkit.getServer().getPlayer(bracket.getWinner()).getName()));

                Tournament tournament = SimpleTournamentService.getInstance().getTournament(bracket.getTournamentName()).get();
                Set<Bracket> remainingBrackets = tournament.getRemainingBrackets();

                ChallongeIntegrationFactory.updateMatchResult(player, tournament, bracket);
                SimpleTournamentService.getInstance().startBracket(bracket);

                // This means every bracket has a winner.
                // Therefore new brackets need to be
                // generated
                if(remainingBrackets.isEmpty()) {
                    ChallongeIntegrationFactory.getTournamentBrackets(null, tournament);
                } else {
                    checkForNewMatchmakings();
                }
            } else {
                continue;
            }
        }
    }

}
