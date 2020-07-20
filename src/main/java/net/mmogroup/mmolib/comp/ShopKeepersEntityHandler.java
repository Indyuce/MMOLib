package net.mmogroup.mmolib.comp;

import org.bukkit.entity.Entity;

import com.nisovin.shopkeepers.api.ShopkeepersPlugin;

import net.mmogroup.mmolib.api.EntityHandler;

public class ShopKeepersEntityHandler implements EntityHandler {

	@Override
	public boolean isInvulnerable(Entity entity) {
		return ShopkeepersPlugin.getInstance().getShopkeeperRegistry().isShopkeeper(entity);
	}
}
