package me.kaotich00.fwtournament.command.tournament;

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

public class StartCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String tournamentName = args[1];

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        if(simpleTournamentService.getTournament(tournamentName).isPresent()) {
            Tournament tournament = simpleTournamentService.getTournament(tournamentName).get();

            if(tournament.isStarted()) {
                sender.sendMessage(ChatFormatter.formatErrorMessage("Tournament already started!"));
                return;
            }

            if(tournament.getChallongeTournament() != null) {
                try {
                    sender.sendMessage(ChatFormatter.formatSuccessMessage("Starting tournament..."));
                    CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> {
                        try {
                            ChallongeIntegrationFactory.startTournament((Player) sender, tournament);
                            sender.sendMessage(ChatFormatter.formatSuccessMessage("Tournament started!"));

                            tournament.setStarted(true);

                            sender.sendMessage(ChatFormatter.formatSuccessMessage("Collecting current brackets..."));
                            ChallongeIntegrationFactory.getTournamentBrackets((Player) sender, tournament);
                            sender.sendMessage(ChatFormatter.formatSuccessMessage("Brackets collected!"));

                            SimpleTournamentService.getInstance().checkForNewMatchmakings();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    });
                    completableFuture.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                sender.sendMessage(ChatFormatter.formatErrorMessage("The tournament must be generated before starting it, with the command /torneo generate <name>"));
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
