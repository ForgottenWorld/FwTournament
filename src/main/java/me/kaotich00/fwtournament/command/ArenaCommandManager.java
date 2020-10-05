package me.kaotich00.fwtournament.command;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.api.command.Command;
import me.kaotich00.fwtournament.command.arena.NewCommand;
import me.kaotich00.fwtournament.services.SimpleArenaService;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import me.kaotich00.fwtournament.utils.CommandTypes;
import me.kaotich00.fwtournament.utils.NameUtil;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArenaCommandManager implements TabExecutor {

    private Map<String, Command> commandRegistry;
    private Fwtournament plugin;

    public ArenaCommandManager(Fwtournament plugin) {
        this.commandRegistry = new HashMap<>();
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        this.commandRegistry.put(CommandTypes.ARENA_NEW_COMMAND, new NewCommand());
    }

    private Command getCommand(String name) {
        return this.commandRegistry.get(name);
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if( args.length == 0 ) {
            sender.sendMessage(ChatFormatter.helpMessage());
            return CommandTypes.COMMAND_SUCCESS;
        }

        Command erCommand = getCommand(args[0]);

        if( erCommand != null ) {
            if(erCommand.getRequiredArgs() > args.length) {
                sender.sendMessage(ChatFormatter.formatErrorMessage("Not enough arguments"));
                sender.sendMessage(ChatFormatter.formatErrorMessage(erCommand.getUsage()));
                return true;
            }
            try {
                erCommand.onCommand(sender, args);
            } catch (CommandException e) {
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();
        String argsIndex = "";

        /* Suggest child commands */
        if(args.length == 1) {
            argsIndex = args[0];

            for(String commandName: this.commandRegistry.keySet()) {
                suggestions.add(commandName);
            }
        }

        if(args.length == 2) {
            argsIndex = args[1];

            for(String arenaName: SimpleArenaService.getInstance().getArenas().keySet()) {
                suggestions.add(arenaName);
            }
        }

        return NameUtil.filterByStart(suggestions, argsIndex);
    }

}
