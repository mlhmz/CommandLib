package de.f4bii.commandlib.parser;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface CommandParser<O> {
    O parse(String in);

    default String getGenericType() {
        Type[] genericInterfaces = CommandParser.class.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                return genericTypes[0].getTypeName();
            }
        }
        return "";
    }
}
