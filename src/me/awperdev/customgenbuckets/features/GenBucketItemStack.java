package me.awperdev.customgenbuckets.features;

import me.awperdev.customgenbuckets.CustomGenbuckets;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenBucketItemStack {

    private CustomGenbuckets plugin;
    private static HashMap<String, Integer> prices;

    public GenBucketItemStack(CustomGenbuckets plugin) {
        this.plugin = plugin;
        prices = plugin.getPrices();
    }

    private static Integer getPrice(Material mat, boolean vertical) {
        if (mat.equals(Material.SAND)) {
            if (vertical) {
                return prices.get("vertical-sand");
            } else {
                return prices.get("horizontal-sand");
            }
        } else if (mat.equals(Material.COBBLESTONE)) {
            if (vertical) {
                return prices.get("vertical-cobblestone");
            } else {
                return prices.get("horizontal-cobblestone");
            }

        } else if (mat.equals(Material.OBSIDIAN)) {
            if (vertical) {
                return prices.get("vertical-obsidian");
            } else {
                return prices.get("horizontal-obsidian");
            }

        }
        return 0;

    }

    public static ItemStack genBucket(boolean vertical, String genmat) {


        ItemStack start = new ItemStack(Material.LAVA_BUCKET, 1);
        net.minecraft.server.v1_8_R3.ItemStack newstack = CraftItemStack.asNMSCopy(start);
        Material m = Material.PAPER;
        if (genmat.equalsIgnoreCase("obsidian")) {
            m = Material.OBSIDIAN;
        } else if (genmat.equalsIgnoreCase("sand")) {
            m = Material.SAND;
        } else if (genmat.equalsIgnoreCase("cobblestone")) {
            m = Material.COBBLESTONE;
        }

        NBTTagCompound compound = new NBTTagCompound();
        compound.setBoolean("isVerified", true);
        compound.setBoolean("vertical", vertical);
        compound.setString("material", genmat);
        newstack.setTag(compound);

        ItemStack test = CraftItemStack.asBukkitCopy(newstack);

        if (test == null || newstack == null) {
            Bukkit.getServer().broadcastMessage("fucked up");
            return null;
        }

        String s;
        if (vertical) {
            s = "Vertical";
        } else {
            s = "Horizontal";
        }
        ItemMeta im = test.getItemMeta();
        im.setDisplayName(ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + s + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY + "GenBucket " + ChatColor.DARK_GRAY + "[" + ChatColor.GREEN + genmat + ChatColor.DARK_GRAY + "]");
        List<String> lore = new ArrayList<>();
        lore.add("Use this item to generate a wall!");
        if (!vertical) {
            lore.add(ChatColor.RED + "Attention! " + "This item will generate a wall to the right!");
            lore.add(ChatColor.GREEN + "1 use will cost you " + getPrice(m, false) + ChatColor.GREEN + "!");
        } else {
            lore.add(ChatColor.GREEN + "1 use will cost you " + getPrice(m, true) + ChatColor.GREEN + "!");
        }
        lore.add(ChatColor.RED + "GenBuckets will only generate blocks in claimed territory!");
        im.setLore(lore);
        im.addEnchant(Enchantment.PROTECTION_FIRE, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        test.setItemMeta(im);


        return test;

    }
}
