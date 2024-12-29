package one.tranic.mongoban.common.commands;

import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.source.SourceImpl;

import java.util.List;

// Todo
public class MongoBanCommand<C extends SourceImpl<?, ?>> extends Command<C> {
    public MongoBanCommand() {
        this.setName("mban");
        this.setPermission("mongoban.command.mban");
    }

    @Override
    public void execute(C source) {

    }

    @Override
    public List<String> suggest(C source) {
        return List.of();
    }
}