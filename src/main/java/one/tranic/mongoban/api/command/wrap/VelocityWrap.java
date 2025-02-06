package one.tranic.mongoban.api.command.wrap;

import com.velocitypowered.api.command.SimpleCommand;
import one.tranic.mongoban.api.command.source.VelocitySource;

import java.util.List;

/**
 * A wrapper class that adapts a {@link one.tranic.t.base.command.simple.SimpleCommand} to work within the Velocity platform
 * by implementing the {@link SimpleCommand} interface.
 * <p>
 * This class facilitates the execution, completion suggestions, and permission checking
 * for commands using a {@link VelocitySource} as the source context.
 * <p>
 * It leverages an instance of a {@link one.tranic.t.base.command.simple.SimpleCommand} with a {@link VelocitySource} type
 * to effectively handle command functionalities, including execution and suggestions,
 * while adhering to Velocity's command structure.
 */
public class VelocityWrap implements SimpleCommand {
    private final one.tranic.t.base.command.simple.SimpleCommand<VelocitySource> command;

    public VelocityWrap(one.tranic.t.base.command.simple.SimpleCommand<VelocitySource> command) {
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
