package one.tranic.mongoban.common.commands;

import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.source.SourceImpl;

import java.util.List;

// Todo
public class WarnCommand<C extends SourceImpl<?, ?>> extends Command<C> {
    @Override
    public void execute(C source) {

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
