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
import java.util.concurrent.ExecutionException;

public class GenerateCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String tournamentName = args[1];

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        if(simpleTournamentService.getTournament(tournamentName).isPresent()) {
            Tournament tournament = simpleTournamentService.getTournament(tournamentName).get();

            if(tournament.isGenerated()) {
                sender.sendMessage(ChatFormatter.formatErrorMessage("Tournament already generated!"));
                return;
            }

            try {
                if(tournament.getChallongeTournament() != null) {
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                        try {
                            ChallongeIntegrationFactory.getTournamentBrackets((Player) sender, tournament);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    });
                    completableFuture.get();
                } else {
                    String challongeTournamentName = tournament.getName();
                    String challongeTournamentDescription = "test descrizione";
                    String challongeTournamentType = "single elimination";
                    String challongeOpenSignup = "false";
                    String challongeTournamentLink = Fwtournament.getDefaultConfig().getString("challonge_tournament_prefix") + "_" + tournament.getName();

                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                        ChallongeTournament challongeTournament = null;
                        try {
                            challongeTournament = ChallongeIntegrationFactory.createTournament((Player) sender, challongeTournamentName, challongeTournamentDescription, challongeTournamentType, challongeOpenSignup, challongeTournamentLink);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        sender.sendMessage(ChatFormatter.formatSuccessMessage("Generating Challonge tournament..."));

                        if (challongeTournament == null) {
                            sender.sendMessage(ChatFormatter.formatErrorMessage("Error while generating tournament. Maybe it is already existent."));
                        } else {
                            sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully generated tournament at link: " + challongeTournament.getChallongeLink()));
                            tournament.setChallongeTournament(challongeTournament);
                            sender.sendMessage(ChatFormatter.formatSuccessMessage("Adding participants to Challonge tournament..."));
                            try {
                                ChallongeIntegrationFactory.addParticipantsToTournament((Player) sender, tournament);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully added participants to the tournament"));
                        }

                        tournament.setGenerated(true);
                    });
                    completableFuture.get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(ChatFormatter.formatErrorMessage("The tournament you specified doesn't exist"));
        }
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/torneo generate <name>";
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
