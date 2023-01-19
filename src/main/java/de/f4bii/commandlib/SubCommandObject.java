package de.f4bii.commandlib;

import de.f4bii.commandlib.annotation.SubCommand;
import de.f4bii.commandlib.annotation.Variable;
import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class SubCommandObject {

    private final Method method;
    private final MethodHandle methodHandle;
    private final CustomCommand<?, ?> customCommand;
    private final SubCommand subCommand;
    private final List<SubCommandArgument<?>> arguments = new ArrayList<>();

    public SubCommandObject(CustomCommand<?, ?> customCommand, SubCommand subCommand, Method method) {
        this.method = method;
        this.methodHandle = new ReflectionHelper().getMethodHandle(method);
        this.customCommand = customCommand;
        this.subCommand = subCommand;
    }

    private void loadParameters() {
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

    @SneakyThrows
    public void execute(String[] in) {
        Object[] out = new Object[arguments.size()];
        for (SubCommandArgument<?> argument : arguments) {
            argument.injectValue(in, out);
        }
        Object[] execute = new Object[arguments.size() + 1];
        execute[0] = customCommand;
        System.arraycopy(out, 0, execute, 1, out.length);
        methodHandle.invokeWithArguments(execute);
    }

    private int getParamPos(Method method, String arg) {
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

}
