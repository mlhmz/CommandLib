package de.f4bii.commandlib.spigot;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.f4bii.commandlib.CustomCommand;
import de.f4bii.commandlib.annotation.Command;
import lombok.extern.log4j.Log4j;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.lang.reflect.InvocationTargetException;

@Log4j
public class BukkitCommand extends CustomCommand<CommandSender, CommandExecutor> implements CommandExecutor {

    @Override
    public final CommandExecutor getExecutor() {
//        Bukkit.getPluginManager().
        return this;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        try {
            this.execute(commandSender, strings);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            log.error("We couldn't execute the Command with these arguments: "+strings);
        } catch (Exception exception) {
            handleException(exception, commandSender, strings);
        }
        return false;
    }
}
