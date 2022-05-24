package de.f4bii.commandlib.parser;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class BukkitDefaultParser {

    public static void registerAll() {
        DefaultParser.registerAll();
        AdapterWrapper.register(Player.class, new PlayerParser());
        AdapterWrapper.register(OfflinePlayer.class, new OfflinePlayerParser());
        AdapterWrapper.register(World.class, new WorldParser());
    }

    private static final class PlayerParser implements CommandParser<Player> {
        @Override
        public Player parse(String in) {
            return Bukkit.getPlayer(in);
        }
    }

    private static final class OfflinePlayerParser implements CommandParser<OfflinePlayer> {
        @Override
        public OfflinePlayer parse(String in) {
            return Bukkit.getOfflinePlayer(in);
        }
    }

    private static final class WorldParser implements CommandParser<World> {

        @Override
        public World parse(String in) {
            return Bukkit.getWorld(in);
        }
    }
}
