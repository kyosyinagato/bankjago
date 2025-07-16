package com.arayx.bankjago.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemBuilder {
    private final ItemStack item;
    private final ItemMeta  meta;

    public ItemBuilder(Material mat) {
        this.item = new ItemStack(mat);
        this.meta = item.getItemMeta();
    }

    /** Set the display name */
    public ItemBuilder name(String name) {
        meta.setDisplayName(name);
        return this;
    }

    /** Set the lore lines */
    public ItemBuilder lore(List<String> lore) {
        meta.setLore(new ArrayList<>(lore));
        return this;
    }

    /** Convenience overload */
    public ItemBuilder lore(String... lines) {
        return lore(Arrays.asList(lines));
    }

    /** If this is a player head, set its owner */
    public ItemBuilder skullOwner(String owner) {
        if (meta instanceof SkullMeta skullMeta) {
            skullMeta.setOwner(owner);
        }
        return this;
    }

    /** Build the final ItemStack */
    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}
