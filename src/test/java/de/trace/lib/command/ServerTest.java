package de.trace.lib.command;

import de.f4bii.commandlib.exception.CommandNotFoundException;
import de.f4bii.commandlib.parser.DefaultParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ServerTest {

    public static final TestPlayer SENDER = new TestPlayer();

    static {
        DefaultParser.registerAll();
    }

    private final TestCommand command = new TestCommand();

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "home3;onHome",
            "sethome,asd;onSetHome",
            "delhome,asd;onDelHome",
            "fabihbbbt,mainbase;onPlayerHome",
            "list;onList",
            " ,;onNoArgs",
            "int,100;onInt"
    })
    void testRightExecutedCommands(String cmd, String expected) {
        SuccessfulCommand thrown = Assertions.assertThrows(SuccessfulCommand.class, () -> {
            command.execute(SENDER, cmd.split(","));
        });
        Assertions.assertEquals(expected, thrown.getMessage());
    }

    @Test
    void testNotFoundCommand() {
        Assertions.assertThrows(CommandNotFoundException.class, () -> {
            command.execute(SENDER, new String[]{"housdf", "adsf", "dgf"});
        });
//        Assertions.assertEquals("onHelp", thrown3.getMessage());
    }

//    @Test
//    void testHelpCommand() {
//        SuccessfulCommand thrown = Assertions.assertThrows(SuccessfulCommand.class, () -> {
//            command.execute(SENDER, new String[]{"help"});
//        });
//        Assertions.assertEquals("onHelp", thrown.getMessage());
//    }

}
