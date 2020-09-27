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
                if( event.getRawSlot() >= 0 && event.getRawSlot() <= 8 )
                    event.setCancelled(true);
                break;
            default:
                return;
        }
    }

    private void handleKitGUISelection(Player player, Material clickedMenu) {
        switch( clickedMenu ) {
            /* Close menu */
            case BARRIER:
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM, 10, 1);
                player.closeInventory();
                break;
            case EMERALD:
                addItemRewards(player, player.getOpenInventory().getTopInventory());
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 1);
                player.closeInventory();
                break;
        }
    }

    private void addItemRewards(Player player, Inventory inventory) {
        Tournament tournament = SimpleTournamentService.getInstance().getTournamentByModifyingPlayer(player.getUniqueId());

        tournament.getKit().clearKit();
        for( int i = 9; i < inventory.getSize(); i++ ) {
            ItemStack kitItem = inventory.getItem(i);
            if( kitItem != null ) {
                tournament.getKit().addItemToKit(kitItem);
            }
        }
    }

}
