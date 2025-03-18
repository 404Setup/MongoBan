package one.tranic.mongoban.api.commands;

import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.message.MessageKey;
import one.tranic.t.base.command.source.CommandSource;

import java.util.List;

// Todo
public class MongoBanCommand<C extends CommandSource<?, ?>> extends Command<C> {
    public MongoBanCommand() {
        this.setName("mbcmd");
        this.setPermission("mongoban.command.mbcmd");
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
}
