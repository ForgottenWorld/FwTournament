package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import org.bukkit.command.CommandSender;

public class NewCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        String tournamentName = args[1];

        Boolean result = simpleTournamentService.newTournament(tournamentName);

        if(result) {
            Message.TOURNAMENT_NEW_SUCCESS.send(sender);
        } else {
            Message.TOURNAMENT_NEW_ERROR.send(sender);
        }
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/torneo new <name>";
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
