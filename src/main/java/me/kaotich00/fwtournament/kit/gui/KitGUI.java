package me.kaotich00.fwtournament.kit.gui;

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

    private Tournament tournament;
    private Player player;

    public KitGUI(Player player, Tournament tournament) {
        this.player = player;
        this.tournament = tournament;
    }

    public void openGUI() {
        Inventory GUI = Bukkit.createInventory(null, GUIUtil.KIT_GUI_INVENTORY_SIZE, GUIUtil.KIT_GUI_INVENTORY_TITLE);

        GUI.setItem(GUIUtil.KIT_GUI_INFO_SLOT, kitInfoMenu());
        GUI.setItem(GUIUtil.KIT_GUI_TITLE_SLOT, kitTitleMenu());
        GUI.setItem(GUIUtil.KIT_GUI_CLOSE_SLOT, kitConfirmMenu());

        if( tournament.getKit().getItemsList() != null ) {
            int currentSlot = 9;
            for( ItemStack item : tournament.getKit().getItemsList() ) {
                GUI.setItem(currentSlot, item);
                currentSlot++;
            }
        }

        player.openInventory(GUI);
    }

    private ItemStack kitTitleMenu(){
        String[] lores = new String[] {};
        return prepareMenuPoint(GUIUtil.KIT_GUI_TITLE_MATERIAL, ChatColor.GOLD + "Tournament: " + tournament.getName(), lores );
    }

    private ItemStack kitInfoMenu(){
        String[] lores = new String[] {
                ChatColor.GRAY + "Through this GUI you can modify",
                ChatColor.GRAY + "the kit associated with this",
                ChatColor.GRAY + "tournament",
        };
        return prepareMenuPoint(GUIUtil.KIT_GUI_INFO_MATERIAL,ChatColor.RED + "Info", lores );
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
