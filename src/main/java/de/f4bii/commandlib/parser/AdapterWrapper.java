package de.f4bii.commandlib.parser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Date: 02.08.2021
 * <br/>@author FabiHBBBT
 * <br/>
 **/
public class AdapterWrapper {

    private static final HashMap<Type, CommandParser<?>> adapters = new HashMap<>();

    public static CommandParser<?> getParser(Class<?> clazz) {
        CommandParser<?> adapter = adapters.get(clazz);
        if (adapter == null) {
            System.out.println("§cWARNING: " + clazz.getName() + " has no Adapter registered! Default Adapter will be used! (" + String.class.getName() + ")");
            System.out.println("§cThis could result in an ClassCastException. Please create a new Adapter for " + clazz.getName());
            adapter = adapters.get(String.class);
        }
        return adapter;
    }


    public static void register(Class<?> type, CommandParser<?> adapter) {
        if (adapters.containsKey(type))
            return;
        adapters.put(type, adapter);
    }

    // TODO möglich machen, dass man direkt mit lambda einen CommandParser registrieren kann.
    public static void register(CommandParser<?> adapter) {
        Type[] genericInterfaces = adapter.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                for (Type genericType : genericTypes) {
                    if (((ParameterizedType) genericInterface).getRawType().equals(CommandParser.class)) {
                        System.out.println("[CommandAPI] Registering: '" + genericType.getTypeName() + "' as an CommandParser");
                        adapters.put(genericType, adapter);
                    }
                }
            }
        }
    }

}
