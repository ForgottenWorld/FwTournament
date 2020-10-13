package me.kaotich00.fwtournament.listener.tournament;

import me.kaotich00.fwtournament.services.SimpleTournamentService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.awt.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoinListener(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        SimpleTournamentService.getInstance().checkMatchmakingStatus(player);
    }

    @EventHandler
    public void onPlayerDeathListener(PlayerDeathEvent event) {
        Player player = event.getEntity();

        /*if(!(event.getEntity().getKiller() instanceof Player)) {
            return;
        }*/

        SimpleTournamentService.getInstance().checkTournamentDeath(player);
    }

}
