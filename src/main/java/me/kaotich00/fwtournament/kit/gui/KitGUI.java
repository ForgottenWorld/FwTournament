package me.kaotich00.fwtournament.kit.gui;

import me.kaotich00.fwtournament.services.SimpleTournamentService;
import me.kaotich00.fwtournament.tournament.Tournament;
import me.kaotich00.fwtournament.utils.GUIUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KitGUI {

    private Player player;

    public KitGUI(Player player) {
        this.player = player;
    }

    public void openGUI() {
        Inventory GUI = Bukkit.createInventory(null, GUIUtil.KIT_GUI_INVENTORY_SIZE, GUIUtil.KIT_GUI_INVENTORY_TITLE);

        GUI.setItem(GUIUtil.KIT_GUI_CLOSE_SLOT, kitConfirmMenu());

        if( SimpleTournamentService.getInstance().getTournamentsKit().getItemsList() != null ) {
            int currentSlot = 9;
            for( ItemStack item : SimpleTournamentService.getInstance().getTournamentsKit().getItemsList() ) {
                GUI.setItem(currentSlot, item);
                currentSlot++;
            }
        }

        player.openInventory(GUI);
    }

    private ItemStack kitConfirmMenu(){
        String[] lores = new String[] {};
        return prepareMenuPoint(GUIUtil.KIT_GUI_CONFIRM_MATERIAL,ChatColor.RED + "Confirm", lores );
    }

    private ItemStack prepareMenuPoint(Material material, String displayName, String[] lore) {
        ItemStack menuPoint = new ItemStack(material);

        ItemMeta menuPointMeta = menuPoint.getItemMeta();
        menuPointMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        menuPointMeta.setDisplayName(displayName);
        List<String> menuPointLore = new ArrayList(Arrays.asList( lore ) );
        menuPointMeta.setLore(menuPointLore);

        menuPoint.setItemMeta(menuPointMeta);

        return menuPoint;
    }

}
