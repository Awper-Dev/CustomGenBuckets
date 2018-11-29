package me.awperdev.customgenbuckets.features;

import com.massivecraft.factions.*;
import me.awperdev.customgenbuckets.CustomGenbuckets;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.minecraft.server.v1_8_R3.ItemStack;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Optional;

import static java.lang.Math.round;
import static org.bukkit.block.BlockFace.*;

public class Event implements Listener {

    private static final BlockFace[] ORDERED = {
            SOUTH, SOUTH_WEST,
            WEST, NORTH_WEST,
            NORTH, NORTH_EAST,
            EAST, SOUTH_EAST
    };
    private CustomGenbuckets plugin;
    private Economy econ;
    private HashMap<String, Integer> prices;

    public Event(CustomGenbuckets plugin) {
        this.plugin = plugin;
        prices = plugin.getPrices();
    }

    public static BlockFace direction(float yaw) {
        return ORDERED[round(yaw / 45) & 0x7];
    }

    public static BlockFace direction(Player entity) {
        return direction(entity.getLocation());
    }

    public static BlockFace direction(Location location) {
        return direction(location.getYaw() + 90);
    }

    public static Block getBlock(Location loc) {
        return loc.getWorld().getBlockAt(loc);
    }

    private Integer getPrice(Material mat, boolean vertical) {
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

    @EventHandler
    public void onBukkitEmpty(PlayerBucketEmptyEvent event) {
        //ItemStack craft = CraftItemStack.asNMSCopy(event.getItemStack());
        ItemStack craft = CraftItemStack.asNMSCopy(event.getPlayer().getItemInHand());


        NBTTagCompound tag = Optional.ofNullable(craft.getTag()).orElse(new NBTTagCompound());
        //NBTTagCompound tag = craft.getTag();
        if (tag.hasKey("isVerified")) {
            event.setCancelled(true);
            if (tag.getBoolean("isVerified")) {
                if (tag.hasKey("material") && tag.hasKey("vertical")) {
                    String matName = tag.getString("material");
                    Material material = Material.valueOf(matName);
                    econ = CustomGenbuckets.getEconomy();
                    FPlayer fp = FPlayers.getInstance().getByPlayer(event.getPlayer());
                    Faction fac = fp.getFaction();
                    OfflinePlayer p = Bukkit.getServer().getOfflinePlayer(event.getPlayer().getUniqueId());


                    Boolean vertical = tag.getBoolean("vertical");
                    Block neededB = event.getBlockClicked().getRelative(event.getBlockFace());
                    Location loc = neededB.getLocation();
                    FLocation fLocation = new FLocation(loc);

                    if(fac != Board.getInstance().getFactionAt(fLocation)){
                        event.getPlayer().sendMessage(ChatColor.RED + "You can only use genbuckets in your own claim!");
                        return;
                    }
                    final int X = loc.getBlockX();
                    final int Y = loc.getBlockY();
                    final int Z = loc.getBlockZ();


                    //vertical
                    if (vertical) {
                        if (econ.getBalance(p) >= (double) getPrice(material, true)) {
                            EconomyResponse response = econ.withdrawPlayer(p, (double) getPrice(material, true));
                            if (response.transactionSuccess()) {
                                //


                                new BukkitRunnable() {
                                    int i = 0;
                                    int rX = X;
                                    int rY = Y;
                                    int rZ = Z;

                                    @Override
                                    public void run() {
                                        if (i <= 256) {
                                            Location loc = new Location(event.getPlayer().getWorld(), rX, rY, rZ);
                                            FLocation floc = new FLocation(loc);
                                            if (!(getBlock(loc) == null || !getBlock(loc).getType().equals(Material.AIR))) {
                                                if(fac == Board.getInstance().getFactionAt(floc)){
                                                    getBlock(loc).setType(material);
                                                }else{
                                                    this.cancel();
                                                }
                                            } else {
                                                this.cancel();
                                            }
                                            rY = rY - 1;
                                            i++;
                                        } else {
                                            this.cancel();

                                        }
                                    }
                                }.runTaskTimer(plugin, 0L, 20L);
                                //
                            }
                        } else {
                            event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to do this!");
                        }
                    } else {
                        if (econ.getBalance(p) >= (double) getPrice(material, false)) {
                            EconomyResponse response = econ.withdrawPlayer(p, (double) getPrice(material, false));
                            if (response.transactionSuccess()) {
                                BlockFace face = direction(event.getPlayer());
                                Block b = neededB.getRelative(face);
                                if (b.getLocation().getBlockZ() < Z) {
                                    new BukkitRunnable() {
                                        int i = 0;
                                        int rX = X;
                                        int rY = Y;
                                        int rZ = Z;

                                        @Override
                                        public void run() {
                                            if (i <= 32) {
                                                Location loc = new Location(event.getPlayer().getWorld(), rX, rY, rZ);
                                                FLocation floc = new FLocation(loc);
                                                if (!(getBlock(loc) == null || !getBlock(loc).getType().equals(Material.AIR))) {
                                                    if(fac == Board.getInstance().getFactionAt(floc)){
                                                        getBlock(loc).setType(material);
                                                    }else{
                                                        this.cancel();
                                                    }
                                                } else {
                                                    this.cancel();
                                                }
                                                rZ = rZ - 1;
                                                i++;
                                            } else {
                                                this.cancel();

                                            }
                                        }
                                    }.runTaskTimer(plugin, 0L, 20L);

                                } else if (b.getLocation().getBlockZ() > Z) {
                                    new BukkitRunnable() {
                                        int i = 0;
                                        int rX = X;
                                        int rY = Y;
                                        int rZ = Z;

                                        @Override
                                        public void run() {
                                            if (i <= 32) {
                                                Location loc = new Location(event.getPlayer().getWorld(), rX, rY, rZ);
                                                FLocation floc = new FLocation(loc);
                                                if (!(getBlock(loc) == null || !getBlock(loc).getType().equals(Material.AIR))) {
                                                    if(fac == Board.getInstance().getFactionAt(floc)){
                                                        getBlock(loc).setType(material);
                                                    }else{
                                                        this.cancel();
                                                    }
                                                } else {
                                                    this.cancel();
                                                }
                                                rZ = rZ + 1;
                                                i++;
                                            } else {
                                                this.cancel();

                                            }
                                        }
                                    }.runTaskTimer(plugin, 0L, 20L);

                                } else if (b.getLocation().getBlockX() < X) {
                                    new BukkitRunnable() {
                                        int i = 0;
                                        int rX = X;
                                        int rY = Y;
                                        int rZ = Z;

                                        @Override
                                        public void run() {
                                            if (i <= 32) {
                                                Location loc = new Location(event.getPlayer().getWorld(), rX, rY, rZ);
                                                FLocation floc = new FLocation(loc);

                                                if (!(getBlock(loc) == null || !getBlock(loc).getType().equals(Material.AIR))) {
                                                    if(fac == Board.getInstance().getFactionAt(floc)){
                                                        getBlock(loc).setType(material);
                                                    }else{
                                                        this.cancel();
                                                    }
                                                } else {
                                                    this.cancel();
                                                }
                                                rX = rX - 1;
                                                i++;
                                            } else {
                                                this.cancel();

                                            }
                                        }
                                    }.runTaskTimer(plugin, 0L, 20L);

                                } else if (b.getLocation().getBlockX() > X) {
                                    new BukkitRunnable() {
                                        int i = 0;
                                        int rX = X;
                                        int rY = Y;
                                        int rZ = Z;

                                        @Override
                                        public void run() {
                                            if (i <= 32) {
                                                Location loc = new Location(event.getPlayer().getWorld(), rX, rY, rZ);
                                                FLocation floc = new FLocation(loc);
                                                if (!(getBlock(loc) == null || !getBlock(loc).getType().equals(Material.AIR))) {
                                                    if(fac == Board.getInstance().getFactionAt(floc)){
                                                        getBlock(loc).setType(material);
                                                    }else{
                                                        this.cancel();
                                                    }

                                                } else {
                                                    this.cancel();
                                                }
                                                rX = rX + 1;
                                                i++;
                                            } else {
                                                this.cancel();

                                            }
                                        }
                                    }.runTaskTimer(plugin, 0L, 20L);
                                }
                            }
                        } else {
                            event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough money to do this!");

                        }
                    }


                }
            } else {
                Bukkit.getLogger().warning("Something went wrong! Please contact awper!");
            }
        }
    }


}
