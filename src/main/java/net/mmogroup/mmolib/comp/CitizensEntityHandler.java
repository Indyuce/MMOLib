package net.mmogroup.mmolib.comp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.mmogroup.mmolib.api.EntityHandler;

public class CitizensEntityHandler implements EntityHandler {

	public static final boolean sentinels = Bukkit.getPluginManager().getPlugin("Sentinel") != null;

	/*
	 * if NPC is null then the entity is not a citizen. otherwise, must check if
	 * the entity is a sentinel in which case the entity must be targetable
	 */
	@Override
	public boolean isInvulnerable(Entity entity) {
		NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
		return npc != null && (!sentinels || !npc.hasTrait(org.mcmonkey.sentinel.SentinelTrait.class));
	}
}
