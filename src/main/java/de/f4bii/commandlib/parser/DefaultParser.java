package de.f4bii.commandlib.parser;

import java.util.UUID;

public class DefaultParser {
    private static volatile boolean registered = false;

    private DefaultParser() {
    }

    public static void registerAll() {
        registered = true;
        AdapterWrapper.register(String.class, new StringParser());
        AdapterWrapper.register(int.class, new IntegerParser());
        AdapterWrapper.register(Integer.class, new IntegerParser());
        AdapterWrapper.register(double.class, new DoubleParser());
        AdapterWrapper.register(Double.class, new DoubleParser());
        AdapterWrapper.register(float.class, new FloatParser());
        AdapterWrapper.register(Float.class, new FloatParser());
        AdapterWrapper.register(short.class, new ShortParser());
        AdapterWrapper.register(Short.class, new ShortParser());
        AdapterWrapper.register(long.class, new LongParser());
        AdapterWrapper.register(Long.class, new LongParser());
        AdapterWrapper.register(byte.class, new ByteParser());
        AdapterWrapper.register(Byte.class, new ByteParser());
        AdapterWrapper.register(char.class, new CharParser());
        AdapterWrapper.register(Character.class, new CharParser());
        AdapterWrapper.register(UUID.class, new UUIDParser());
    }

    private static final class IntegerParser implements CommandParser<Integer> {
        @Override
        public Integer parse(String in) {
            return Integer.parseInt(in);
        }
    }

    private static final class DoubleParser implements CommandParser<Double> {
        @Override
        public Double parse(String in) {
            return Double.parseDouble(in);
        }
    }

    private static final class FloatParser implements CommandParser<Float> {
        @Override
        public Float parse(String in) {
            return Float.parseFloat(in);
        }
    }

    private static final class LongParser implements CommandParser<Long> {
        @Override
        public Long parse(String in) {
            return Long.parseLong(in);
        }
    }

    private static final class ShortParser implements CommandParser<Short> {
        @Override
        public Short parse(String in) {
            return Short.parseShort(in);
        }
    }

    private static final class ByteParser implements CommandParser<Byte> {
        @Override
        public Byte parse(String in) {
            return Byte.parseByte(in);
        }
    }

    private static final class StringParser implements CommandParser<String> {
        @Override
        public String parse(String in) {
            return in;
        }
    }

    private static final class CharParser implements CommandParser<Character> {
        @Override
        public Character parse(String in) {
            return in.charAt(0);
        }
    }

    private static final class UUIDParser implements CommandParser<UUID> {
        @Override
        public UUID parse(String in) {
            return UUID.fromString(in);
        }
    }
}
