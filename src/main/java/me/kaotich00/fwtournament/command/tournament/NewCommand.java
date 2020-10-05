package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.command.CommandSender;

public class NewCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        String tournamentName = args[1];

        Boolean result = simpleTournamentService.newTournament(tournamentName);

        if(result) {
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully created tournament"));
            sender.sendMessage(ChatFormatter.formatSuccessMessage("You can now setup the tournament by typing"));
            sender.sendMessage(ChatFormatter.formatSuccessMessage("/torneo setup"));
        } else {
            sender.sendMessage("A tournament is already created. Skipping.");
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
