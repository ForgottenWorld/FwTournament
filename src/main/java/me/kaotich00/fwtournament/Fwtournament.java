package me.kaotich00.fwtournament;

import me.kaotich00.fwtournament.command.ArenaCommandManager;
import me.kaotich00.fwtournament.command.TournamentCommandManager;
import me.kaotich00.fwtournament.listener.arena.ArenaCreationListener;
import me.kaotich00.fwtournament.listener.kit.KitGuiListener;
import me.kaotich00.fwtournament.listener.tournament.PlayerListener;
import me.kaotich00.fwtournament.storage.sqlite.SQLiteConnectionService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

public final class Fwtournament extends JavaPlugin {

    public static FileConfiguration defaultConfig;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FwTournament]" + ChatColor.RESET + " Loading configuration...");
        loadConfiguration();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FwTournament]" + ChatColor.RESET + " Registering commands...");
        registerCommands();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FwTournament]" + ChatColor.RESET + " Registering listeners...");
        registerListeners();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FwTournament]" + ChatColor.RESET + " Setting up SQLite...");
        setupSQLite();
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FwTournament]" + ChatColor.RESET + " Saving data to SQLite");
        CompletableFuture.runAsync(() -> {
            SQLiteConnectionService sqLiteConnectionService = SQLiteConnectionService.getInstance();
            sqLiteConnectionService.saveTournamentToDatabase(this, "fwtournament");
        });
    }

    private void loadConfiguration() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        defaultConfig = getConfig();
    }

    private void setupSQLite() {
        CompletableFuture.runAsync(() -> {
            SQLiteConnectionService sqLiteConnectionService = SQLiteConnectionService.getInstance();
            sqLiteConnectionService.createNewDatabase(this, "fwtournament");
            sqLiteConnectionService.setupDefaultTables(this, "fwtournament");

            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[FwTournament]" + ChatColor.RESET + " Loading data from SQLite...");
            loadDataFromSQlite();
        });
    }

    private void loadDataFromSQlite() {
        SQLiteConnectionService sqLiteConnectionService = SQLiteConnectionService.getInstance();
        sqLiteConnectionService.loadTournamentsFromDatabase(this, "fwtournament");
    }

    public static FileConfiguration getDefaultConfig() {
        return defaultConfig;
    }

    public void registerCommands() {
        getCommand("torneo").setExecutor(new TournamentCommandManager(this));
        getCommand("arena").setExecutor(new ArenaCommandManager(this));
    }

    public void registerListeners() {
        getServer().getPluginManager().registerEvents(new KitGuiListener(), this);
        getServer().getPluginManager().registerEvents(new ArenaCreationListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public void reloadDefaultConfig() {
        reloadConfig();
        defaultConfig = getConfig();
    }

}
