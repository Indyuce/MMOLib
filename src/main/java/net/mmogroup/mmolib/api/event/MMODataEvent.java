package net.mmogroup.mmolib.api.event;

import org.bukkit.event.player.PlayerEvent;

import net.mmogroup.mmolib.api.player.MMOPlayerData;

public abstract class MMODataEvent extends PlayerEvent {
	private final MMOPlayerData playerData;

	public MMODataEvent(MMOPlayerData playerData) {
		super(playerData.getPlayer());

		this.playerData = playerData;
	}

	public MMOPlayerData getData() {
		return playerData;
	}
}
