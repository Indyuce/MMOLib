package net.mmogroup.mmolib.api;

import org.bukkit.entity.Entity;

public interface EntityHandler {

	/*
	 * this method lets you check if a specific entity was created using a plugin
	 * and therefore which should not be a skill target.
	 */
	boolean isInvulnerable(Entity entity);
}
