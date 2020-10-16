package me.kaotich00.fwtournament.listener.tournament;

import me.kaotich00.fwtournament.message.Message;
import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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

    @EventHandler
    public void onPlayerTalkEvent(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        if(!SimpleTournamentService.getInstance().getTournament().isPresent()) {
            return;
        }

        Tournament tournament = SimpleTournamentService.getInstance().getTournament().get();

        if(!tournament.isStarted()) {
            return;
        }

        if(!player.hasPermission("fwtournament.speak")) {
            Message.CANT_TALK.send(player);
            event.setCancelled(true);
        }
    }

}
