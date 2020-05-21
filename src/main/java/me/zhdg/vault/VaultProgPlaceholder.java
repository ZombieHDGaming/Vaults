package me.zhdg.vault;

import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;

import java.util.List;

public class VaultProgPlaceholder implements PlaceholderReplacer {
    @Override
    public String onPlaceholderReplace(PlaceholderReplaceEvent e) {
        PlayerConfig conf = PlayerConfig.getConfig(e.getOfflinePlayer().getUniqueId());
        String path = e.getPlaceholder().split("_")[2];
        int lvl = conf.getInt(path+".lvl");

        List<String> contents = conf.getStringList(path+".items");
        int j = 0;
        for(String str : contents) if(!str.equals("null")) j++;

        int prog = Double.valueOf(100.0/(lvl*9)*j).intValue();

        return Main.get().getProgBar(prog);
    }
}
