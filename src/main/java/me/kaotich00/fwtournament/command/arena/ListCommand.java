package me.kaotich00.fwtournament.command.arena;

import me.kaotich00.fwtournament.arena.Arena;
import me.kaotich00.fwtournament.command.api.AdminCommand;
import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import org.bukkit.command.CommandSender;

public class ListCommand extends AdminCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        super.onCommand(sender, args);

        sender.sendMessage(ChatFormatter.pluginPrefix() + "Arenas list:");
        if(SimpleArenaService.getInstance().getArenas().values().isEmpty()) {
            Message.ARENA_EMPTY.send(sender);
        }
        for(Arena arena : SimpleArenaService.getInstance().getArenas().values()) {
            sender.sendMessage(ChatFormatter.formatSuccessMessage(arena.getArenaName()));
        }
    }

    @Override
    public String getInfo() {
        return super.getInfo();
    }

    @Override
    public String getUsage() {
        return "/arena list";
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
