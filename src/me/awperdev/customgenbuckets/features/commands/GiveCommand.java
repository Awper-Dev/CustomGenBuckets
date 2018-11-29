package me.awperdev.customgenbuckets.features.commands;

import me.awperdev.customgenbuckets.CustomGenbuckets;
import me.awperdev.customgenbuckets.features.GenBucketItemStack;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveCommand implements CommandExecutor {




    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.hasPermission("genbucket.give")) {
            if (command.getName().equalsIgnoreCase("gengive")) {
                if (strings.length == 3) {
                    Player p = Bukkit.getPlayer(strings[0]);
                    if (strings[2].equalsIgnoreCase("obsidian") || strings[2].equalsIgnoreCase("sand") || strings[2].equalsIgnoreCase("cobblestone")) {
                        Material material = Material.GLASS;
                        Boolean vertical = true;
                        if (strings[2].equalsIgnoreCase("obsidian")) {
                            material = Material.OBSIDIAN;
                        } else if (strings[2].equalsIgnoreCase("sand")) {
                            material = Material.SAND;
                        } else if (strings[2].equalsIgnoreCase("cobblestone")) {
                            material = Material.COBBLESTONE;
                        }
                        if (strings[1].equalsIgnoreCase("vertical") || strings[1].equalsIgnoreCase("horizontal")) {
                            if (strings[1].equalsIgnoreCase("vertical")) {
                                vertical = true;
                            } else if (strings[1].equalsIgnoreCase("horizontal")) {
                                vertical = false;
                            }

                            org.bukkit.inventory.ItemStack stack = GenBucketItemStack.genBucket(vertical, material.name());
                            if (p.getInventory().firstEmpty() == -1) {
                                p.getWorld().dropItem(p.getLocation(), stack);
                            } else {
                                p.getInventory().addItem(stack);
                            }

                        } else {
                            commandSender.sendMessage(ChatColor.RED + "Please choose between horizontal and vertical");
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.RED + "Please choose between cobblestone, sand or obsidian");
                    }
                } else {
                    commandSender.sendMessage(ChatColor.RED + "Wrong usage! /gengive <playername> <vertical/horizontal> <obsidian/sand/cobblestone>");
                }
            }
        }
        return true;
    }
}
