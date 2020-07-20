package net.mmogroup.mmolib.api;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.LivingEntity;

public class RegisteredAttack {
	private final AttackResult result;
	private final LivingEntity damager;

	public RegisteredAttack(AttackResult result, LivingEntity damager) {
		// Validate.notNull(damager, "Damager cannot be null");
		Validate.notNull(result, "Attack cannot be null");

		this.result = result;
		this.damager = damager;
	}

	public AttackResult getResult() {
		return result;
	}

	public LivingEntity getDamager() {
		return damager;
	}
}
