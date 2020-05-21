package me.zhdg.vault;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.*;

public class GUIManager {

    private static GUIManager instance = null;
    public Map<Inventory, String> invs = new HashMap<>();

    private DecimalFormat format = new DecimalFormat("$###,###,###.00");

    public static GUIManager get() {
        return instance == null ? instance = new GUIManager() : instance;
    }

    public void setupBorder(int count, Inventory inv) {
        for (int i = 0; i < 9 * count; i++) {
            inv.setItem(i, getItem(Material.BLACK_STAINED_GLASS_PANE, 1, "\u00a70", null));
        }
    }

    public Inventory getVaultMain(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Main.PREFIX + "Main");

        setupBorder(3, inv);

        for (int i = 0; i < 7; i++) {
            inv.clear(getSlot(2, 2 + i));
        }

        int vaults = Main.get().getVaultsCount(p.getUniqueId());

        inv.setItem(getSlot(2, 3), getItem(Material.CHEST, 1, "\u00a7a\u00a7lVIEW ALL VAULTS", Arrays.asList("\u00a77Open the Vault Selection", "\u00a77", "\u00a7e\u00a7lVAULT COUNT: \u00a7a" + vaults)));
        inv.setItem(getSlot(2, 7), getItem(Material.ENDER_CHEST, 1, "\u00a73\u00a7lCREATE VAULT", Arrays.asList("\u00a77Create a new Vault", "\u00a77", "\u00a7e\u00a7lVAULTS LEFT: \u00a7a" + (Main.get().getMaxVaults(p) - vaults))));

        inv.setItem(getSlot(3, 5), getItem(Material.BARRIER, 1, "\u00a7c\u00a7lCLOSE MENU", Arrays.asList("\u00a77Close the Menu")));

        return inv;
    }

    public Inventory getMoneyVault(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 2, Main.PREFIX + "\u00a7a\u00a7lMONEY VAULT");

        PlayerConfig conf = PlayerConfig.getConfig(p.getUniqueId());

        setupBorder(2, inv);

        inv.setItem(getSlot(1, 1), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lADD \u00a77$5", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add $5 to your Bank", "\u00a78\u00a7m-------------------")));
        inv.setItem(getSlot(1, 2), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lADD \u00a77$10", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add $10 to your Bank", "\u00a78\u00a7m-------------------")));
        inv.setItem(getSlot(1, 3), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lADD \u00a77$100", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add $100 to your Bank", "\u00a78\u00a7m-------------------")));

        inv.setItem(getSlot(1, 5), getItem(Material.GOLD_BLOCK, 1, "\u00a7a\u00a7lSTORED BALANCE", Collections.singletonList("\u00a76$" + conf.getInt("money", 0))));

        inv.setItem(getSlot(1, 7), getItem(Material.RED_STAINED_GLASS_PANE, 1, "\u00a7c\u00a7lREMOVE \u00a77$5", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add $5 to your Economy Balance", "\u00a78\u00a7m-------------------")));
        inv.setItem(getSlot(1, 8), getItem(Material.RED_STAINED_GLASS_PANE, 1, "\u00a7c\u00a7lREMOVE \u00a77$10", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add $10 to your Economy Balance", "\u00a78\u00a7m-------------------")));
        inv.setItem(getSlot(1, 9), getItem(Material.RED_STAINED_GLASS_PANE, 1, "\u00a7c\u00a7lREMOVE \u00a77$100", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add $100 to your Economy Balance", "\u00a78\u00a7m-------------------")));

        inv.setItem(getSlot(2, 5), getItem(Material.BARRIER, 1, "\u00a7c\u00a7lRETURN TO SELECTION", Collections.singletonList("\u00a77Close Money Vault")));

        return inv;
    }

    public Inventory getEXPVault(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 2, Main.PREFIX + "\u00a7a\u00a7lEXP VAULT");

        PlayerConfig conf = PlayerConfig.getConfig(p.getUniqueId());

        setupBorder(2, inv);

        inv.setItem(getSlot(1, 1), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lADD \u00a771 EXP Level", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add 1 Lvl to your Bank", "\u00a78\u00a7m-------------------")));
        inv.setItem(getSlot(1, 2), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lADD \u00a775 EXP Level", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add 5 Lvl to your Bank", "\u00a78\u00a7m-------------------")));

        inv.setItem(getSlot(1, 5), getItem(Material.EXPERIENCE_BOTTLE, 1, "\u00a7a\u00a7lSTORED EXP LEVELS", Collections.singletonList("\u00a76" + conf.getInt("exp", 0) + " Levels")));

        inv.setItem(getSlot(1, 8), getItem(Material.RED_STAINED_GLASS_PANE, 1, "\u00a7c\u00a7lREMOVE \u00a771 EXP Level", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add 1 Lvl to your Experience", "\u00a78\u00a7m-------------------")));
        inv.setItem(getSlot(1, 9), getItem(Material.RED_STAINED_GLASS_PANE, 1, "\u00a7c\u00a7lREMOVE \u00a775 EXP Level", Arrays.asList("\u00a78\u00a7m-------------------", "\u00a77Add 5 Lvl to your Experience", "\u00a78\u00a7m-------------------")));

        inv.setItem(getSlot(2, 5), getItem(Material.BARRIER, 1, "\u00a7c\u00a7lRETURN TO SELECTION", Collections.singletonList("\u00a77Close Experience Vault")));

        return inv;
    }

    public Inventory getUpgradeInv(Player p, int id) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Main.PREFIX + "Vault #" + id + " Menu");

        PlayerConfig conf = PlayerConfig.getConfig(p.getUniqueId());

        setupBorder(3, inv);

        for (int i = 0; i < 7; i++) {
            inv.clear(getSlot(2, 2 + i));
        }
        if (conf.getInt(id + ".lvl") + 1 < 6) {
            inv.setItem(getSlot(2, 3), getItem(Material.NAME_TAG, 1, "\u00a7a\u00a7lRENAME VAULT", Arrays.asList("\u00a77Rename this Vault", "\u00a77Recommended if you are that OCD person... we don't judge.", "\u00a77", "\u00a7e\u00a7lCOST: \u00a7a$500")));
            inv.setItem(getSlot(2, 5), getItem(Material.END_PORTAL_FRAME, 1, "\u00a74\u00a7lEMPTY VAULT", Arrays.asList("\u00a77Empties this Vault out", "\u00a7c\u00a7lWARNING:", "\u00a7cThis cannot be undone.", "\u00a7cEverything will be deleted!", "\u00a77", "\u00a7e\u00a7lCOST: \u00a7a$1,000")));
            inv.setItem(getSlot(2, 7), getItem(Material.ENDER_CHEST, 1, "\u00a7b\u00a7lUPGRADE VAULT", Arrays.asList("\u00a77Upgrade this Vault to add more slots.", "\u00a76Current Vault Size: \u00a77" + conf.getInt(id + ".lvl") * 9 + " Slots", "\u00a77", "\u00a7e\u00a7lCOST: \u00a7a" + format.format(Integer.parseInt(Main.get().getConf().getStringList("Prices").get(conf.getInt(id + ".lvl") - 1))))));

            inv.setItem(getSlot(3, 5), getItem(Material.BARRIER, 1, "\u00a7c\u00a7lCLOSE UPGRADE MENU", Collections.singletonList("\u00a77Close the Upgrade Menu")));
        } else {
            inv.setItem(getSlot(2, 3), getItem(Material.NAME_TAG, 1, "\u00a7a\u00a7lRENAME VAULT", Arrays.asList("\u00a77Rename this Vault", "\u00a77Recommended if you are that OCD person... we don't judge.", "\u00a77", "\u00a7e\u00a7lCOST: \u00a7a$500")));
            inv.setItem(getSlot(2, 7), getItem(Material.END_PORTAL_FRAME, 1, "\u00a74\u00a7lEMPTY VAULT", Arrays.asList("\u00a77Empties this Vault out", "\u00a7c\u00a7lWARNING:", "\u00a7cThis cannot be undone.", "\u00a7cEverything will be deleted!", "\u00a77", "\u00a7e\u00a7lCOST: \u00a7a$1,000")));

            inv.setItem(getSlot(3, 5), getItem(Material.BARRIER, 1, "\u00a7c\u00a7lCLOSE UPGRADE MENU", Collections.singletonList("\u00a77Close the Upgrade Menu")));
        }

        return inv;
    }

    public Inventory getDeleteVerification(String name, int id) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Main.PREFIX + "Confirm Deletion #" + id + (name != null ? " " + name : ""));

        setupBorder(3, inv);

        inv.setItem(getSlot(2, 5), getItem(Material.END_PORTAL_FRAME, 1, "\u00a74\u00a7lCLEAR VAULT", null));
        for (int i = 0; i < 3; i++)
            inv.setItem(getSlot(2, 1 + i), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lCONFIRM", Collections.singletonList("\u00a77Confirm Deletion")));
        for (int i = 0; i < 3; i++)
            inv.setItem(getSlot(2, 7 + i), getItem(Material.RED_STAINED_GLASS_PANE, 1, "\u00a7c\u00a7lCANCEL", Collections.singletonList("\u00a77Cancel Deletion")));
        return inv;
    }

    public Inventory getRenameVerification(String name, int id) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Main.PREFIX + "Confirm Rename #" + id + (name != null ? " " + name : ""));

        setupBorder(3, inv);

        inv.setItem(getSlot(2, 5), getItem(Material.NAME_TAG, 1, "\u00a7a\u00a7lRENAME VAULT", null));
        for (int i = 0; i < 3; i++)
            inv.setItem(getSlot(2, 1 + i), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lCONFIRM", Collections.singletonList("\u00a77Confirm Rename")));
        for (int i = 0; i < 3; i++)
            inv.setItem(getSlot(2, 7 + i), getItem(Material.RED_STAINED_GLASS_PANE, 1, "\u00a7c\u00a7lCANCEL", Collections.singletonList("\u00a77Cancel Rename")));
        return inv;
    }

    public Inventory getUpgradeVerification(Player p, String name, int id) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, Main.PREFIX + "Confirm Upgrade #" + id + (name != null ? " " + name : ""));

        PlayerConfig conf = PlayerConfig.getConfig(p.getUniqueId());

        setupBorder(3, inv);

        inv.setItem(getSlot(2, 5), getItem(Material.ENDER_CHEST, 1, "\u00a7b\u00a7lUPGRADE VAULT", Arrays.asList("\u00a77Upgrade this Vault to add more slots.", "\u00a76Current Vault Size: \u00a77" + conf.getInt(id + ".lvl") * 9 + " Slots", "\u00a77", "\u00a7e\u00a7lCOST: \u00a7a" + format.format(Integer.parseInt(Main.get().getConf().getStringList("Prices").get(conf.getInt(id + ".lvl") - 1))))));
        for (int i = 0; i < 3; i++)
            inv.setItem(getSlot(2, 1 + i), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lCONFIRM", Collections.singletonList("\u00a77Confirm Upgrade")));
        for (int i = 0; i < 3; i++)
            inv.setItem(getSlot(2, 7 + i), getItem(Material.RED_STAINED_GLASS_PANE, 1, "\u00a7c\u00a7lCANCEL", Collections.singletonList("\u00a77Cancel Upgrade")));
        return inv;
    }

    public Inventory getSelectInv(UUID name, boolean own, boolean edit, int page) {
        Inventory inv = Bukkit.createInventory(null, 9 * 4, Main.PREFIX + (edit ? "Select Vault" : "Selection"));

        PlayerConfig conf = PlayerConfig.getConfig(name);

        setupBorder(4, inv);

        for (int i = 1; i <= 9; i++) {
            int vault = (page - 1) * 7 + i; //+(page != 1 ? 1 : 0)
            if (page != 1 && i == 1) {
                inv.setItem(getSlot(2, i), getItem(Material.WHITE_STAINED_GLASS_PANE, page - 1, "\u00a76\u00a7l< \u00a7ePrevious Page", Collections.singletonList("\u00a77Page " + (page - 1))));
            } else if (conf.contains(vault + ".lvl")) {
                if (i != 9 || !conf.contains(Integer.toString(vault + 1))) {
                    ConfigurationSection cs = conf.getConfigurationSection((Integer.toString(vault)));
                    int lvl = cs.getInt("lvl");

                    List<String> contents = cs.getStringList("items");
                    int j = 0;
                    for (String str : contents) if (!str.equals("null")) j++;

                    int prog = Double.valueOf(100.0 / (lvl * 9) * j).intValue();
                    if (conf.contains(vault + ".name")) {
                        inv.setItem(getSlot(2, i), getItem(Material.CHEST, 1, "\u00a7e\u00a7l" + cs.getString("name", "") + " \u00a77#" + vault,
                                Arrays.asList("\u00a77Vault Level: \u00a7e" + lvl,
                                        "\u00a77Date Created: \u00a7e" + cs.getString("date"),
                                        "\u00a77Space Left:",
                                        Main.get().getProgBar(prog) + " \u00a77[\u00a7a" + prog + "%\u00a77]"
                                )));
                    } else {
                        inv.setItem(getSlot(2, i), getItem(Material.CHEST, 1, "\u00a76\u00a7lVAULT \u00a77#" + vault,
                                Arrays.asList("\u00a77Vault Level: \u00a7e" + lvl,
                                        "\u00a77Date Created: \u00a7e" + cs.getString("date"),
                                        "\u00a77Space Left:",
                                        Main.get().getProgBar(prog) + " \u00a77[\u00a7a" + prog + "%\u00a77]"
                                )));
                    }
                } else {
                    inv.setItem(getSlot(2, i), getItem(Material.WHITE_STAINED_GLASS_PANE, page + 1, "\u00a7eNext Page \u00a76\u00a7l>", Collections.singletonList("\u00a77Page " + (page + 1))));
                }
            }
        }

        if (own) {
            if (!edit)
                inv.setItem(getSlot(3, 5), getItem(Material.ANVIL, 1, "\u00a76\u00a7lEDIT MODE", Collections.singletonList("\u00a77Edit/Upgrade a Vault(s)")));
            else
                inv.setItem(getSlot(4, 5), getItem(Material.REDSTONE_BLOCK, 1, "\u00a7c\u00a7lEXIT EDIT MODE", Collections.singletonList("\u00a77Return to Vault View Mode")));
        }
        if (!edit)
            inv.setItem(getSlot(4, 5), getItem(Material.BARRIER, 1, "\u00a7c\u00a7lRETURN TO VAULT MENU", Collections.singletonList("\u00a77Close the Selection Menu")));
        if (own && !edit)
            inv.setItem(getSlot(4, 2), getItem(Material.LIME_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lMoney Vault", Arrays.asList("\u00a77Store Un-needed Money.", "\u00a77Never be afraid to lose money during battle again.", "\u00a7c\u00a7lMineTec Secured", "\u00a77", "\u00a7e\u00a7lMONEY STORED: \u00a7a$" + conf.getInt("money", 0))));
        if (own && !edit)
            inv.setItem(getSlot(4, 8), getItem(Material.YELLOW_STAINED_GLASS_PANE, 1, "\u00a7a\u00a7lEXP Vault", Arrays.asList("\u00a77Store Un-needed Experience.", "\u00a77Always have Experience ready to go.", "\u00a7c\u00a7lMineTec Secured", "\u00a77", "\u00a7e\u00a7lEXP STORED: \u00a7a" + conf.getInt("exp", 0) + " Levels")));
        return inv;
    }

    public ItemStack getItem(Material mat, Integer size, String name, List<String> lore) {
        ItemStack i = new ItemStack(mat, size);
        ItemMeta m = i.getItemMeta();
        m.setDisplayName(name);
        m.setLore(lore);
        i.setItemMeta(m);
        return i;
    }

    public int getSlot(int row, int slot) {
        return ((row - 1) * 9) + (slot - 1);
    }

    public Inventory getVault(String path, UUID id) {
        PlayerConfig conf = PlayerConfig.getConfig(id);
        int lvl = conf.getInt(path + ".lvl");

        List<String> contents = conf.getStringList(path + ".items");
        int j = 0;
        for (String str : contents) if (!str.equals("null")) j++;

        int prog = Double.valueOf(100.0 / (lvl * 9) * j).intValue();

        String name = conf.getString(path + ".name");
        Inventory inv = InventoryManager.get().getInv(path,
                name == null ?
                        Main.getMessage("VaultUnnamed", "ID", path.split("\\.")[1], "PROG_BAR", Main.get().getProgBar(prog)) :
                        Main.getMessage("VaultNamed", "NAME", name, "ID", path.split("\\.")[1], "PROG_BAR", Main.get().getProgBar(prog)), id);
        GUIManager.get().invs.put(inv, path);
        return inv;
    }
}