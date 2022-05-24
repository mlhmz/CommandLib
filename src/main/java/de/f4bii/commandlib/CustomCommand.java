package de.f4bii.commandlib;

import de.f4bii.commandlib.annotation.*;
import de.f4bii.commandlib.exception.CommandNotFoundException;
import de.f4bii.commandlib.exception.NoPermissionException;
import de.f4bii.commandlib.parser.AdapterWrapper;
import de.f4bii.commandlib.parser.CommandParser;
import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.MessageFormat;
import java.util.*;

@Log4j
public class CustomCommand<S, E> {

    private final List<CommandObject> commands = new ArrayList<>();
    private final HashMap<Class<? extends Exception>, Method> exceptions = new HashMap<>();

    public CustomCommand() {
        for (Method method : getClass().getMethods()) {
            if (method.isAnnotationPresent(SubCommand.class)) {
                method.setAccessible(true);
                SubCommand annotation1 = method.getAnnotation(SubCommand.class);
                CommandObject object = new CommandObject(method, annotation1);
                commands.add(object);
            } else if (method.isAnnotationPresent(ExceptionHandler.class)) {
                method.setAccessible(true);
                ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
                for (Class<? extends Exception> aClass : annotation.value()) {
                    exceptions.put(aClass, method);
                }
            }
        }
        commands.sort((o1, o2) -> o1.compare(o1, o2));
    }

    private static int getParamPos(Method method, String arg) {
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            if (parameter.isAnnotationPresent(Variable.class)) {
                if (parameter.getAnnotation(Variable.class).value().equalsIgnoreCase(arg)) {
                    return i;
                }
            } else {
                if (parameter.isNamePresent() && parameter.getName().equalsIgnoreCase(arg)) {
                    return i;
                }
            }
        }
        throw new IllegalArgumentException("'" + arg + "' parameter not found not found");
    }

    public final void execute(S sender, String[] args) throws InvocationTargetException, IllegalAccessException {
        long start = System.nanoTime();
        try {
            String finArgs = String.join(" ", args);
            args = finArgs.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
            CommandObject object = match(sender, args);
            if (object == null) {
                throw new CommandNotFoundException();
            }
            if (!canExecute(sender, object)) {
                throw new NoPermissionException();
            }
            object.method.invoke(this, sortObjects(sender, args, object));
        } catch (Exception ex) {
            if (!handleException(ex, sender, args)) throw ex;
        }
        log.debug("The Command execution took: " + (System.nanoTime() - start) + "ns");
    }

    private Class<?> getParamClass(CommandArgument commandArgument) {

        return null;
    }

    private Object[] sortObjects(S sender, String[] args, CommandObject object) {
        Object[] in = new Object[object.method.getParameterCount()];
        for (CommandArgument param : object.params) {
            if (param.var) {
                in[param.mappedin] = param.parse(args[param.pos]);
            }
        }
        int senderPos = getSenderPos(object.method);
        if (senderPos < in.length && senderPos > -1) in[senderPos] = sender;
        return in;
    }

    private CommandObject match(S sender, String[] args) {
        List<CommandObject> out = new ArrayList<>(commands);
        out.removeIf(object -> object.params.size() != args.length);
        out.removeIf(object -> !canExecute(sender, object));
        out.removeIf(object -> !hasPermission(sender, object));
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            int finalI = i;
            out.removeIf(object -> {
                CommandArgument commandArgument = object.params.get(finalI);
                return !commandArgument.param.equals(arg) && !commandArgument.var;
            });
        }
        if (out.isEmpty()) {
            return null;
        }
        if (out.size() > 1) {
            log.warn("The Command Execution with " + args + " has more then 1 match!");
            out.forEach(commandObject -> log.warn(commandObject.command));
            log.warn("If that's a bug and shouldn't happen, please report it to the Developer");
            log.warn("Executing now: " + out.get(0).method.getName() + " | " + out.get(0).command);
        }
        return out.get(0);
    }

    private boolean canExecute(S sender, CommandObject object) {
        Class<?> executorClass = getExecutorClass(object);
        if (executorClass == null) {
            return hasPermission(sender, object);
        }
        return executorClass.isAssignableFrom(sender.getClass());
//        return true;
    }

    private Class<?> getExecutorClass(CommandObject object) {
        for (Parameter parameter : object.method.getParameters()) {
            if (parameter.isAnnotationPresent(Executor.class)) {
                return parameter.getType();
            }
        }
        return null;
    }

    private int getSenderPos(Method method) {
        for (int i = 0; i < method.getParameters().length; i++) {
            Parameter parameter = method.getParameters()[i];
            if (parameter.isAnnotationPresent(Executor.class)) {
                return i;
            }
        }
        return -1;
    }

    protected final boolean handleException(Exception exception, S sender, String[] args) {
        Method method = exceptions.get(exception.getClass());
        if (method == null) return false;
        try {
            method.invoke(this, sender, exception);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public E getExecutor() {
        return null;
    }

    public boolean hasPermission(S sender, CommandObject commandObject) {
        return true;
    }

    @Data
    public static class CommandObject implements Comparator<CommandObject> {
        private String command;
        private Method method;
        private List<CommandArgument> params = new ArrayList<>();
        private String permission;

        public CommandObject(Method method, SubCommand subCommand) {
            this.method = method;
            this.command = subCommand.value();
            this.permission = subCommand.permission();
            if (method.isAnnotationPresent(NoArgs.class)) {
                params.add(new CommandArgument("", false, -1, -1, void.class));
                return;
            }
            String[] s = subCommand.value().split(" ");
            for (int i = 0; i < s.length; i++) {
                String arg = s[i];
                if (arg.matches("\\{(.+)}")) {
                    arg = arg.replaceAll("\\{(.+)}", "$1");
                    int paramPos = getParamPos(method, arg);
                    params.add(new CommandArgument(arg, true, i, paramPos, method.getParameterTypes()[paramPos]));
                } else {
                    params.add(new CommandArgument(arg, false, -1, -1, void.class));
                }
            }
        }

        @Override
        public String toString() {
            return MessageFormat.format("CommandObject'{'command=''{0}'', method={1}, params={2}'}'", command, method, params);
        }

        @Override
        public int compare(CommandObject o1, CommandObject o2) {
            int a = 0, b = 0;
            for (CommandArgument param : o1.params) {
                a += param.getWeight();
            }
            for (CommandArgument param : o2.params) {
                b += param.getWeight();
            }
            return Integer.compare(b, a);
        }
    }

    @Data
    public static class CommandArgument<T> {
        private String param;
        private boolean var;
        private int pos, mappedin;
        private Class<T> type;

        public CommandArgument(String param, boolean var, int pos, int mappedin, Class<T> type) {
            this.param = param;
            this.var = var;
            this.pos = pos;
            this.mappedin = mappedin;
            this.type = type;
        }

        public <T> T parse(String in) {
            T out = null;
            if (var) {
                CommandParser<?> parser = AdapterWrapper.getParser(type);
                try {
                    out = (T) parser.parse(in);
                } catch (Exception ex) {
                    return null;
                }
            }
            return out;
        }

        private int getWeight() {
            int out = param.length();
            if (!var) out += 100;
            else out -= 100;

            return out;
        }
    }
}

