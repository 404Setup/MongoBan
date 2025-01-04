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
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.api.exception.UnsupportedTypeException;
import one.tranic.mongoban.api.parse.time.TimeParser;
import one.tranic.mongoban.api.player.MongoPlayer;
import one.tranic.mongoban.api.player.Player;
import one.tranic.mongoban.common.form.GeyserForm;
import one.tranic.mongoban.api.parse.network.NetworkParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.UUID;

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

            if (player.isBedrockPlayer()) {
                player.sendFormAsync(GeyserForm.getDoForm(source, (form) -> exec(source, form)));
                return;
            }
        }

        String[] args = source.getArgs();

        if (args.length < 1) {
            source.sendMessage(Component.text(
                    "Invalid usage! Use: /" + getName() + " --name <playerName> --time [time] --reason [reason]", NamedTextColor.RED));
            return;
        }

        MongoBanAPI.runAsync(() -> exec(source, args));
    }

    private void exec(C source, Object arg) {
        @Nullable String target;
        String reason;
        String time;
        boolean strict;

        if (arg instanceof GeyserForm.DoForm args) {
            target = args.player();
            reason = args.reason() == null || args.reason().isBlank() ? "<Banned by ServerAdmin>" : args.reason();

            if (args.duration() < 1) time = "forever";
            else time = args.duration_unit() == "forever" ?
                    "forever" : args.duration() + args.duration_unit();

            strict = args.strict();
        } else if (arg instanceof BanArgs args) {
            target = args.target().orElse(null);
            if (target == null || target.isEmpty()) {
                source.sendMessage(Component.text("Target flag is missing! Use --target <playerName>|<ip> to specify the target.", NamedTextColor.RED));
                return;
            }

            time = TimeParser.parse(args.duration().orElse("forever"));
            reason = args.reason().orElse("<Banned by ServerAdmin>");
            strict = args.strict().orElse(false);
        } else throw new UnsupportedTypeException(arg);

        try {
            InetAddress inip = InetAddress.getByName(target);

            // Check Private IP
            if (NetworkParser.isPrivateIp(inip)) {
                TextComponent msg = Message.failedPrivateIPMessage(target);
                sendResult(source, msg, false);
                return;
            }

            IPBanInfo result = MongoDataAPI.getDatabase().ban().ip().find(inip).sync();
            if (result != null) {
                TextComponent msg = Message.alreadyBannedMessage(result.ip(), result.operator(), result.duration(), result.reason());
                sendResult(source, msg, false);
                return;
            }

            MongoDataAPI.getDatabase().ban().ip().add(inip, source.getOperator(), time, reason).async().thenAcceptAsync((v) -> {
                TextComponent msg = Message.banMessage(target, time, reason, source.getOperator());
                sendResult(source, msg);
            });
        } catch (Exception ignored) {
            MongoPlayer<?> targetPlayer = Player.getPlayer(target);
            String userIP;
            UUID uuid;

            if (targetPlayer == null) {
                PlayerInfo player = MongoDataAPI.getDatabase().player().find(target).sync();
                if (player == null) {
                    @NotNull TextComponent msg = Component.text("Target " + target + " not found!", NamedTextColor.RED);
                    sendResult(source, msg, false);
                    return;
                }
                userIP = player.ip().getLast();
                uuid = player.uuid();
            } else {
                userIP = targetPlayer.getConnectHost();
                uuid = targetPlayer.getUniqueId();
            }

            PlayerBanInfo pBanInfo = MongoDataAPI.getDatabase().ban().player().find(uuid).sync();
            if (pBanInfo != null) {
                TextComponent msg = Message.alreadyBannedMessage(target, pBanInfo.operator(), pBanInfo.duration(), pBanInfo.reason());
                sendResult(source, msg, false);
                return;
            }

            TextComponent msg = Message.banMessage(target, time, reason, source.getOperator());

            if (strict) {
                MongoDataAPI.getDatabase().ban().ip().add(userIP, source.getOperator(), time, reason)
                        .async()
                        .thenAcceptAsync((v) -> {
                            sendResult(source, msg);

                            if (!v.isEmpty()) for (PlayerInfo player : v) {
                                MongoPlayer<?> p = Player.getPlayer(player.uuid());
                                if (p != null) p.kick(msg);
                            }
                        }, MongoBanAPI.executor);
            } else {
                MongoDataAPI.getDatabase().ban().player().add(uuid, source.getOperator(), time, null, reason)
                        .async()
                        .thenAcceptAsync((v) -> {
                            sendResult(source, msg);

                            if (targetPlayer != null) targetPlayer.kick(msg);
                        });
            }
        }
    }
}
