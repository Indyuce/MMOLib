package net.mmogroup.mmolib;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.mmogroup.mmolib.api.player.MMOPlayerData;
import net.mmogroup.mmolib.api.stat.handler.AttributeStatHandler;
import net.mmogroup.mmolib.command.ExploreAttributesCommand;
import net.mmogroup.mmolib.comp.CitizensEntityHandler;
import net.mmogroup.mmolib.comp.MyPetEntityHandler;
import net.mmogroup.mmolib.comp.MythicMobsDamageHandler;
import net.mmogroup.mmolib.comp.ShopKeepersEntityHandler;
import net.mmogroup.mmolib.gui.PluginInventory;
import net.mmogroup.mmolib.listener.DamageReduction;
import net.mmogroup.mmolib.listener.MitigationListener;
import net.mmogroup.mmolib.listener.PlayerListener;
import net.mmogroup.mmolib.listener.event.ArmorEquipEventListener;
import net.mmogroup.mmolib.listener.event.PlayerAttackEventListener;
import net.mmogroup.mmolib.manager.DamageManager;
import net.mmogroup.mmolib.manager.EntityManager;
import net.mmogroup.mmolib.version.ServerVersion;
import net.mmogroup.mmolib.version.SpigotPlugin;
import net.mmogroup.mmolib.version.nms.NMSHandler;

public class MMOLib extends JavaPlugin {
	public static MMOLib plugin;

	private NMSHandler nms;
	private final ServerVersion version = new ServerVersion(Bukkit.getServer().getClass());
	private final DamageManager damage = new DamageManager();
	private final EntityManager entity = new EntityManager();

	public void onLoad() {
		plugin = this;

		try {
			getLogger().log(Level.INFO, "Detected Bukkit Version: " + version.toString());
			nms = (NMSHandler) Class.forName("net.mmogroup.mmolib.version.nms.NMSHandler_" + version.toString().substring(1)).newInstance();
		} catch (Exception e) {
			getLogger().log(Level.INFO, "Your server version is not compatible.");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
	}

	@SuppressWarnings("deprecation")
	public void onEnable() {
		new SpigotPlugin(73855, this).checkForUpdate();

		saveDefaultConfig();

		Bukkit.getPluginManager().registerEvents(damage, this);
		Bukkit.getPluginManager().registerEvents(new DamageReduction(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new MitigationListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerAttackEventListener(), this);
		Bukkit.getPluginManager().registerEvents(new ArmorEquipEventListener(), this);

		if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null)
			damage.registerHandler(new MythicMobsDamageHandler());

		if (Bukkit.getPluginManager().getPlugin("Citizens") != null)
			entity.registerHandler(new CitizensEntityHandler());

		if (Bukkit.getPluginManager().getPlugin("ShopKeepers") != null) 
			entity.registerHandler(new ShopKeepersEntityHandler());
		
		if (Bukkit.getPluginManager().getPlugin("MyPet") != null) 
			entity.registerHandler(new MyPetEntityHandler());
		
		if (version.isStrictlyHigher(1, 12))
			getCommand("exploreattributes").setExecutor(new ExploreAttributesCommand());

		if (getConfig().getBoolean("fix-player-attributes"))
			AttributeStatHandler.updateAttributes = true;

		Bukkit.getOnlinePlayers().forEach(online -> MMOPlayerData.setup(online));
	}

	public void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory().getHolder() != null
					&& player.getOpenInventory().getTopInventory().getHolder() instanceof PluginInventory)
				player.closeInventory();
	}

	public ServerVersion getVersion() {
		return version;
	}

	public NMSHandler getNMS() {
		return nms;
	}

	public DamageManager getDamage() {
		return damage;
	}

	public EntityManager getEntities() {
		return entity;
	}
}
