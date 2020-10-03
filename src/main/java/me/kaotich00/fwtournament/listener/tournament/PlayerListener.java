package me.kaotich00.fwtournament.listener.tournament;

import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.kit.Kit;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoinListener(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Check if there is any started tournament
        List<Tournament> availableTournaments = SimpleTournamentService.getInstance().getStartedTournaments();

        if(availableTournaments.isEmpty()) {
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
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }

        // Check if the opponent is online
        if(Bukkit.getPlayer(playerBracket.getFirstPlayerUUID()) == null || Bukkit.getPlayer(playerBracket.getSecondPlayerUUID()) == null) {
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
            return;
        }

        // If so, teleport both players and give them the kit
        Player firstPlayer = Bukkit.getPlayer(playerBracket.getFirstPlayerUUID());
        Player secondPlayer = Bukkit.getPlayer(playerBracket.getSecondPlayerUUID());

        firstPlayer.teleport(freeArena.getPlayerOneSpawn());
        secondPlayer.teleport(freeArena.getPlayerTwoSpawn());

        Kit playersKit = playerTournament.getKit();

        firstPlayer.getInventory().clear();
        secondPlayer.getInventory().clear();

        firstPlayer.setGameMode(GameMode.SURVIVAL);
        secondPlayer.setGameMode(GameMode.SURVIVAL);

        for(ItemStack itemStack: playersKit.getItemsList()) {
            firstPlayer.getInventory().addItem(itemStack);
            secondPlayer.getInventory().addItem(itemStack);
        }

        // Set the arena as occupied
        freeArena.setOccupied(true);

        // Add bracket as active
        SimpleTournamentService.getInstance().startBracket(playerBracket);

        // Run battle timer
        playerTournament.startBattleTimer(freeArena, playerBracket);
    }

    @EventHandler
    public void onPlayerDeathListener(PlayerDeathEvent event) throws ParseException {
        Player player = event.getEntity();

        if(!(event.getEntity().getKiller() instanceof Player)) {
            return;
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
                //Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage("The winner is " + Bukkit.getServer().getPlayer(bracket.getWinner()).getName()));
                //Bukkit.getServer().broadcastMessage(ChatFormatter.formatSuccessMessage(Bukkit.getServer().getPlayer(bracket.getWinner()).getName() + " qualified for next turn"));
            } else {
                continue;
            }

            Tournament tournament = SimpleTournamentService.getInstance().getTournament(bracket.getTournamentName()).get();
            Set<Bracket> remainingBrackets = tournament.getRemainingBrackets();

            ChallongeIntegrationFactory.updateMatchResult(player, tournament, bracket);

            // This means every bracket has a winner.
            // Therefore new brackets need to be
            // generated
            if(remainingBrackets.isEmpty()) {
                ChallongeIntegrationFactory.getTournamentBrackets(null, tournament);
            }
        }
    }

}
