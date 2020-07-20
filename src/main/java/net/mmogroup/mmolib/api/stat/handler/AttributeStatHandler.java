package net.mmogroup.mmolib.api.stat.handler;

import java.util.Iterator;
import java.util.function.Consumer;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;

import net.mmogroup.mmolib.api.stat.StatInstance;
import net.mmogroup.mmolib.api.stat.StatMap;

public class AttributeStatHandler implements Consumer<StatMap> {
	private final Attribute attribute;
	private final String stat;

	@Deprecated
	public static boolean updateAttributes;

	public AttributeStatHandler(Attribute attribute, String stat) {
		this.attribute = attribute;
		this.stat = stat;
	}

	@Override
	public void accept(StatMap stats) {
		AttributeInstance ins = stats.getPlayerData().getPlayer().getAttribute(attribute);
		removeModifiers(ins);

		if (updateAttributes)
			ins.setBaseValue(ins.getDefaultValue());

		/*
		 * if the attribute is a default attribute, substract default value from
		 * it so that it compensates it
		 */
		StatInstance statIns = stats.getInstance(stat);
		double d = statIns.getTotal();
		if (d != statIns.getVanilla())
			ins.addModifier(new AttributeModifier("mmolib.main", d - statIns.getVanilla(), Operation.ADD_NUMBER));
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