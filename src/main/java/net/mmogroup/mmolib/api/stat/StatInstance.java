package net.mmogroup.mmolib.api.stat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import net.mmogroup.mmolib.api.stat.modifier.Closable;
import net.mmogroup.mmolib.api.stat.modifier.ModifierType;
import net.mmogroup.mmolib.api.stat.modifier.StatModifier;
import net.mmogroup.mmolib.api.stat.modifier.TemporaryStatModifier;

public class StatInstance {
	private final StatMap map;
	private final String stat;
	private final Map<String, StatModifier> modifiers = new HashMap<>();

	/*
	 * saves default value if the stat is a vanilla stat because the stat needs
	 * to take the default value into account
	 */
	private final double vanilla;

	public StatInstance(StatMap map, String stat) {
		this.map = map;
		this.stat = stat;
		this.vanilla = getAttributeDefaultValue(stat);
	}

	public StatMap getMap() {
		return map;
	}

	public String getStat() {
		return stat;
	}

	public double getVanilla() {
		return vanilla;
	}

	/*
	 * 1) two types of attributes: flat attributes which add X to the value, and
	 * relative attributes which add X% and which must be applied afterwards 2)
	 * the 'd' parameter lets you choose if the relative attributes also apply
	 * on the vanilla stat, or if they only apply on the extra stat value
	 */
	public double getTotal() {
		double d = vanilla;

		for (StatModifier attr : modifiers.values())
			if (attr.getType() == ModifierType.FLAT)
				d += attr.getValue();

		for (StatModifier attr : modifiers.values())
			if (attr.getType() == ModifierType.RELATIVE)
				d *= 1 + attr.getValue() / 100;

		return d;
	}

	/*
	 * allows to apply a certain function to all the stat modifiers before
	 * calculating the stat value, this can be used for instance to decrease a
	 * certain type of debuffs.
	 */
	public double getTotal(Function<StatModifier, StatModifier> modification) {
		double d = vanilla;

		for (StatModifier attr : modifiers.values())
			if (attr.getType() == ModifierType.FLAT)
				d += modification.apply(attr).getValue();

		for (StatModifier attr : modifiers.values())
			if (attr.getType() == ModifierType.RELATIVE)
				d *= 1 + modification.apply(attr).getValue() / 100;

		return d;
	}

	public StatModifier getAttribute(String key) {
		return modifiers.get(key);
	}

	public void addModifier(String key, double value) {
		addModifier(key, new StatModifier(value));
	}

	public void applyTemporaryModifier(String key, StatModifier modifier, long duration) {
		addModifier(key, new TemporaryStatModifier(modifier.getValue(), duration, modifier.getType(), key, this));
	}

	public void addModifier(String key, StatModifier modifier) {
		modifiers.put(key, modifier);

		map.update(stat);
	}

	public Collection<StatModifier> getModifiers() {
		return modifiers.values();
	}

	public Set<String> getKeys() {
		return modifiers.keySet();
	}

	public void removeIf(Predicate<String> condition) {
		for (Iterator<String> iterator = modifiers.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			if (condition.test(key))
				iterator.remove();
		}
	}

	public boolean contains(String key) {
		return modifiers.containsKey(key);
	}

	public void remove(String key) {

		if (!modifiers.containsKey(key))
			return;

		/*
		 * closing stat is really important with temporary stats because
		 * otherwise the runnable will try to remove the key from the map even
		 * though the attribute was cancelled before hand
		 */
		StatModifier mod = modifiers.get(key);
		if (mod instanceof Closable)
			((Closable) mod).close();

		modifiers.remove(key);
		map.update(stat);
	}

	@Deprecated
	private static double getAttributeDefaultValue(String stat) {
		if (stat.equals("ATTACK_DAMAGE"))
			return 1;
		if (stat.equals("ATTACK_SPEED"))
			return 4;
		if (stat.equals("MAX_HEALTH"))
			return 20;
		if (stat.equals("MOVEMENT_SPEED"))
			return .1;
		return 0;
	}
}
