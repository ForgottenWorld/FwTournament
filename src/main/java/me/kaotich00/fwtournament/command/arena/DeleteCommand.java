package me.kaotich00.fwtournament.command.arena;

import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import org.bukkit.command.CommandSender;

import java.util.Optional;

public class DeleteCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        String arenaName = args[1];

        SimpleArenaService simpleArenaService = SimpleArenaService.getInstance();
        Optional<Arena> arena = simpleArenaService.getArena(arenaName);

        if(!arena.isPresent()) {
            Message.ARENA_NOT_FOUND.send(sender);
        } else {
            SimpleArenaService.getInstance().deleteArena(arenaName);
            Message.ARENA_DELETED.send(sender);
        }

    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/arena delete <name>";
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
