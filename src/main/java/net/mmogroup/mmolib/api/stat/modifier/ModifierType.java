package net.mmogroup.mmolib.api.stat.modifier;

public enum ModifierType {

	/*
	 * use this modifier type to multiply the entire stat value by X%. these
	 * modifiers scale on how much stat value the player already has.
	 */
	RELATIVE,

	/*
	 * used to add a flat numeric value to a certain stat. the result does not
	 * depend on the current stat value
	 */
	FLAT;
}
