package me.kaotich00.fwtournament.command.arena;

import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.command.api.UserCommand;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class JoinCommand extends UserCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String arenaName = args[1];

        SimpleArenaService simpleArenaService = SimpleArenaService.getInstance();
        Optional<Arena> optArena = simpleArenaService.getArena(arenaName);

        if(optArena.isPresent()) {
            Arena arena = optArena.get();
            ((Player) sender).teleport(arena.getPlayerOneBattle());
        }
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/torneo info";
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
