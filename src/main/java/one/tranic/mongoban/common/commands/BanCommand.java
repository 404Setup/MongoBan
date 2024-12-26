package one.tranic.mongoban.common.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.CommandFlag;
import one.tranic.mongoban.api.command.CommandParse;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.exception.ParseException;
import one.tranic.mongoban.api.player.MongoPlayer;
import one.tranic.mongoban.api.player.Player;
import one.tranic.mongoban.common.Parse;
import one.tranic.mongoban.common.form.GeyserForm;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;

// Todo - Unfinished
public class BanCommand<C extends SourceImpl<?, ?>> extends Command<C> {
    public BanCommand() {
        this.setName("ban");
        this.setPermission("mongoban.command.ban");
    }

    @Override
    public void execute(C source) {
        MongoPlayer<?> player = source.asPlayer();

        if (player != null) {
            if (!hasPermission(source)) {
                source.sendMessage(Component.text("Permission denied!", NamedTextColor.RED));
                return;
            }

            // TODO： Bedrock command executors will pass a form instead of a command
            if (player.isBedrockPlayer()) {
                player.sendFormAsync(GeyserForm.getDoForm());
                return;
            }
        }

        String[] args = source.getArgs();

        if (args.length < 1) {
            source.sendMessage(Component.text(
                    "Invalid usage! Use: /" + getName() + " --name <playerName> --time [time] --reason [reason]", NamedTextColor.RED));
            return;
        }

        // Test: Do not block the command execution thread
        MongoBanAPI.runAsync(() -> {
            CommandParse parse = new CommandParse();
            parse.parse(args);

            @Nullable String target = parse.getFlagValue(CommandFlag.TARGET);
            if (target == null) {
                source.sendMessage(Component.text("Target flag is missing! Use --target <playerName>|<ip> to specify the target.", NamedTextColor.RED));
                return;
            }

            @Nullable String timeArg = parse.getFlagValue(CommandFlag.TIME);
            @Nullable String reason = parse.getFlagValue(CommandFlag.REASON);
            @Nullable String strict = parse.getFlagValue(CommandFlag.STRICT);

            boolean isIP = false;
            try {
                InetAddress inip = InetAddress.getByName(target);
                IPBanInfo result = MongoDataAPI.getDatabase().ban().ip().find(inip.getHostAddress()).sync();
                if (result != null) {
                    if (result.expired()) MongoDataAPI.getDatabase().ban().ip().remove(inip.getHostAddress()).async();
                    else {
                        source.sendMessage(Component.text("The specified IP address is already banned:", NamedTextColor.YELLOW));
                        source.sendMessage(Component.text("IP: " + result.ip(), NamedTextColor.GOLD));
                        source.sendMessage(Component.text("Operator: " + result.operator().name(), NamedTextColor.GREEN));
                        source.sendMessage(Component.text("Duration: " + (result.duration() != null ? result.duration() : "Permanent"), NamedTextColor.BLUE));
                        source.sendMessage(Component.text("Reason: " + result.reason(), NamedTextColor.RED));
                        return;
                    }
                }
                isIP = true;
            } catch (Exception ignored) {
            }
            Player targetPlayer = one.tranic.mongoban.api.player.Player.getPlayer(target);
            if (targetPlayer != null) {
                String parsedTime = null;
                try {
                    parsedTime = Parse.timeArg(timeArg);
                } catch (ParseException ignored) {
                    reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                }

                if (reason == null && args.length > 2) {
                    reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                }

                source.sendMessage(Component.text(
                        "Target " + target + " has been banned." +
                                (parsedTime != null ? " Duration: " + parsedTime : " Permanently.") +
                                (reason != null ? " Reason: " + reason : ""), NamedTextColor.GREEN));
            } else {
                source.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
            }
        });
    }

    @Override
    public List<String> suggest(C source) {
        String[] args = source.getArgs();
        if (args.length == 1) {
            return Parse.players();
        } else if (args.length == 2) return MongoBanAPI.TIME_SUGGEST;
        else if (args.length == 3)
            return MongoBanAPI.REASON_SUGGEST;
        return MongoBanAPI.EMPTY_LIST;
    }

    @Override
    public boolean hasPermission(C source) {
        return source.hasPermission(getPermission());
    }
}
