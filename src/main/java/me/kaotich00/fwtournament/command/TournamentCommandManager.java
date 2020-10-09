package me.kaotich00.fwtournament.command;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.api.command.Command;
import me.kaotich00.fwtournament.command.tournament.*;
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

public class TournamentCommandManager implements TabExecutor {

    private Map<String, Command> commandRegistry;
    private Fwtournament plugin;

    public TournamentCommandManager(Fwtournament plugin) {
        this.commandRegistry = new HashMap<>();
        this.plugin = plugin;
        setup();
    }

    private void setup() {
        this.commandRegistry.put(CommandTypes.TORNEO_NEW_COMMAND, new NewCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_GENERATE_COMMAND, new GenerateCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_START_COMMAND, new StartCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_INFO_COMMAND, new InfoCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_ADD_COMMAND, new AddCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_KIT_COMMAND, new KitCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_REMOVE_COMMAND, new RemoveCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_DELETE_COMMAND, new DeleteCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_SKIP_COMMAND, new SkipCommand());
        this.commandRegistry.put(CommandTypes.TORNEO_FIX_COMMAND, new FixCommand());
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
        }

        return NameUtil.filterByStart(suggestions, argsIndex);
    }

}
