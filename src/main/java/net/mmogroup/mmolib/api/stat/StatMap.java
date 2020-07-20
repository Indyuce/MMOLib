package net.mmogroup.mmolib.api.stat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang.Validate;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.mmogroup.mmolib.MMOLib;
import net.mmogroup.mmolib.api.AttackResult;
import net.mmogroup.mmolib.api.DamageType;
import net.mmogroup.mmolib.api.player.MMOPlayerData;
import net.mmogroup.mmolib.api.stat.handler.AttributeStatHandler;
import net.mmogroup.mmolib.api.stat.handler.MovementSpeedStatHandler;

public class StatMap {

	/*
	 * instance which saves data data from both MMOCore and MMOItems to let
	 * other plugins retrieve data
	 */
	private final MMOPlayerData data;

	private final Map<String, StatInstance> stats = new HashMap<>();

	public StatMap(MMOPlayerData player) {
		this.data = player;
	}

	public MMOPlayerData getPlayerData() {
		return data;
	}

	public double getStat(String id) {
		return getInstance(id).getTotal();
	}

	public StatInstance getInstance(String id) {
		if (stats.containsKey(id))
			return stats.get(id);

		StatInstance ins = new StatInstance(this, id);
		stats.put(id, ins);
		return ins;
	}

	public Collection<StatInstance> getInstances() {
		return stats.values();
	}

	public void updateAll() {
		updates.values().forEach(consumer -> consumer.accept(this));
	}

	public void update(String stat) {
		if (updates.containsKey(stat))
			updates.get(stat).accept(this);
	}

	public CachedStatMap cache() {
		return new CachedStatMap();
	}

	/*
	 * all updates which should be ran whenever a stat is updated so that
	 * MMOCore and MMOItems know the stat was updated.
	 */
	private static final Map<String, Consumer<StatMap>> updates = new HashMap<>();

	static {
		updates.put(SharedStat.ARMOR, new AttributeStatHandler(Attribute.GENERIC_ARMOR, SharedStat.ARMOR));
		updates.put(SharedStat.ARMOR_TOUGHNESS, new AttributeStatHandler(Attribute.GENERIC_ARMOR_TOUGHNESS, SharedStat.ARMOR_TOUGHNESS));

		updates.put(SharedStat.ATTACK_DAMAGE, new AttributeStatHandler(Attribute.GENERIC_ATTACK_DAMAGE, SharedStat.ATTACK_DAMAGE));
		updates.put(SharedStat.ATTACK_SPEED, new AttributeStatHandler(Attribute.GENERIC_ATTACK_SPEED, SharedStat.ATTACK_SPEED));
		updates.put(SharedStat.KNOCKBACK_RESISTANCE,
				new AttributeStatHandler(Attribute.GENERIC_KNOCKBACK_RESISTANCE, SharedStat.KNOCKBACK_RESISTANCE));
		updates.put(SharedStat.MAX_HEALTH, new AttributeStatHandler(Attribute.GENERIC_MAX_HEALTH, SharedStat.MAX_HEALTH));

		Consumer<StatMap> moveSpeed = new MovementSpeedStatHandler();
		updates.put(SharedStat.MOVEMENT_SPEED, moveSpeed);
		updates.put(SharedStat.SPEED_MALUS_REDUCTION, moveSpeed);
	}

	public static void registerUpdate(String stat, Consumer<StatMap> update) {
		Validate.notNull(stat, "Stat cannot be null");
		Validate.notNull(update, "StatMap update cannot be null");

		updates.put(stat, update);
	}

	/*
	 * function used to cache data stats, for example when he is casting a spell
	 * since stats could be changed when the spell is cast
	 */
	public class CachedStatMap {

		/*
		 * this field is made final so even when the player logs out, the
		 * ability can still be cast without any additional errors. this allows
		 * not to add a safe check in every ability loop.
		 */
		private final Player player;

		private final Map<String, Double> cached = new HashMap<>();

		private CachedStatMap() {
			this.player = data.getPlayer();
			for (String key : stats.keySet())
				cached.put(key, getStat(key));
		}

		public Player getPlayer() {
			return player;
		}

		public MMOPlayerData getData() {
			return data;
		}

		public double getStat(String stat) {
			return cached.getOrDefault(stat, 0d);
		}

		public void setStat(String stat, double value) {
			cached.put(stat, value);
		}

		public void damage(LivingEntity target, double value, DamageType... types) {
			MMOLib.plugin.getDamage().damage(player, target, new AttackResult(value, types));
		}
	}
}
