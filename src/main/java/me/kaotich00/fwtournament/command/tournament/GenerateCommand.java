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

public class GenerateCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String tournamentName = args[1];

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        if(simpleTournamentService.getTournament(tournamentName).isPresent()) {
            Tournament tournament = simpleTournamentService.getTournament(tournamentName).get();
            try {
                if(tournament.getChallongeTournament() != null) {
                    sender.sendMessage(ChatFormatter.formatErrorMessage("The tournament has already been generated, skipping."));
                    ChallongeIntegrationFactory.getTournamentBrackets((Player) sender, tournament);
                } else {
                    ChallongeTournament challongeTournament = ChallongeIntegrationFactory.createTournament((Player) sender, tournament.getName(), "test descrizione", "single elimination", "false", "fw_tournament_" + tournament.getName());
                    sender.sendMessage(ChatFormatter.formatSuccessMessage("Generating Challonge tournament..."));

                    if (challongeTournament == null) {
                        sender.sendMessage(ChatFormatter.formatErrorMessage("Error while generating tournament. Maybe it is already existent."));
                    } else {
                        sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully generated tournament at link: " + challongeTournament.getChallongeLink()));
                        tournament.setChallongeTournament(challongeTournament);
                        sender.sendMessage(ChatFormatter.formatSuccessMessage("Adding participants to Challonge tournament..."));
                        ChallongeIntegrationFactory.addParticipantsToTournament((Player) sender, tournament);
                        sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully added participants to the tournament"));
                    }
                }
            } catch (ParseException e) {
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
