package net.mmogroup.mmolib.version;

import java.util.function.Predicate;

import net.mmogroup.mmolib.MMOLib;

public enum SoundServerVersion {

	/*
	 * latest version is NEVER used because it should be corresponding to the
	 * enum
	 */
	V1_16(version -> version.isStrictlyHigher(1, 15), 1),

	/*
	 * corresponds to 1.13 all the way up to 1.15, never used by convention
	 * because sound names utilize sounds from 1.13 to 1.15
	 */
	V1_13(version -> version.isStrictlyHigher(1, 12) && version.isBelowOrEqual(1, 15), 0),

	/*
	 * legacy versions i.e 1.12 because MMOItems still supports 1.12
	 */
	LEGACY(version -> version.isBelowOrEqual(1, 12), 2);

	/*
	 * versions from 1.13 to
	 */

	private final Predicate<ServerVersion> matches;

	/*
	 * index of 0 corresponds to the enum NAME, anything above is offset by 1
	 */
	private final int index;

	public static final SoundServerVersion FOUND = findVersion();

	/*
	 * SoundServerVersion is used to store what sound enum constants the server
	 * must use depending on the running spigot build; then we get the INDEX
	 * which tells what string to use in the String... from the constructor
	 */
	private SoundServerVersion(Predicate<ServerVersion> matches, int index) {
		this.matches = matches;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public boolean matches(ServerVersion version) {
		return matches.test(version);
	}

	public static SoundServerVersion findVersion() {
		ServerVersion server = MMOLib.plugin.getVersion();
		for (SoundServerVersion checked : values())
			if (checked.matches(server))
				return checked;

		// uses latest by default
		return V1_16;
	}
}
