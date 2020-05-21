package me.zhdg.vault;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.text.SimpleDateFormat;
import java.util.*;

public class EventListener implements Listener {
    private static EventListener instance = null;

    public Map<String, String> chat = new HashMap<>();

    public static EventListener get() {
        return instance == null ? instance = new EventListener() : instance;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        if (title.startsWith(Main.PREFIX)) {
            Player p = (Player) e.getWhoClicked();
            PlayerConfig conf = PlayerConfig.getConfig(p.getUniqueId());
            e.setCancelled(true);
            if (e.getCurrentItem() != null) {
                Material mat = e.getCurrentItem().getType();
                if (title.equals(Main.PREFIX + "Main")) {
                    if (e.getRawSlot() < 3 * 9) {
                        switch (mat) {
                            case CHEST:
                                p.openInventory(GUIManager.get().getSelectInv(p.getUniqueId(), true, false, 1));
                                return;
                            case ENDER_CHEST:
                                int nVault = Main.get().getVaultsCount(p.getUniqueId()) + 1;
                                if (nVault <= Main.get().getMaxVaults(p)) {
                                    conf.set(nVault + ".lvl", 1);
                                    conf.set(nVault + ".date", new SimpleDateFormat("M/d/YYYY").format(new Date()));
                                    try {
                                        InventoryManager.get().saveInv(Bukkit.createInventory(null, 9, ""), nVault + ".items", p.getUniqueId());
                                    } catch (IllegalArgumentException e1) {
                                        e1.printStackTrace();
                                    }
                                    Main.sendMessage(p, "CreatedNewVault", true);
                                    p.openInventory(GUIManager.get().getVaultMain(p));
                                } else Main.sendMessage(p, "ReachedMaxVaults", true);
                                return;
                            case BARRIER:
                                p.closeInventory();
                        }
                    }
                } else if (title.equals(Main.PREFIX + "Selection")) {
                    if (e.getRawSlot() < 4 * 9) {
                        switch (mat) {
                            case CHEST:
                                String iName = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName();
                                p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
                                p.openInventory(GUIManager.get().getVault(getPath(iName), p.getUniqueId()));
                                return;
                            case ANVIL:
                                p.openInventory(GUIManager.get().getSelectInv(p.getUniqueId(), true, true, 1));
                                return;
                            case LIME_STAINED_GLASS_PANE:
                                p.openInventory(GUIManager.get().getMoneyVault(p));
                                return;
                            case YELLOW_STAINED_GLASS_PANE:
                                p.openInventory(GUIManager.get().getEXPVault(p));
                                return;
                            case WHITE_STAINED_GLASS_PANE:
                                p.openInventory(GUIManager.get().getSelectInv(p.getUniqueId(), Objects.requireNonNull(e.getInventory().getItem(22)).getType() == Material.ANVIL, false, e.getCurrentItem().getAmount()));
                                return;
                            case BARRIER:
                                p.openInventory(GUIManager.get().getVaultMain(p));
                        }
                    }
                } else if (title.equals(Main.PREFIX + "Select Vault")) {
                    if (e.getRawSlot() < 4 * 9) {
                        switch (mat) {
                            case REDSTONE_BLOCK:
                                p.openInventory(GUIManager.get().getSelectInv(p.getUniqueId(), true, false, 1));
                                return;
                            case CHEST:
                                String iName = Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName();
                                p.openInventory(GUIManager.get().getUpgradeInv(p, Integer.parseInt(iName.split("#")[iName.split("#").length - 1])));
                                return;
                            case WHITE_STAINED_GLASS_PANE:
                                p.openInventory(GUIManager.get().getSelectInv(p.getUniqueId(), true, true, e.getCurrentItem().getAmount()));
                        }
                    }
                } else if (title.endsWith("Menu")) {
                    if (e.getRawSlot() < 3 * 9) {
                        String path = title.split("#")[1].split(" ")[0];
                        switch (mat) {
                            case END_PORTAL_FRAME:
                                p.openInventory(GUIManager.get().getDeleteVerification(null, Integer.parseInt(path.split("\\.")[0])));
                                return;
                            case ENDER_CHEST:
                                p.openInventory(GUIManager.get().getUpgradeVerification(p, null, Integer.parseInt(path.split("\\.")[0])));
                                return;
                            case NAME_TAG:
                                p.openInventory(GUIManager.get().getRenameVerification(null, Integer.parseInt(path.split("\\.")[0])));
                                return;
                            case BARRIER:
                                p.openInventory(GUIManager.get().getSelectInv(p.getUniqueId(), true, true, 1));
                        }
                    }
                } else if (title.equals(Main.PREFIX + "\u00a7a\u00a7lMONEY VAULT")) {
                    if (e.getRawSlot() < 2 * 9) {
                        int amount;
                        switch (mat) {
                            case LIME_STAINED_GLASS_PANE:
                                amount = Integer.parseInt(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().split("\\$")[1]);
                                if (pay(p, amount)) {
                                    conf.set("money", conf.getInt("money", 0) + amount);
                                    conf.save();
                                    Main.sendMessage(p, "AddedMoneyToVault", true, "AMOUNT", String.valueOf(amount));
                                    p.openInventory(GUIManager.get().getMoneyVault(p));
                                } else Main.sendMessage(p, "NotEnoughMoney", true);
                                return;
                            case RED_STAINED_GLASS_PANE:
                                amount = Integer.parseInt(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().split("\\$")[1]);
                                if (conf.getInt("money", 0) >= amount) {
                                    conf.set("money", conf.getInt("money", 0) - amount);
                                    conf.save();
                                    Main.sendMessage(p, "AddedMoney", true, "AMOUNT", String.valueOf(amount));
                                    p.openInventory(GUIManager.get().getMoneyVault(p));
                                    Main.economy.depositPlayer(p, amount);
                                } else Main.sendMessage(p, "NotEnoughMoneyInVault", true);
                                return;
                            case BARRIER:
                                p.openInventory(GUIManager.get().getSelectInv(p.getUniqueId(), true, false, 1));
                        }
                    }
                } else if (title.equals(Main.PREFIX + "\u00a7a\u00a7lEXP VAULT")) {
                    if (e.getRawSlot() < 2 * 9) {
                        int amount;
                        switch (mat) {
                            case LIME_STAINED_GLASS_PANE:
                                amount = Integer.parseInt(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().split("\u00a77")[1].split(" ", 2)[0]);
                                if (payEXP(p, amount)) {
                                    conf.set("exp", conf.getInt("exp", 0) + amount);
                                    conf.save();
                                    Main.sendMessage(p, "AddedEXPToVault", true, "AMOUNT", String.valueOf(amount));
                                    p.openInventory(GUIManager.get().getEXPVault(p));
                                } else Main.sendMessage(p, "NotEnoughEXP", true);
                                return;
                            case RED_STAINED_GLASS_PANE:
                                amount = Integer.parseInt(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName().split("\u00a77")[1].split(" ", 2)[0]);
                                if (conf.getInt("exp", 0) >= amount) {
                                    conf.set("exp", conf.getInt("exp", 0) - amount);
                                    conf.save();
                                    p.setLevel(p.getLevel() + amount);
                                    Main.sendMessage(p, "AddedEXP", true, "AMOUNT", String.valueOf(amount));
                                    p.openInventory(GUIManager.get().getEXPVault(p));
                                } else Main.sendMessage(p, "NotEnoughEXPInVault", true);
                                return;
                            case BARRIER:
                                p.openInventory(GUIManager.get().getSelectInv(p.getUniqueId(), true, false, 1));
                        }
                    }
                } else if (title.startsWith(Main.PREFIX + "Confirm Deletion")) {
                    if (e.getRawSlot() < 3 * 9) {
                        switch (mat) {
                            case LIME_STAINED_GLASS_PANE:
                                p.closeInventory();
                                if (pay(p, 1000)) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                                    Main.sendMessage(p, "ConfirmedDeletion", true);
                                    conf.set(getPath(title) + ".items", Collections.singletonList("null"));
                                    conf.save();
                                } else Main.sendMessage(p, "NotEnoughMoney", true);
                                return;
                            case RED_STAINED_GLASS_PANE:
                                p.closeInventory();
                                Main.sendMessage(p, "CanceledDeletion", true);
                        }
                    }
                } else if (title.startsWith(Main.PREFIX + "Confirm Upgrade")) {
                    if (e.getRawSlot() < 3 * 9) {
                        switch (mat) {
                            case LIME_STAINED_GLASS_PANE:
                                p.closeInventory();
                                int nLvL = conf.getInt(getPath(title + ".lvl"));
                                if (pay(p, Integer.parseInt(Main.get().getConf().getStringList("Prices").get(nLvL - 1)))) {
                                    conf.set(getPath(title + ".lvl"), nLvL + 1);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                                    Main.sendMessage(p, "UpgradingVault", true, "LVL", String.valueOf(nLvL + 1));
                                } else Main.sendMessage(p, "NotEnoughMoney", true);
                                return;
                            case RED_STAINED_GLASS_PANE:
                                p.closeInventory();
                                Main.sendMessage(p, "CanceledUpgrade", true);
                        }
                    }
                } else if (title.startsWith(Main.PREFIX + "Confirm Rename")) {
                    if (e.getRawSlot() < 3 * 9) {
                        switch (mat) {
                            case LIME_STAINED_GLASS_PANE:
                                p.closeInventory();
                                if (pay(p, 500)) {
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1, 1);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
                                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);
                                    Main.sendMessage(p, "EnterVaultName", false);
                                    chat.remove(p.getUniqueId().toString());
                                    chat.put(p.getUniqueId().toString(), title.split("#")[1].split(" ")[0]);
                                } else Main.sendMessage(p, "NotEnoughMoney", true);
                                return;
                            case RED_STAINED_GLASS_PANE:
                                p.closeInventory();
                                Main.sendMessage(p, "CanceledDeletion", true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        PlayerConfig conf = PlayerConfig.getConfig(p.getUniqueId());
        if (chat.containsKey(p.getUniqueId().toString())) {
            e.setCancelled(true);
            if (e.getMessage().matches("[a-z,A-Z,0-9,\\-,\\&.\\ ,_]{1,15}")) {
                Main.sendMessage(p, "ChangedNameTo", true, "NAME", e.getMessage());
                e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
                conf.set(chat.get(p.getUniqueId().toString()) + ".name", e.getMessage());
                chat.remove(p.getUniqueId().toString());
                conf.save();
            } else Main.sendMessage(p, "NotValidVaultName", false);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (GUIManager.get().invs.containsKey(e.getInventory())) {
            InventoryManager.get().saveInv(e.getInventory(), GUIManager.get().invs.get(e.getInventory()), e.getPlayer().getUniqueId());
        }
    }

    public boolean pay(Player p, int price) {
        if (p.isOp()) {
            return true;
        } else return Main.economy.withdrawPlayer(p, price).transactionSuccess();
    }

    public boolean payEXP(Player p, int price) {
        if (p.isOp()) {
            return true;
        } else if (p.getLevel() >= price) {
            p.setLevel(p.getLevel() - price);
            return true;
        } else {
            return false;
        }
    }

    public String getPath(String iName) {
        String lasItem = iName.split("#")[iName.split("#").length - 1];
        return (lasItem.contains(" ") ? lasItem.split(" ")[1] : "") + "." + lasItem.split(" ")[0];
    }
}
