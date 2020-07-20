package net.mmogroup.mmolib.comp;

import org.bukkit.entity.Entity;

import net.mmogroup.mmolib.api.AttackResult;
import net.mmogroup.mmolib.api.DamageHandler;
import net.mmogroup.mmolib.api.DamageType;
import net.mmogroup.mmolib.api.RegisteredAttack;

public class MythicMobsDamageHandler implements DamageHandler {

	@Override
	public RegisteredAttack getDamage(Entity entity) {
		return new RegisteredAttack(new AttackResult(0, DamageType.MAGIC), null);
	}

	@Override
	public boolean hasDamage(Entity entity) {
		return entity.hasMetadata("skill-damage");
	}
}
