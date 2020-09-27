package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.tournament.setup.TournamentSetupPrompt;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetupCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String tournamentName = args[1];

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        if(simpleTournamentService.getTournament(tournamentName).isPresent()) {
            Tournament tournament = simpleTournamentService.getTournament(tournamentName).get();
            TournamentSetupPrompt creation = new TournamentSetupPrompt(Fwtournament.getPlugin(Fwtournament.class), tournament);
            creation.startConversationForPlayer((Player)sender);

            SimpleTournamentService.getInstance().addModifyingPlayer(((Player) sender).getUniqueId(), tournament);
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
        return "/torneo setup <name>";
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
