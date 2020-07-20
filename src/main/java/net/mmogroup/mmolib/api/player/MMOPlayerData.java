package net.mmogroup.mmolib.api.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.mmogroup.mmolib.api.stat.StatMap;

public class MMOPlayerData {

	/*
	 * MMOCore and MMOItems data cached for easier access. only initialized on
	 * PlayerJoinEvent at priority NORMAL or higher
	 */
	private Object mmocore, mmoitems;
	private Player player;
	private long lastLogin;

	/*
	 * stat data saved till next server startup
	 */
	private final UUID uuid;
	private final StatMap stats = new StatMap(this);
	private final Map<MitigationType, Long> cooldowns = new HashMap<>();

	private static final Map<UUID, MMOPlayerData> data = new HashMap<>();

	public MMOPlayerData(Player player) {
		this.player = player;
		this.uuid = player.getUniqueId();
	}

	@Deprecated
	public MMOPlayerData(Player player, UUID uuid) {
		this.player = player;
		this.uuid = uuid;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public StatMap getStatMap() {
		return stats;
	}
	
	public long getLastLogin() {
		return lastLogin;
	}

	/*
	 * player instances must not be saved after a player quits the server
	 * because it creates memory issues. therefore, MMOLib can check if the
	 * corresponding player from a PlayerData instance is offline or not by
	 * checking if the Player instance is cached or not
	 */
	public boolean isOnline() {
		return player != null;
	}

	/*
	 * that method can only be used after making sure that the player is on
	 */
	public Player getPlayer() {
		Validate.notNull(player, "Player must be online");
		return player;
	}

	public net.Indyuce.mmocore.api.player.PlayerData getMMOCore() {
		return (net.Indyuce.mmocore.api.player.PlayerData) mmocore;
	}

	public net.Indyuce.mmoitems.api.player.PlayerData getMMOItems() {
		return (net.Indyuce.mmoitems.api.player.PlayerData) mmoitems;
	}

	public void setMMOCore(Object mmocore) {
		this.mmocore = mmocore;
	}

	public void setMMOItems(Object mmoitems) {
		this.mmoitems = mmoitems;
	}

	public void setPlayer(Player player) {
		this.player = player;
		this.lastLogin = System.currentTimeMillis();
	}

	public void applyCooldown(MitigationType cd, double value) {
		cooldowns.put(cd, (long) (System.currentTimeMillis() + value * 1000));
	}

	public boolean isOnCooldown(MitigationType cd) {
		return cooldowns.containsKey(cd) && cooldowns.get(cd) > System.currentTimeMillis();
	}

	public static void setup(Player player) {
		if (!data.containsKey(player.getUniqueId()))
			data.put(player.getUniqueId(), new MMOPlayerData(player));
		else
			data.get(player.getUniqueId()).setPlayer(player);
	}

	public static boolean isLoaded(UUID uuid) {
		return data.containsKey(uuid);
	}

	public static MMOPlayerData get(OfflinePlayer player) {
		return data.get(player.getUniqueId());
	}

	public static MMOPlayerData get(UUID uuid) {
		return data.get(uuid);
	}

	public static Collection<MMOPlayerData> getLoaded() {
		return data.values();
	}
}
