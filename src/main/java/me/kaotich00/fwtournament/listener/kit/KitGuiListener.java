package me.kaotich00.fwtournament.listener.kit;

import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.GUIUtil;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class KitGuiListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        dispatchClickEvent(event);
    }

    private void dispatchClickEvent(InventoryClickEvent event ) {
        if( event.getCurrentItem() == null ) {
            return;
        }

        switch(event.getView().getTitle()) {
            case GUIUtil.KIT_GUI_INVENTORY_TITLE:
                if(event.getRawSlot() <= event.getInventory().getSize()) {
                    handleKitGUISelection((Player) event.getWhoClicked(), event.getCurrentItem().getType());
                }
                if( event.getRawSlot() == 44 )
                    event.setCancelled(true);
                break;
            default:
                return;
        }
    }

    private void handleKitGUISelection(Player player, Material clickedMenu) {
        switch( clickedMenu ) {
            case EMERALD:
                addItemRewards(player.getOpenInventory().getTopInventory());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                player.closeInventory();
                break;
        }
    }

    private void addItemRewards(Inventory inventory) {
        SimpleTournamentService.getInstance().getTournamentsKit().clearKit();
        for( int i = 0; i < inventory.getSize() - 1; i++ ) {
            ItemStack kitItem = inventory.getItem(i);
            if( kitItem != null ) {
                SimpleTournamentService.getInstance().getTournamentsKit().addItemToKit(kitItem);
            }
        }
    }

}
