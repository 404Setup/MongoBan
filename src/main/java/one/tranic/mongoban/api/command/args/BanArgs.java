package one.tranic.mongoban.api.command.args;

import dev.nipafx.args.Args;
import dev.nipafx.args.ArgsParseException;

import java.util.Optional;

public record BanArgs(Optional<String> target, Optional<String> reason, Optional<String> duration, Optional<Boolean> strict) {
    public static BanArgs parse(String[] args) throws ArgsParseException {
        return Args.parse(args, BanArgs.class);
    }
}
