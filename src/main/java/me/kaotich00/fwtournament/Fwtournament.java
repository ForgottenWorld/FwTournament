package me.kaotich00.fwtournament;

import me.kaotich00.fwtournament.command.TournamentCommandManager;
import me.kaotich00.fwtournament.listener.kit.KitGuiListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class Fwtournament extends JavaPlugin {

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FwTournament]" + ChatColor.RESET + " Registering commands...");
        registerCommands();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[EasyRanking]" + ChatColor.RESET + " Registering listeners...");
        registerListeners();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void registerCommands() {
        getCommand("torneo").setExecutor(new TournamentCommandManager(this));
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new KitGuiListener(), this);
    }

}
