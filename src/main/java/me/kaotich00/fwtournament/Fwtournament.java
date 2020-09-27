package me.kaotich00.fwtournament;

import me.kaotich00.fwtournament.command.TournamentCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Fwtournament extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FwTournament]" + ChatColor.RESET + " Registering commands...");
        registerCommands();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerCommands() {
        getCommand("torneo").setExecutor(new TournamentCommandManager(this));
    }

}
