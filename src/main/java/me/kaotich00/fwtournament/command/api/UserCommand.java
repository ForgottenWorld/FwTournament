package me.kaotich00.fwtournament.command.api;

import me.kaotich00.fwtournament.api.command.Command;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UserCommand implements Command {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Only players can run this command"));
            throw new CommandException();
        }
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getUsage() {
        return null;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public Integer getRequiredArgs() {
        return null;
    }

}
