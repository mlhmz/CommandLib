package de.trace.lib.command;

import de.f4bii.commandlib.parser.DefaultParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

class ServerTest {

    public static final TestPlayer SENDER = new TestPlayer();

    static {
        DefaultParser.registerAll();
    }

    private final TestCommand command = new TestCommand();

    @Test
    void testHelpCommand() {
        InvocationTargetException thrown = Assertions.assertThrows(InvocationTargetException.class, () -> {
            command.execute(SENDER, new String[]{"help"});
        });
        Assertions.assertEquals("onHelp", thrown.getCause().getMessage());
    }

    @Test
    void testHomeCommands() {
        InvocationTargetException thrown = Assertions.assertThrows(InvocationTargetException.class, () -> {
            command.execute(SENDER, new String[]{"home3"});
        });
        Assertions.assertEquals("onHome", thrown.getCause().getMessage());
    }

    @Test
    void testSetCommands() {
        InvocationTargetException thrown = Assertions.assertThrows(InvocationTargetException.class, () -> {
            command.execute(SENDER, new String[]{"sethome", "asd"});
        });
        Assertions.assertEquals("onSetHome", thrown.getCause().getMessage());
    }

    @Test
    void testDelCommands() {
        InvocationTargetException thrown = Assertions.assertThrows(InvocationTargetException.class, () -> {
            command.execute(SENDER, new String[]{"delhome", "asd"});
        });
        Assertions.assertEquals("onDelHome", thrown.getCause().getMessage());
    }

    @Test
    void testPHomeCommands() {
        InvocationTargetException thrown = Assertions.assertThrows(InvocationTargetException.class, () -> {
            command.execute(SENDER, new String[]{"zenzon", "mainbase"});
        });
        Assertions.assertEquals("onPlayerHome", thrown.getCause().getMessage());
    }

    @Test
    void testListCommands() {
        InvocationTargetException thrown = Assertions.assertThrows(InvocationTargetException.class, () -> {
            command.execute(SENDER, new String[]{"list"});
        });
        Assertions.assertEquals("onList", thrown.getCause().getMessage());
    }

    @Test
    void testNoArgsCommands() {
        InvocationTargetException thrown = Assertions.assertThrows(InvocationTargetException.class, () -> {
            command.execute(SENDER, new String[]{});
        });
        Assertions.assertEquals("onNoArgs", thrown.getCause().getMessage());
    }

    @Test
    void testIntegerCommands() {
        InvocationTargetException thrown = Assertions.assertThrows(InvocationTargetException.class, () -> {
            command.execute(SENDER, new String[]{"int", "100"});
        });
        Assertions.assertEquals("onInt", thrown.getCause().getMessage());
    }

    @Test
    void testNoFoundCommands() {
        SuccessfulCommand thrown3 = Assertions.assertThrows(SuccessfulCommand.class, () -> {
            command.execute(SENDER, new String[]{"housdf", "adsf", "dgf"});
        });
        Assertions.assertEquals("onHelp", thrown3.getMessage());
    }

}
