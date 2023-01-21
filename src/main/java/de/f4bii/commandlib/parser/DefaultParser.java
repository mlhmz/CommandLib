package de.f4bii.commandlib.parser;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultParser {

    public static void registerAll() {
        AdapterWrapper.register(String.class, in -> in);
        AdapterWrapper.register(int.class, Integer::parseInt);
        AdapterWrapper.register(Integer.class, Integer::parseInt);
        AdapterWrapper.register(double.class, Double::parseDouble);
        AdapterWrapper.register(Double.class, Double::parseDouble);
        AdapterWrapper.register(float.class, Float::parseFloat);
        AdapterWrapper.register(Float.class, Float::parseFloat);
        AdapterWrapper.register(short.class, Short::parseShort);
        AdapterWrapper.register(Short.class, Short::parseShort);
        AdapterWrapper.register(long.class, Long::parseLong);
        AdapterWrapper.register(Long.class, Long::parseLong);
        AdapterWrapper.register(byte.class, Byte::parseByte);
        AdapterWrapper.register(Byte.class, Byte::parseByte);
        AdapterWrapper.register(char.class, in -> in.charAt(0));
        AdapterWrapper.register(Character.class, in -> in.charAt(0));
        AdapterWrapper.register(UUID.class, UUID::fromString);
    }

}
