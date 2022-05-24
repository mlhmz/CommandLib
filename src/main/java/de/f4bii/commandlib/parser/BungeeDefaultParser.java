package de.f4bii.commandlib.parser;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeDefaultParser {
    private static volatile boolean registered = false;

    public static void registerAll() {
        registered = true;
        DefaultParser.registerAll();
        AdapterWrapper.register(ProxiedPlayer.class, new ProxiedPlayerParser());
    }

    private static final class ProxiedPlayerParser implements CommandParser<ProxiedPlayer> {
        @Override
        public ProxiedPlayer parse(String in) {
            return ProxyServer.getInstance().getPlayer(in);
        }
    }
}
