package me.zhdg.vault;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener{
	private static Main instance = null;

	public static Main get(){
		return instance;
	}
	
	public static File depend = new File("/MVdWPlaceholderAPI.jar");
	
	public static File msgFile;
	public static YamlConfiguration msg;

	private static File confFile;
	private static YamlConfiguration conf;

	public YamlConfiguration getConf() {
		return conf;
	}

	public static Economy economy = null;

	public static String PREFIX = "\u00a78[\u00a76\u00a7lV.A.U.L.T.S.\u00a78] \u00a7e\u00a7l» \u00a76\u00a7l";

	@Override
	public void onEnable() {
		instance = this;

		confFile = new File(Main.get().getDataFolder(), "config.yml");
		msgFile = new File(Main.get().getDataFolder(), "messages.yml");

		if(!setupEconomy()){
			System.out.println("WARNING! Failed to setup Economy | Vault is needed for this plugin to work");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		if (!msgFile.exists()) {
			this.saveResource("messages.yml", true);
		}
		msg = YamlConfiguration.loadConfiguration(msgFile);

		if (!confFile.exists()) {
			this.saveResource("config.yml", true);
		}
		conf = YamlConfiguration.loadConfiguration(confFile);
		PREFIX = getMessage("InvPrefix");

		System.out.println(" ");
		System.out.println("==================================================================");
		System.out.println("Enabling V.A.U.L.T.S. v." + this.getDescription().getVersion());
		System.out.println("Designed and Maintained by ZombieHDGaming");
		System.out.println("==================================================================");
		System.out.println(" ");
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(EventListener.get(), this);

		if(getServer().getPluginManager().isPluginEnabled("MVdWPlaceholderAPI") && depend.exists()){
			PlaceholderAPI.registerPlaceholder(this, "vaults_progress_exp", e -> {
				PlayerConfig conf = PlayerConfig.getConfig(e.getOfflinePlayer().getUniqueId());
				return String.valueOf(conf.getInt("exp", 0));
			});
			PlaceholderAPI.registerPlaceholder(this, "vaults_progress_money", e -> {
				PlayerConfig conf = PlayerConfig.getConfig(e.getOfflinePlayer().getUniqueId());
				return String.valueOf(conf.getInt("money", 0));
			});

			VaultProgPlaceholder vpp = new VaultProgPlaceholder();
			for (int i = 1; i <= 100; i++) PlaceholderAPI.registerPlaceholder(this, "vaults_progress_"+i, vpp);
		}
	}

	@Override
	public void onDisable() {
		PlayerConfig conf = new PlayerConfig(new UUID(0, 0));
		conf.removeConfigs();
		System.out.println(" ");
		System.out.println("==================================================================");
		System.out.println("Disabling V.A.U.L.T.S. v." + this.getDescription().getVersion());
		System.out.println("Designed and Maintained by ZombieHDGaming");
		System.out.println("==================================================================");
		System.out.println(" ");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String cmdlable, String[] args){
		if(sender instanceof Player) {
			Player p = (Player) sender;
	        PlayerConfig conf = PlayerConfig.getConfig(p.getUniqueId());
			if(cmd.getName().equalsIgnoreCase("systemstatus")) {
				if(p.hasPermission("vaults.status")) {
					File file = new File(getDataFolder() + File.separator + "Players" + File.separator);
					long size = folderSize(file);
					p.sendMessage("\u00a78\u00a7m------------------------------");
					p.sendMessage("\u00a7aCurrent Storage Size:");
					p.sendMessage("\u00a77" + systemSize(size, true));
					p.sendMessage("\u00a78\u00a7m------------------------------");
				}
			}
			if(cmd.getName().equalsIgnoreCase("vault")){
				if(args.length == 0){
					if(p.hasPermission("vaults.open"))
						p.openInventory(GUIManager.get().getVaultMain(p));
					else sendMessage(p, "NoPerms", true);
				}else{
					if(args[0].equalsIgnoreCase("open") && args.length == 2){
						if(args[1].matches("[0-9]*")){
							if(p.hasPermission("vaults.open."+args[1])){
								if(conf.contains(args[1])){
									p.playSound(p.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 1);
									p.openInventory(GUIManager.get().getVault(args[1], p.getUniqueId()));
								}else sendMessage(p, "NoVaultID", true);
							}else sendMessage(p, "NoPerms", true);
						}else if(conf.contains(args[1])){
							if(p.hasPermission("vaults.players")){
								p.openInventory(GUIManager.get().getSelectInv(Objects.requireNonNull(Bukkit.getPlayer(args[1])).getUniqueId(), false, false, 1));
							}else sendMessage(p, "NoPerms", true);
						}else sendMessage(p, "PlayerHasNoVault", true, "PLAYER", args[1]);
					}else if(args[0].equalsIgnoreCase("clear")){
						if(args.length >= 2 && args[1].matches("[0-9]*")){
							if(args.length == 2){
								if(p.hasPermission("vaults.clear."+args[1])){
									if(conf.contains(args[1])){
										p.openInventory(GUIManager.get().getDeleteVerification(p.getUniqueId().toString(), Integer.parseInt(args[1])));
									}else sendMessage(p, "NoVaultID", true);
								}else sendMessage(p, "NoPerms", true);
							}else{
								if(p.hasPermission("vaults.players.clear")){
									Player p2 = Bukkit.getPlayer(args[2]);
									PlayerConfig pconf = new PlayerConfig(Objects.requireNonNull(p2).getUniqueId());
									if(pconf.contains(args[1])){
										p.openInventory(GUIManager.get().getDeleteVerification(args[2], Integer.parseInt(args[1])));
									}else sendMessage(p, "NoVaultID", true);
								}else sendMessage(p, "NoPerms", true);
							}
						}else sendMessage(p, "ClearUsage", true);
					}else if(args[0].equalsIgnoreCase("reload")){
						if(p.hasPermission("vaults.reload")){
							Main.conf = YamlConfiguration.loadConfiguration(confFile);
							msg = YamlConfiguration.loadConfiguration(msgFile);
							sendMessage(p, "Reloaded", true);
						}else sendMessage(p, "NoPerms", true);
					}else if(args[0].equalsIgnoreCase("about")){
						if(p.hasPermission("vaults.about")){
							for(String s : conf.getStringList("About")){
								p.sendMessage(s.replace('&', '\u00a7'));
							}
						}else sendMessage(p, "NoPerms", true);
					}
				}
			}
		}
		return true;
	}

	public int getMaxVaults(Player p){
		for (int i = 25; i >= 0; i--) {
			if(p.hasPermission("vaults.create."+i)) return i;
		}
		return 0;
	}

	public String getProgBar(int percent){
		StringBuilder str = new StringBuilder("\u00a7a");
		for (int i = 1; i <= 10; i++) {
			if(percent >= i*10) str.append("\u258D");
			else str.append("\u00a78\u258D");
		}
		return str.toString();
	}

	public int getVaultsCount(UUID name) {
        PlayerConfig conf = PlayerConfig.getConfig(name);
        		
		int vaults = 0;
		for (int i = 100; i > 0; i--) {
			if(conf.contains(Integer.toString(i))){
				vaults = i;
				break;
			}
		}
		return vaults;
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	public static String getMessage(String path, String... parameters){
		String str = conf.getString(path);
		if(str == null){
			str = "Failed! See console for details!";
			System.out.println("===============================================================");
			System.out.println("Error while getting message from config.yml");
			System.out.println(" ");
			System.out.println("In order to solve this problem:");
			System.out.println(" - Please Check Config.yml for any missing or double \"or '");
			System.out.println(" - Validate YAML at yamllint.com for config.yml");
			System.out.println(" - Delete config.yml and restart server");
			System.out.println("===============================================================");
		}else str = replace(str, parameters).replace('&', '\u00a7');
		return str;
	}
	
	public static long folderSize(File directory) {
	    long length = 0;
	    for (File file : Objects.requireNonNull(directory.listFiles())) {
	        if (file.isFile())
	            length += file.length();
	        else
	            length += folderSize(file);
	    }
	    return length;
	}
	
	public static String systemSize(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static void sendMessage(Player p, String path, boolean prefix, String... parameters){
		p.sendMessage((prefix ? getMessage("Prefix")+" " : "")+getMessage(path, parameters));
	}

	private static String replace(String str, String... parameters){
		for(int i = 0; i < parameters.length-1; i = i+2) str = str.replaceAll("%"+parameters[i]+"%", parameters[i+1]);
		return str;
	}
}
