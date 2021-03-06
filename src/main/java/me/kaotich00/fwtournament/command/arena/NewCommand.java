package me.kaotich00.fwtournament.command.arena;

import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import me.kaotich00.fwtournament.utils.ChatFormatter;
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
            Message.ARENA_ALREADY_EXISTS.send(sender);
        } else {
            Message.ARENA_CREATION_STARTED.send(sender);
            Message.ARENA_CREATION_STEP.send(sender, "FIRST PLAYER SPAWN");
            SimpleArenaService.getInstance().addPlayerToArenaCreation((Player) sender, arenaName);
        }
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/arena new <name>";
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
