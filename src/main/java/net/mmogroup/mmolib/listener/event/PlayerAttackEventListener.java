package net.mmogroup.mmolib.listener.event;

import org.bukkit.Bukkit;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import net.mmogroup.mmolib.MMOLib;
import net.mmogroup.mmolib.api.AttackResult;
import net.mmogroup.mmolib.api.DamageType;
import net.mmogroup.mmolib.api.RegisteredAttack;
import net.mmogroup.mmolib.api.event.EntityKillEntityEvent;
import net.mmogroup.mmolib.api.event.PlayerAttackEvent;
import net.mmogroup.mmolib.api.player.MMOPlayerData;

public class PlayerAttackEventListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void a(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Damageable))
			return;

		/*
		 * looks for a registered attack from MMOLib damage system, or
		 * initializes an attack based either on a melee or projectile vanilla
		 * attack
		 */
		RegisteredAttack attack = getAttack(event);

		/*
		 * if the damager cannot be found, no PlayerAttackEvent should be called
		 */
		if (attack == null || attack.getDamager() == null)
			return;

		/*
		 * check damage systems from other MMOCore plugins + from MMOCore, and
		 * register an attack damage for easier plugin calculations
		 */
		if (attack.getDamager() instanceof Player && !attack.getDamager().hasMetadata("NPC")) {

			PlayerAttackEvent attackEvent = new PlayerAttackEvent(MMOPlayerData.get((Player) attack.getDamager()), event, attack.getResult());
			Bukkit.getPluginManager().callEvent(attackEvent);
			if (attackEvent.isCancelled())
				return;

			event.setDamage(attack.getResult().getDamage());
		}

		/*
		 * checks for killing
		 */
		if (event.getFinalDamage() >= ((Damageable) event.getEntity()).getHealth())
			Bukkit.getPluginManager().callEvent(new EntityKillEntityEvent(attack.getDamager(), event.getEntity()));
	}

	private RegisteredAttack getAttack(EntityDamageByEntityEvent event) {

		/*
		 * check MMOLib registered attacks database and updates damage dealt
		 * based on the value given by the Bukkit event
		 */
		RegisteredAttack custom = MMOLib.plugin.getDamage().findInfo(event.getEntity());
		if (custom != null) {
			custom.getResult().setDamage(event.getDamage());
			return custom;
		}

		/*
		 * check direct damager
		 */
		if (event.getDamager() instanceof LivingEntity)
			return new RegisteredAttack(new AttackResult(event.getDamage(), DamageType.WEAPON, DamageType.PHYSICAL),
					(LivingEntity) event.getDamager());

		/*
		 * checks projectile and add damage type, which supports every vanilla
		 * projectile like snowballs, tridents and arrows
		 */
		if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof LivingEntity)
				return new RegisteredAttack(new AttackResult(event.getDamage(), DamageType.WEAPON, DamageType.PHYSICAL, DamageType.PROJECTILE),
						(LivingEntity) proj.getShooter());
		}

		/*
		 * check for last damage
		 */
		// if (event.getEntity().getLastDamageCause() instanceof
		// EntityDamageByEntityEvent && checkLastDamageCause)
		// return getDamager(result, (EntityDamageByEntityEvent)
		// event.getEntity().getLastDamageCause(), false);

		return null;
	}
}
