package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.util.concurrent.CompletableFuture;

public class StartCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        if(simpleTournamentService.getTournament().isPresent()) {
            Tournament tournament = simpleTournamentService.getTournament().get();

            if(tournament.isStarted()) {
                sender.sendMessage(ChatFormatter.formatErrorMessage("Tournament already started!"));
                return;
            }

            if(tournament.getChallongeTournament() != null) {
                sender.sendMessage(ChatFormatter.formatSuccessMessage("Starting tournament..."));
                CompletableFuture.supplyAsync(() -> {
                    try {
                        ChallongeIntegrationFactory.startTournament((Player) sender, tournament);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
                }).thenAccept(startingResult -> {
                    sender.sendMessage(ChatFormatter.formatSuccessMessage("Tournament started!"));
                    tournament.setStarted(true);

                    sender.sendMessage(ChatFormatter.formatSuccessMessage("Collecting current brackets..."));
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            ChallongeIntegrationFactory.getTournamentBrackets((Player) sender, tournament);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }).thenAccept(bracketResult -> {
                        sender.sendMessage(ChatFormatter.formatSuccessMessage("Brackets collected!"));
                        SimpleTournamentService.getInstance().checkForNewMatchmakings();
                    });
                });
            } else {
                sender.sendMessage(ChatFormatter.formatErrorMessage("The tournament must be generated before starting it, with the command /torneo generate"));
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
        return "/torneo start";
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
