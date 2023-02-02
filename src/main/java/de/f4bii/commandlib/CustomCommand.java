package de.f4bii.commandlib;

import de.f4bii.commandlib.annotation.ExceptionHandler;
import de.f4bii.commandlib.annotation.NoArgs;
import de.f4bii.commandlib.annotation.SubCommand;
import de.f4bii.commandlib.exception.CommandNotFoundException;
import lombok.extern.slf4j.Slf4j;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class CustomCommand<S, E> {

    private final List<SubCommandObject<S>> commands = new ArrayList<>();
    private final HashMap<Class<? extends Throwable>, MethodHandle> exceptions = new HashMap<>();
    private SubCommandObject<S> noargs;
    private final ReflectionHelper reflectionHelper = new ReflectionHelper();

    public CustomCommand() {
        loadCommands();
    }

    private void loadCommands() {
        for (Method method : getClass().getMethods()) {
            initAnnotations(method);
        }
        commands.sort(SubCommandObject::compareTo);
    }

    /**
     * Initializes Annotations for the {@link NoArgs}, {@link SubCommand} and {@link ExceptionHandler}
     * classes.
     *
     * @param method to check annotations for
     */
    private void initAnnotations(Method method) {
        if (method.isAnnotationPresent(NoArgs.class)) {
            noargs = new SubCommandObject<>(this, method.getAnnotation(SubCommand.class), method);
        } else if (method.isAnnotationPresent(SubCommand.class)) {
            commands.add(new SubCommandObject<>(this, method.getAnnotation(SubCommand.class), method));
        } else if (method.isAnnotationPresent(ExceptionHandler.class)) {
            ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
            initExceptionHandlerAnnotation(method, annotation);
        }
    }

    /**
     * Initializes the {@link ExceptionHandler} Annotation.
     *
     * @param method to initialize the annotation for
     * @param annotation the actual annotation object
     */
    private void initExceptionHandlerAnnotation(Method method, ExceptionHandler annotation) {
        Arrays.stream(annotation.value()).forEach(getMethodHandleConsumer(method));
    }

    /**
     * Gets the MethodHandler from the ExceptionHandler annotated class as
     * Consumer
     *
     * @param method to get the method handler from
     * @return method handler consumer
     */
    private Consumer<Class<? extends Throwable>> getMethodHandleConsumer(Method method) {
        return exception -> exceptions.put(exception, reflectionHelper.getMethodHandle(method));
    }

    public final void execute(S sender, String[] args) throws Throwable {
        try {
            if (args.length == 0) {
                noargs.execute(sender, args);
                return;
            }
            String finArgs = String.join(" ", args);
            args = finArgs.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
            SubCommandObject<S> object = getCommandMatchingArguments(sender, args);
            object.execute(sender, args);
        } catch (Throwable ex) {
            if (!handleException(ex, sender, args)) throw ex;
        }
    }

    private SubCommandObject<S> getCommandMatchingArguments(S sender, String[] arg){
        Optional<SubCommandObject<S>> first = commands.stream().filter(sSubCommandObject -> sSubCommandObject.matches(arg))
                .filter(sSubCommandObject -> sSubCommandObject.canExecute(sender)).findFirst();
        return first.orElseThrow(CommandNotFoundException::new);
    }

    protected final boolean handleException(Throwable exception, S sender, String[] args) {
        MethodHandle method = exceptions.get(exception.getClass());
        if (method == null) return false;
        try {
            method.invoke(this, sender, exception);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public E getExecutor() {
        return null;
    }
}

