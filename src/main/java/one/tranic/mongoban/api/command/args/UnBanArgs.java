package one.tranic.mongoban.api.command.args;

import dev.nipafx.args.Args;
import dev.nipafx.args.ArgsParseException;

import java.util.Optional;

public record UnBanArgs(Optional<String> target) {
    public static BanArgs parse(String[] args) throws ArgsParseException {
        return Args.parse(args, BanArgs.class);
    }
}
