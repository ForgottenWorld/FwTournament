package me.kaotich00.fwtournament.api.command;

import org.bukkit.command.CommandSender;

public interface Command {

    void onCommand(CommandSender sender, String args[]);

    String getName();

    String getUsage();

    String getInfo();

    Integer getRequiredArgs();

}
