package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.challonge.ChallongeIntegrationFactory;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
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
                Message.TOURNAMENT_ALREADY_STARTED.send(sender);
                return;
            }

            if(tournament.getChallongeTournament() != null) {
                Message.TOURNAMENT_STARTING.send(sender);
                CompletableFuture.supplyAsync(() -> {
                    try {
                        ChallongeIntegrationFactory.startTournament((Player) sender, tournament);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return true;
                }).thenAccept(startingResult -> {
                    Message.TOURNAMENT_STARTED.send(sender);
                    tournament.setStarted(true);

                    SimpleTournamentService.getInstance().refreshTournamentBrackets();
                });
            } else {
                Message.TOURNAMENT_MUST_GENERATE.send(sender);
            }
        } else {
            Message.TOURNAMENT_NOT_FOUND.send(sender);
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
