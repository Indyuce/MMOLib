package net.mmogroup.mmolib.api.stat.handler;

import java.util.Iterator;
import java.util.function.Consumer;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;

import net.mmogroup.mmolib.api.stat.StatMap;

public class MovementSpeedStatHandler implements Consumer<StatMap> {

	@SuppressWarnings("deprecation")
	@Override
	public void accept(StatMap stats) {
		AttributeInstance ins = stats.getPlayerData().getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
		removeModifiers(ins);

		/*
		 * calculate speed malus reduction (capped at 80%)
		 */
		double coef = 1 - Math.min(.8, Math.max(0, stats.getInstance("SPEED_MALUS_REDUCTION").getTotal() / 100));

		// fix player walk speed
		if (AttributeStatHandler.updateAttributes)
			stats.getPlayerData().getPlayer().setWalkSpeed(.2f);

		/*
		 * unlike other attributes, MMOLib directly applies movement speed as
		 * base value which is an important compatibility issue, can't see
		 * anything better as of right now
		 */
		ins.setBaseValue(stats.getInstance("MOVEMENT_SPEED").getTotal(mod -> mod.getValue() < 0 ? mod.multiply(coef) : mod));
	}

	/*
	 * TODO remove mmoitems. in 1 year when corrupted data is gone
	 */
	private void removeModifiers(AttributeInstance ins) {
		for (Iterator<AttributeModifier> iterator = ins.getModifiers().iterator(); iterator.hasNext();) {
			AttributeModifier attribute = iterator.next();
			if (attribute.getName().startsWith("mmolib.") || attribute.getName().startsWith("mmoitems."))
				ins.removeModifier(attribute);
		}
	}
}