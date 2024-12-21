package one.tranic.mongoban.common.command.s;

import one.tranic.mongoban.api.command.s.Command;
import one.tranic.mongoban.api.command.source.SourceImpl;

import java.util.List;

public class BanCommand<C extends SourceImpl<?>> extends Command<C> {
    public BanCommand() {
        this.setName("ban");
        this.setPermission("mongoban.ban");
    }

    @Override
    public void execute(C source) {
        source.sendMessage("Ban command not implemented yet.");
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
