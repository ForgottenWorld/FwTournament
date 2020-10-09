package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import me.kaotich00.fwtournament.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class SkipCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String playerName = args[1];

        Optional<Tournament> optTournament = SimpleTournamentService.getInstance().getTournament();
        Bracket selectedBracket = null;
        UUID selectedPlayerUUID = null;
        String selectedPlayerChallongeId = null;
        if(optTournament.isPresent()) {
            Tournament tournament = optTournament.get();
            for(Bracket tournamentBracket: tournament.getBracketsList()) {
                if(tournamentBracket.getFirstPlayerName().equals(playerName)) {
                    selectedPlayerUUID = tournamentBracket.getFirstPlayerUUID();
                    selectedPlayerChallongeId = tournamentBracket.getFirstPlayerChallongeId();
                    selectedBracket = tournamentBracket;
                    tournament.removePlayer(selectedBracket.getSecondPlayerUUID());
                }

                if(tournamentBracket.getSecondPlayerName().equals(playerName)) {
                    selectedPlayerUUID = tournamentBracket.getSecondPlayerUUID();
                    selectedPlayerChallongeId = tournamentBracket.getSecondPlayerChallongeId();
                    selectedBracket = tournamentBracket;
                    tournament.removePlayer(selectedBracket.getFirstPlayerUUID());
                }
            }

            if(selectedBracket != null) {
                selectedBracket.setWinner(selectedPlayerUUID);
                selectedBracket.setWinnerChallongeId(selectedPlayerChallongeId);

                Bukkit.getServer().broadcastMessage(ChatFormatter.pluginPrefix() + ChatFormatter.formatSuccessMessage("The player ") +
                        ChatFormatter.parseColorMessage(Objects.requireNonNull(Bukkit.getServer().getPlayer(selectedBracket.getWinner())).getName(), ColorUtil.colorSub2) +
                        ChatFormatter.formatSuccessMessage(" won by forfeit!"));

                Player firstPlayer = Bukkit.getPlayer(selectedBracket.getFirstPlayerUUID());
                if(firstPlayer != null) {
                    firstPlayer.setGameMode(GameMode.SPECTATOR);
                }
                Player secondPlayer = Bukkit.getPlayer(selectedBracket.getSecondPlayerUUID());
                if(secondPlayer != null) {
                    secondPlayer.setGameMode(GameMode.SPECTATOR);
                }

                tournament.stopBracket(selectedBracket);

                Set<Bracket> remainingBrackets = tournament.getRemainingBrackets();

                Bracket finalSelectedBracket = selectedBracket;
                CompletableFuture.supplyAsync(() -> {
                    try {
                        ChallongeIntegrationFactory.updateMatchResult(null, tournament, finalSelectedBracket);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
                }).thenAccept(result -> {
                    String occupiedArenaName = finalSelectedBracket.getOccupiedArenaName();
                    Optional<Arena> optOccupiedArena = SimpleArenaService.getInstance().getArena(occupiedArenaName);
                    if(optOccupiedArena.isPresent()) {
                        Arena occupiedArena = optOccupiedArena.get();
                        occupiedArena.setOccupied(false);
                    }

                    // This means every bracket has a winner.
                    // Therefore new brackets need to be
                    // generated
                    if (remainingBrackets.isEmpty()) {
                        Bukkit.getServer().broadcastMessage(ChatFormatter.pluginPrefix() + ChatFormatter.formatSuccessMessage("Round " + tournament.getCurrentRound() + " is over!"));
                        SimpleTournamentService.getInstance().refreshTournamentBrackets();
                    } else {
                        SimpleTournamentService.getInstance().checkForNewMatchmakings();
                    }
                });
            } else {
                Message.BRACKET_OR_PLAYER_NOT_FOUND.send(sender);
            }
        }
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/torneo skip <nickname>";
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Integer getRequiredArgs() {
        return 2;
    }

}
