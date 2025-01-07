package one.tranic.mongoban.common.commands;

import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.message.MessageKey;
import one.tranic.mongoban.api.player.MongoPlayer;

import java.util.List;

// Todo
public class UnBanCommand<C extends SourceImpl<?, ?>> extends Command<C> {
    public UnBanCommand() {
        setName("unban");
        setPermission("mongoban.command.unban");
    }

    @Override
    public void execute(C source) {
        MongoPlayer<?> player = source.asPlayer();

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
