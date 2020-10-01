package me.kaotich00.fwtournament.command.arena;

import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class NewCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String arenaName = args[1];

        SimpleArenaService simpleArenaService = SimpleArenaService.getInstance();
        Optional<Arena> arena = simpleArenaService.getArena(arenaName);

        if(arena.isPresent()) {
            sender.sendMessage(ChatFormatter.formatErrorMessage("Error: an arena with that name already exists"));
        } else {
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Arena creation started."));
            sender.sendMessage(ChatFormatter.formatSuccessMessage("Select FIRST PLAYER SPAWN by right clicking on the block"));
            SimpleArenaService.getInstance().addPlayerToArenaCreation((Player) sender, arenaName);
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
