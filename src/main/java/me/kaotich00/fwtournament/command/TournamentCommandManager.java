package me.kaotich00.fwtournament.command;

import me.kaotich00.fwtournament.Fwtournament;
import me.kaotich00.fwtournament.command.tournament.NewCommand;
import me.kaotich00.fwtournament.command.tournament.SetupCommand;
import me.kaotich00.fwtournament.utils.ChatFormatter;
import me.kaotich00.fwtournament.utils.CommandTypes;
import me.kaotich00.fwtournament.utils.NameUtil;
import me.kaotich00.fwtournament.api.command.Command;
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
        this.commandRegistry.put(CommandTypes.TORNEO_SETUP_COMMAND, new SetupCommand());
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
            erCommand.onCommand(sender, args);
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
            /* Admin commands */
            //suggestions.add("reload");
        }

        /*if(args.length == 2) {
            argsIndex = args[1];
            switch(args[1]) {
                case CommandTypes.CHECK_COMMAND:
                    for(Player player: Bukkit.getOnlinePlayers()) {
                        suggestions.add(player.getPlayerListName());
                    }
                    break;
            }
        }*/

        return NameUtil.filterByStart(suggestions, argsIndex);
    }

}
