package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleMojangApiService;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AddCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        if(simpleTournamentService.getTournament().isPresent()) {
            Tournament tournament = simpleTournamentService.getTournament().get();

            if(tournament.isGenerated()) {
                Message.TOURNAMENT_ALREADY_GENERATED.send((Player) sender);
                return;
            }

            String playersName = args[1];
            List<String> playersList = Arrays.asList(playersName.split(","));

            CompletableFuture.runAsync(() -> {
                for(String playerName : playersList) {
                    Message.TOURNAMENT_ADD_PLAYER_VALIDATING.send(sender, playerName);

                    UUID playerUUID = SimpleMojangApiService.getInstance().getPlayerUUID(playerName);
                    if(playerUUID == null) {
                        Message.TOURNAMENT_ADD_PLAYER_DOES_NOT_EXIST.send(sender, playerName);
                    }

                    if (simpleTournamentService.addPlayerToTournament(playerUUID, playerName)) {
                        Message.TOURNAMENT_ADD_PLAYER_SUCCESS.send(sender, playerName);
                    } else {
                        Message.TOURNAMENT_ADD_PLAYER_ALREADY_PARTICIPANT.send(sender, playerName);
                    }
                }
            });

        }

    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/torneo add <playerlist>";
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
