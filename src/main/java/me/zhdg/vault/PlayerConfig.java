package me.zhdg.vault;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerConfig extends YamlConfiguration {
    private static final Map<UUID, PlayerConfig> configs = new HashMap<>();

    private File file = null;
    private final Object saveLock = new Object();
    private UUID uuid;

    public static PlayerConfig getConfig(UUID uuid) {
        synchronized (configs) {
            if (configs.containsKey(uuid)) {
                return configs.get(uuid);
            }
            PlayerConfig config = new PlayerConfig(uuid);
            configs.put(uuid, config);
            return config;
        }
    }

    public void removeConfigs() {
        Collection<PlayerConfig> oldConfs = new ArrayList<>(configs.values());
        synchronized (configs) {
            for (PlayerConfig config : oldConfs) {
                config.discard(true);
            }
        }
    }

    public PlayerConfig(UUID uuid) {
        super();
        file = new File(Main.getPlugin(Main.class).getDataFolder(), "Players" + File.separator + uuid.toString() + ".yml");
        this.uuid = uuid;
        reload();
    }

    @SuppressWarnings("unused")
    private PlayerConfig() {
        uuid = null;
    }

    private void reload() {
        synchronized (saveLock) {
            try {
                load(file);
            } catch (Exception ignore) {
            }
        }
    }

    public void save() {
        synchronized (saveLock) {
            try {
                save(file);
            } catch (Exception ignore) {
            }
        }
    }

    public void discard(boolean save) {
        if (save) {
            save();
        }
        synchronized (configs) {
            configs.remove(uuid);
        }
    }
}
