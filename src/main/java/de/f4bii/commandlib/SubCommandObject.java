package de.f4bii.commandlib;

import de.f4bii.commandlib.annotation.Sender;
import de.f4bii.commandlib.annotation.SubCommand;
import de.f4bii.commandlib.annotation.Variable;
import org.bukkit.Bukkit;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SubCommandObject<S> implements Comparable<SubCommandObject<S>> {

    private final Method method;
    private final MethodHandle methodHandle;
    private final CustomCommand<S, ?> customCommand;
    private final SubCommand subCommand;
    private final List<SubCommandArgument<?>> arguments = new ArrayList<>();

    public SubCommandObject(CustomCommand<S, ?> customCommand, SubCommand subCommand, Method method) {
        this.method = method;
        this.methodHandle = new ReflectionHelper().getMethodHandle(method);
        this.customCommand = customCommand;
        this.subCommand = subCommand;
        loadParameters();
    }

    private void loadParameters() {
        if (subCommand == null)
            return;
        String[] s = subCommand.value().split(" ");
        for (int i = 0; i < s.length; i++) {
            String arg = s[i];
            if (arg.matches("\\{(.+)}")) {
                arg = arg.replaceAll("\\{(.+)}", "$1");
                int paramPos = getParamPos(method, arg);
                arguments.add(new SubCommandArgument<>(method.getParameterTypes()[paramPos], i, paramPos));
            }
        }
    }

    public void execute(S sender, String[] in) throws Throwable {
        Object[] out = new Object[arguments.size()+1];
        for (SubCommandArgument<?> argument : arguments) {
            argument.injectValue(in, out);
        }
        int senderPosition = getSenderPosition();
        if (senderPosition != -1)
            out[senderPosition] = sender;
        Object[] execute = new Object[out.length + 1];
        execute[0] = customCommand;
        System.arraycopy(out, 0, execute, 1, out.length);
        methodHandle.invokeWithArguments(execute);
    }

    public int getSenderPosition(){
        for (int i = 0; i < method.getParameters().length; i++) {
            if (method.getParameters()[i].isAnnotationPresent(Sender.class)) {
                return i;
            }
        }
        return -1;
    }

    public Class<?> getSenderType(){
        for (Parameter parameter : method.getParameters()) {
            if (parameter.isAnnotationPresent(Sender.class))
                return parameter.getType();
        }
        return null;
    }

    public boolean matches(String[] args){
        String[] cmdArgs = subCommand.value().split(" ");
        if (args.length != cmdArgs.length)
            return false;
        for (int i = 0; i < args.length; i++) {
            if (cmdArgs[i].matches("\\{(.+)}"))
                continue;
            if (!args[i].equalsIgnoreCase(cmdArgs[i]))
                return false;
        }
        return true;
    }

    private int getParamPos(@NotNull Method method, String arg) {
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            if (parameter.isAnnotationPresent(Variable.class)) {
                if (parameter.getAnnotation(Variable.class).value().equalsIgnoreCase(arg)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public boolean canExecute(S sender) {
        Class<?> senderType = getSenderType();
        if (senderType == null) {
            return false;
        }
        return senderType.isAssignableFrom(sender.getClass());
    }

    @Override
    public int compareTo(@NotNull SubCommandObject<S> o) {
        int out = 0;
        String[] s = subCommand.value().split(" ");
        String[] s1 = o.subCommand.value().split(" ");
        for (String s2 : s) {
            if (s2.matches("\\{(.+)}")) {
                out+=100;
            } else
                out++;
        }
        for (String s2 : s1) {
            if (s2.matches("\\{(.+)}")) {
                out-=100;
            } else
                out--;
        }
        return out;
    }
}
