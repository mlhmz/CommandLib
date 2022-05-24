package de.trace.lib.command;

import de.f4bii.commandlib.CustomCommand;
import de.f4bii.commandlib.exception.NoPermissionException;
import de.f4bii.commandlib.annotation.*;

@Command("help")
public class TestCommand extends CustomCommand<TestPlayer, String> {

    @SubCommand("{home}")
    public void onHome(
            @Variable("home") String home,
            @Executor TestPlayer executor
    ) {
        throw new SuccessfulCommand("onHome");
    }

    @SubCommand("sethome {home}")
    public void onSetHome(
            @Variable("home") String home,
            @Executor TestPlayer executor
    ) {
        throw new SuccessfulCommand("onSetHome");
    }

    @SubCommand("delhome {home}")
    public void onDelHome(
            @Variable("home") String home,
            @Executor TestPlayer executor
    ) {
        throw new SuccessfulCommand("onDelHome");
    }

    @SubCommand("{player} {home}")
    public void onPlayerHome(
            @Variable("player") String player,
            @Variable("home") String home,
            @Executor TestPlayer executor
    ) {
        throw new SuccessfulCommand("onPlayerHome");
    }

    @SubCommand("list")
    public void onList(
            @Executor TestPlayer executor
    ) {
        throw new SuccessfulCommand("onList");
    }

    @SubCommand("int {id}")
    public void onList(
            @Variable("id") Integer id,
            @Executor TestPlayer executor
    ) {
        throw new SuccessfulCommand("onInt");
    }

    @NoArgs
    @SubCommand
    public void onNoArgs(
            @Executor TestPlayer executor
    ) {
        throw new SuccessfulCommand("onNoArgs");
    }

    @SubCommand("help")
    public void onHelp(@Executor TestPlayer testPlayer) {
        throw new SuccessfulCommand("onHelp");
    }

    @ExceptionHandler(NoPermissionException.class)
    public void exception(@Executor TestPlayer executor, NoPermissionException ex) {
        throw ex;
    }
}
