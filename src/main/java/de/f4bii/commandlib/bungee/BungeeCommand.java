package de.f4bii.commandlib.bungee;

import de.f4bii.commandlib.CustomCommand;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.lang.reflect.InvocationTargetException;

public class BungeeCommand extends CustomCommand<CommandSender, Command> {
    private final Command executor;

    public BungeeCommand() {
        de.f4bii.commandlib.annotation.Command cmd = getClass().getAnnotation(de.f4bii.commandlib.annotation.Command.class);
        this.executor = new Command(cmd.value()) {
            @Override
            public void execute(CommandSender commandSender, String[] strings) {
                try {
                    BungeeCommand.this.execute(commandSender, strings);
                } catch (InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                } catch (Exception exception) {
                    handleException(exception, commandSender, strings);
                }
            }
        };
    }

    @Override
    public final Command getExecutor() {
        return executor;
    }
}
