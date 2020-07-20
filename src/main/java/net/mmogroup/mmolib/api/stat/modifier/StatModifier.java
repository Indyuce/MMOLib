package net.mmogroup.mmolib.api.stat.modifier;

import org.apache.commons.lang.Validate;

import net.Indyuce.mmocore.MMOCore;

public class StatModifier {
	private final double d;
	private final ModifierType type;

	public StatModifier(double d) {
		this(d, ModifierType.FLAT);
	}

	public StatModifier(double d, ModifierType type) {
		this.d = d;
		this.type = type;
	}

	public StatModifier(StatModifier mod) {
		this(mod.d, mod.type);
	}

	public StatModifier(String str) {
		Validate.notNull(str, "String cannot be null");
		Validate.notEmpty(str, "String cannot be empty");

		type = str.toCharArray()[str.length() - 1] == '%' ? ModifierType.RELATIVE : ModifierType.FLAT;
		d = Double.parseDouble(type == ModifierType.RELATIVE ? str.substring(0, str.length() - 1) : str);
	}

	/*
	 * used for instance for party stat modifiers which scale with the amount of
	 * players in the party
	 */
	public StatModifier multiply(double coef) {
		return new StatModifier(d * coef, type);
	}

	public ModifierType getType() {
		return type;
	}

	public double getValue() {
		return d;
	}

	@Override
	public String toString() {
		return MMOCore.plugin.configManager.decimal.format(d) + (type == ModifierType.RELATIVE ? "%" : "");
	}
}
