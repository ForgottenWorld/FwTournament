package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.challonge.objects.ChallongeTournament;
import me.kaotich00.fwtournament.command.api.AdminCommand;
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
                sender.sendMessage(ChatFormatter.formatErrorMessage("The tournament is already generated!"));
                return;
            }

            String challongeTournamentName = tournament.getName();
            String challongeTournamentDescription = "PvP Tournament";
            String challongeTournamentType = "single elimination";
            String challongeOpenSignup = "false";
            String challongeTournamentLink = Fwtournament.getDefaultConfig().getString("challonge_tournament_prefix") + "_" + tournament.getName();

            sender.sendMessage(ChatFormatter.formatSuccessMessage("Generating Challonge tournament..."));
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
                    sender.sendMessage(ChatFormatter.formatErrorMessage("Error while generating tournament. Maybe it is already existent."));
                    return;
                }

                sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully generated tournament at link: " + challongeTournament.getChallongeLink()));
                tournament.setChallongeTournament(challongeTournament);

                sender.sendMessage(ChatFormatter.formatSuccessMessage("Adding participants to Challonge tournament..."));
                CompletableFuture.supplyAsync(() -> {
                    try {
                        ChallongeIntegrationFactory.addParticipantsToTournament((Player) sender, tournament);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
                }).thenAccept(result -> {
                    sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully added participants to the tournament"));
                    tournament.setGenerated(true);
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
