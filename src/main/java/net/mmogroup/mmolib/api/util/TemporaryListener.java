package net.mmogroup.mmolib.api.util;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.Indyuce.mmoitems.MMOItems;
import net.mmogroup.mmolib.MMOLib;

public abstract class TemporaryListener implements Listener {

	/*
	 * handler lists which must be called when the temporary listener is closed so
	 * that the listener is entirely unregistered.
	 */
	private final HandlerList[] lists;

	/*
	 * sometimes the close method is called twice because of a safe delayed task not
	 * being cancelled when the listener is closed.
	 */
	private boolean closed;

	public TemporaryListener(HandlerList... events) {
		this(MMOLib.plugin, events);
	}

	public TemporaryListener(JavaPlugin plugin, HandlerList... events) {
		lists = events;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	/*
	 * used to close the temporary listener after some delay
	 */
	public void close(long duration) {
		Bukkit.getScheduler().runTaskLater(MMOItems.plugin, () -> close(), duration);
	}

	/*
	 * returns 'true' if the listener was closed for the first time
	 */
	public boolean close() {
		if (closed)
			return false;

		closed = true;
		for (HandlerList list : lists)
			list.unregister(this);
		return true;
	}
}
