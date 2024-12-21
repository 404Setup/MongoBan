package one.tranic.mongoban.common.command.wrap;

import com.velocitypowered.api.command.SimpleCommand;
import one.tranic.mongoban.common.command.s.Command;
import one.tranic.mongoban.common.source.s.VelocitySource;

import java.util.List;

public class VelocityWrap implements SimpleCommand {
    private final Command<VelocitySource> command;

    public VelocityWrap(Command<VelocitySource> command) {
        this.command = command;
    }

    @Override
    public void execute(Invocation invocation) {
        this.command.execute(new VelocitySource(invocation));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        return this.command.suggest(new VelocitySource(invocation));
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return this.command.hasPermission(new VelocitySource(invocation));
    }
}
