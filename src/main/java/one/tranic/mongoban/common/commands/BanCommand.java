package one.tranic.mongoban.common.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.args.BanArgs;
import one.tranic.mongoban.api.command.message.Message;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.exception.CommandException;
import one.tranic.mongoban.api.parse.time.TimeParser;
import one.tranic.mongoban.api.player.MongoPlayer;
import one.tranic.mongoban.api.player.Player;
import one.tranic.mongoban.common.form.GeyserForm;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;

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
            try {
                BanArgs parse = BanArgs.parse(args);

                @Nullable String target = parse.target().orElse(null);
                if (target == null || target.isEmpty()) {
                    source.sendMessage(Component.text("Target flag is missing! Use --target <playerName>|<ip> to specify the target.", NamedTextColor.RED));
                    return;
                }

                String time;
                {
                    @Nullable String timeArg = parse.duration().orElse(null);
                    if (timeArg == null) timeArg = "forever";
                    time = TimeParser.parse(timeArg);
                }

                @Nullable String reason = parse.reason().orElse(null);
                boolean strict = parse.strict().orElse(false);

                try {
                    InetAddress inip = InetAddress.getByName(target);
                    IPBanInfo result = MongoDataAPI.getDatabase().ban().ip().find(inip).sync();
                    if (result != null) {
                        if (result.expired()) MongoDataAPI.getDatabase().ban().ip().remove(inip).sync();
                        else {
                            source.sendMessage(Message.alreadyBannedMessage(result.ip(), result.operator(), result.duration(), result.reason()));
                            return;
                        }
                    }

                    MongoDataAPI.getDatabase().ban().ip().add(inip, source.getOperator(), time, reason);
                } catch (Exception ignored) {

                }

                MongoPlayer<?> targetPlayer = Player.getPlayer(target);
                if (targetPlayer == null) {
                    source.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
                    return;
                }

                TextComponent msg = Message.banMessage(target, time, reason, source.getOperator());

                source.sendMessage(msg);
                MongoBanAPI.CONSOLE_SOURCE.sendMessage(msg);
            } catch (Exception e) {
                throw new CommandException(e);
            }
        });
    }
}
