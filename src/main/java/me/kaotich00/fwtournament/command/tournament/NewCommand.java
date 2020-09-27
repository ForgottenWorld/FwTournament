package me.kaotich00.fwtournament.command.tournament;

import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.command.services.SimpleTournamentService;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class NewCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String tournamentName = args[1];

        SimpleTournamentService simpleTournamentService = SimpleTournamentService.getInstance();
        Boolean result = simpleTournamentService.newTournament(tournamentName);

        if(result) {
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Successfully created tournament " + ChatColor.GOLD + tournamentName));
            sender.sendMessage(ChatFormatter.formatSuccessMessage("You can now setup the tournament by typing"));
            sender.sendMessage(ChatFormatter.formatSuccessMessage("/torneo setup " + tournamentName));
        } else {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Error: a tournament with the same name already exists"));
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
