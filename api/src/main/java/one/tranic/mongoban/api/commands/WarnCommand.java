package one.tranic.mongoban.api.commands;

import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.message.MessageKey;
import one.tranic.t.base.command.source.CommandSource;

import java.util.List;

// Todo
public class WarnCommand<C extends CommandSource<?, ?>> extends Command<C> {

    public WarnCommand() {
        setName("warn");
        setPermission("mongoban.command.warn");
    }

    @Override
    public void execute(C source) {
        var player = source.asPlayer();

        if (player != null) {
            if (!hasPermission(source)) {
                source.sendMessage(MessageKey.PERMISSION_DENIED.format());
                return;
            }
        }
    }

    @Override
    public List<String> suggest(C source) {
        return List.of();
    }

    @Override
    public boolean hasPermission(C source) {
        return false;
    }
}
