package net.mmogroup.mmolib.api.stat.modifier;

import org.bukkit.scheduler.BukkitRunnable;

import net.mmogroup.mmolib.MMOLib;
import net.mmogroup.mmolib.api.stat.StatInstance;

public class TemporaryStatModifier extends StatModifier implements Closable {
	private final BukkitRunnable runnable;

	public TemporaryStatModifier(double d, long duration, ModifierType type, String key, StatInstance ins) {
		super(d, type);

		(runnable = new BukkitRunnable() {
			public void run() {
				ins.remove(key);
			}
		}).runTaskLater(MMOLib.plugin, duration);
	}

	@Override
	public void close() {
		if (!runnable.isCancelled())
			runnable.cancel();
	}
}
