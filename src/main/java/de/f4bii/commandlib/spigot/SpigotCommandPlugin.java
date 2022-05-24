package de.f4bii.commandlib.spigot;

import de.f4bii.commandlib.parser.BukkitDefaultParser;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotCommandPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        BukkitDefaultParser.registerAll();
    }
}
