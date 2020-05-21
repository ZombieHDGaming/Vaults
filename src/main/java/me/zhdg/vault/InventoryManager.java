package me.zhdg.vault;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InventoryManager {

    private static InventoryManager instance = null;

    public static InventoryManager get() {
        return instance == null ? instance = new InventoryManager() : instance;
    }

    public void saveInv(Inventory inv, String path, UUID id) {
        List<String> items = new ArrayList<>();
        for (ItemStack its : inv.getContents()) {
            if (its == null) items.add("null");
            else items.add(serialize(its));
        }
        PlayerConfig conf = PlayerConfig.getConfig(id);
        conf.set(path + ".items", items);
        conf.save();
    }

    public Inventory getInv(String path, String title, UUID id) {
        PlayerConfig conf = PlayerConfig.getConfig(id);
        if (!conf.contains(path + ".items")) return Bukkit.createInventory(null, conf.getInt(path + ".lvl") * 9, title);
        List<String> items = conf.getStringList(path + ".items");
        Inventory inv = Bukkit.createInventory(null, conf.getInt(path + ".lvl") * 9, title);
        int i = 0;
        for (String s : items) {
            if (!s.equalsIgnoreCase("null")) inv.setItem(i, deserialize(s));
            i++;
        }
        return inv;
    }

    public String serialize(ItemStack is) {
        try {
            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(bytesOut);
            out.writeObject(is);
            out.flush();
            out.close();
            return Base64Coder.encodeLines(bytesOut.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ItemStack deserialize(String base64) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new ByteArrayInputStream(Base64Coder.decodeLines(base64)));
            ItemStack is = (ItemStack) in.readObject();
            in.close();
            return is;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
