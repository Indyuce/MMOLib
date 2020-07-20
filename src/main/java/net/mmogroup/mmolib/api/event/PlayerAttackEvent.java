package net.mmogroup.mmolib.api.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.mmogroup.mmolib.MMOLib;
import net.mmogroup.mmolib.api.AttackResult;
import net.mmogroup.mmolib.api.DamageType;
import net.mmogroup.mmolib.api.player.MMOPlayerData;

public class PlayerAttackEvent extends MMODataEvent implements Cancellable {
	private static final HandlerList handlers = new HandlerList();

	private final EntityDamageByEntityEvent event;
	private final AttackResult attack;

	public PlayerAttackEvent(MMOPlayerData data, EntityDamageByEntityEvent event, AttackResult attack) {
		super(data);

		this.event = event;
		this.attack = attack;
	}

	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}

	@Override
	public void setCancelled(boolean value) {
		event.setCancelled(value);
	}

	public AttackResult getAttack() {
		return attack;
	}

	public boolean isWeapon() {
		return attack.getTypes().contains(DamageType.WEAPON);
	}

	public LivingEntity getEntity() {
		return (LivingEntity) event.getEntity();
	}

	/*
	 * returns if the event is called by custom damage like staffs, abilities
	 * etc or any non vanilla damage source
	 */
	public boolean isCustomDamage() {
		return MMOLib.plugin.getDamage().hasDamage(event.getEntity());
	}

	@Deprecated
	public double getDamage() {
		return attack.getDamage();
	}

	@Deprecated
	public void setDamage(double value) {
		attack.setDamage(value);
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
