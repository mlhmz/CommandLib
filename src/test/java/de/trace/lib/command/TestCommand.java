package de.trace.lib.command;

import de.f4bii.commandlib.CustomCommand;
import de.f4bii.commandlib.exception.NoPermissionException;
import de.f4bii.commandlib.annotation.*;

@Command("help")
public class TestCommand extends CustomCommand<TestPlayer, String> {

    @SubCommand("{home}")
    public void onHome(
            @Variable("home") String home,
            @Sender TestPlayer executor
    ) {
        throw new SuccessfulCommand("onHome");
    }

    @SubCommand("sethome {home}")
    public void onSetHome(
            @Variable("home") String home,
            @Sender TestPlayer executor
    ) {
        throw new SuccessfulCommand("onSetHome");
    }

    @SubCommand("delhome {home}")
    public void onDelHome(
            @Variable("home") String home,
            @Sender TestPlayer executor
    ) {
        throw new SuccessfulCommand("onDelHome");
    }

    @SubCommand("dgethome {home}")
    public void onGetHome(
            @Variable("home") String home,
            @Sender TestPlayer executor
    ) {
        throw new SuccessfulCommand("onGetHome");
    }

    @SubCommand("{player} {home}")
    public void onPlayerHome(
            @Variable("player") String player,
            @Variable("home") String home,
            @Sender TestPlayer executor
    ) {
        throw new SuccessfulCommand("onPlayerHome");
    }

    @SubCommand("list")
    public void onList(
            @Sender TestPlayer executor
    ) {
        throw new SuccessfulCommand("onList");
    }

    @SubCommand("int {id}")
    public void onList(
            @Variable("id") Integer id,
            @Sender TestPlayer executor
    ) {
        throw new SuccessfulCommand("onInt");
    }

    @NoArgs
    public void onNoArgs(
            @Sender TestPlayer executor
    ) {
        throw new SuccessfulCommand("onNoArgs");
    }

    @SubCommand("help")
    public void onHelp(@Sender TestPlayer testPlayer) {
        throw new SuccessfulCommand("onHelp");
    }

    @ExceptionHandler(NoPermissionException.class)
    public void exception(@Sender TestPlayer executor, NoPermissionException ex) {
        throw ex;
    }
}
