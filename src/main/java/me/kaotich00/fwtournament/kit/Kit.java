package me.kaotich00.fwtournament.kit;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private List<ItemStack> itemsList;

    public Kit() {
        this.itemsList = new ArrayList<>();
    }

    public List<ItemStack> getItemsList() {
        return this.itemsList;
    }

    public void addItemToKit(ItemStack item) {
        this.itemsList.add(item);
    }

    public void clearKit() {
        this.itemsList.clear();
    }

}
