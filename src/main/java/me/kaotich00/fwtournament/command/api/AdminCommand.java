package me.kaotich00.fwtournament.command.api;

import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

public class AdminCommand extends UserCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("fwtournament.admin")) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("You don't have permissions to run this command"));
            throw new CommandException();
        }
    }

}
