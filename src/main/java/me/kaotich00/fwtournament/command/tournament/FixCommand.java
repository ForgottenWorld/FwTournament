package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.bracket.Bracket;
import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleMojangApiService;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class FixCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String oldName = args[1];
        String newName = args[2];

        Optional<Tournament> optTournament = SimpleTournamentService.getInstance().getTournament();
        if(optTournament.isPresent()) {
            Tournament tournament = optTournament.get();
            if(!tournament.getPlayersList().values().contains(oldName)) {
                Message.PLAYER_NOT_FOUND.send(sender);
                return;
            }

            CompletableFuture.supplyAsync(() -> {
                Message.TOURNAMENT_ADD_PLAYER_VALIDATING.send(sender, newName);

                UUID playerUUID = SimpleMojangApiService.getInstance().getPlayerUUID(newName);
                if(playerUUID == null) {
                    Message.TOURNAMENT_ADD_PLAYER_DOES_NOT_EXIST.send(sender, newName);
                }

                return playerUUID;
            }).thenAccept(uuid -> {
                String playerChallongeId = null;
                Bracket incriminatedBracket = null;
                for(Bracket bracket: tournament.getBracketsList()) {
                    if(bracket.getFirstPlayerName().equals(oldName)) {
                        playerChallongeId = bracket.getFirstPlayerChallongeId();
                        incriminatedBracket = bracket;
                    }
                    if(bracket.getSecondPlayerName().equals(oldName)) {
                        playerChallongeId = bracket.getSecondPlayerChallongeId();
                        incriminatedBracket = bracket;
                    }
                }
                if(playerChallongeId != null) {
                    String finalPlayerChallongeId = playerChallongeId;
                    Bracket finalIncriminatedBracket = incriminatedBracket;
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            ChallongeIntegrationFactory.fixPlayerName((Player) sender, tournament, finalPlayerChallongeId, newName);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }).thenAccept(result -> {
                        Message.FIXED_PLAYER_NAME.send(sender, oldName, newName);
                        tournament.fixPlayerName(finalIncriminatedBracket, finalPlayerChallongeId, newName, uuid);
                    });
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
        return "/torneo fix <oldName> <newName>";
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Integer getRequiredArgs() {
        return 3;
    }

}
