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
    private SubCommandObject<S> noArgs;
    private final ReflectionHelper reflectionHelper = new ReflectionHelper();

    public CustomCommand() {
        loadCommands();
    }

    /**
     * Initializes the annotations of the commands and sorts them.
     */
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
            noArgs = new SubCommandObject<>(this, method.getAnnotation(SubCommand.class), method);
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

    /**
     * Execution and exception handling of the command
     *
     * @param sender generic command sender
     * @param args arguments of the command
     * @throws Throwable thrown if exception couldn't be handled
     */
    public final void execute(S sender, String[] args) throws Throwable {
        try {
            processCommandExecution(sender, args);
        } catch (Throwable ex) {
            if (!handleException(ex, sender, args)) throw ex;
        }
    }

    /**
     * Processes and delegates the Command Execution to the {@link SubCommandObject}-objects
     *
     * @param sender of the command
     * @param args of the command
     * @throws Throwable if something went wrong while executing the command
     */
    private void processCommandExecution(S sender, String[] args) throws Throwable {
        if (args.length == 0) {
            noArgs.execute(sender, args);
            return;
        }
        String finArgs = String.join(" ", args);
        args = finArgs.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
        SubCommandObject<S> object = getCommandMatchingArguments(sender, args);
        object.execute(sender, args);
    }

    /**
     * Gets the matching arguments for the command
     *
     * @param sender of the command
     * @param arg of the command
     * @return {@link SubCommandObject} of the command
     */
    private SubCommandObject<S> getCommandMatchingArguments(S sender, String[] arg){
        Optional<SubCommandObject<S>> first = commands.stream().filter(sSubCommandObject -> sSubCommandObject.matches(arg))
                .filter(sSubCommandObject -> sSubCommandObject.canExecute(sender)).findFirst();
        return first.orElseThrow(CommandNotFoundException::new);
    }

    /**
     * Handles thrown Exceptions to a called command and returns callback if the exception could be handled.
     *
     * @param exception to handle
     * @param sender that executed the command
     * @param args of the command
     * @return boolean if exception could be handled
     */
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

