package net.mmogroup.mmolib.comp;

import org.bukkit.entity.Entity;

import de.Keyle.MyPet.api.entity.MyPetBukkitEntity;
import net.mmogroup.mmolib.api.EntityHandler;

public class MyPetEntityHandler implements EntityHandler {

	@Override
	public boolean isInvulnerable(Entity entity) {
		return entity instanceof MyPetBukkitEntity;
	}
}
