package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.util.concurrent.CompletableFuture;

public class GenerateCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        if(simpleTournamentService.getTournament().isPresent()) {
            Tournament tournament = simpleTournamentService.getTournament().get();

            if(tournament.isGenerated()) {
                Message.TOURNAMENT_ALREADY_GENERATED.send(sender);
                return;
            }

            String challongeTournamentName = tournament.getName();
            String challongeTournamentDescription = "PvP Tournament";
            String challongeTournamentType = "single elimination";
            String challongeOpenSignup = "false";
            String challongeTournamentLink = Fwtournament.getDefaultConfig().getString("challonge_tournament_prefix") + "_" + tournament.getName();

            Message.TOURNAMENT_GENERATING.send(sender);
            CompletableFuture.supplyAsync(() -> {
                ChallongeTournament challongeTournament = null;
                try {
                    challongeTournament = ChallongeIntegrationFactory.createTournament((Player) sender, challongeTournamentName, challongeTournamentDescription, challongeTournamentType, challongeOpenSignup, challongeTournamentLink);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return challongeTournament;
            }).thenAccept(challongeTournament -> {
                if (challongeTournament == null) {
                    Message.TOURNAMENT_GENERATING_ERROR.send(sender);
                    return;
                }

                Message.TOURNAMENT_GENERATING_SUCCESS.send(sender, challongeTournament.getChallongeLink());
                tournament.setChallongeTournament(challongeTournament);

                Message.TOURNAMENT_ADDING_PARTICIPANTS.send(sender);
                CompletableFuture.supplyAsync(() -> {
                    try {
                        ChallongeIntegrationFactory.addParticipantsToTournament((Player) sender, tournament);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
                }).thenAccept(result -> {
                    Message.TOURNAMENT_ADDING_PARTICIPANTS_SUCCESS.send(sender);
                    tournament.setGenerated(true);

                    Message.TOURNAMENT_RANDOMIZING.send(sender);
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            ChallongeIntegrationFactory.randomizeBrackets((Player) sender, tournament);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }).thenAccept(randomizeResult -> {
                        Message.TOURNAMENT_RANDOMIZE_SUCCESS.send(sender);
                    });
                });
            });
        }

    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/torneo generate";
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public Integer getRequiredArgs() {
        return 1;
    }

}
