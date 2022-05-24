package de.f4bii.commandlib.bungee;

import de.f4bii.commandlib.parser.BungeeDefaultParser;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeCommandPlugin extends Plugin {
    @Override
    public void onEnable() {
        BungeeDefaultParser.registerAll();
    }
}
