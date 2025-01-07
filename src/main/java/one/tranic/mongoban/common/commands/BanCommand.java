package one.tranic.mongoban.common.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.args.BanArgs;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.api.exception.UnsupportedTypeException;
import one.tranic.mongoban.api.message.MessageFormat;
import one.tranic.mongoban.api.message.MessageKey;
import one.tranic.mongoban.api.parse.network.NetworkParser;
import one.tranic.mongoban.api.parse.time.TimeParser;
import one.tranic.mongoban.api.player.MongoPlayer;
import one.tranic.mongoban.api.player.Player;
import one.tranic.mongoban.common.form.GeyserForm;
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
                source.sendMessage(MessageKey.PERMISSION_DENIED.format());
                return;
            }

            if (player.isBedrockPlayer()) {
                player.sendFormAsync(GeyserForm.getDoForm(source, (form) -> exec(source, form)));
                return;
            }
        }

        String[] args = source.getArgs();

        if (args.length < 1) {
            source.sendMessage(MessageKey.BAN_INVALID_USAGE.format(
                    new MessageFormat("cmd", Component.text(getName(), NamedTextColor.BLUE))
            ));
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
                source.sendMessage(MessageKey.TARGET_MISSIONG.format());
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
                Component msg = MessageKey.PRIVATE_IP.format(
                        new MessageFormat("ip", Component.text(target, NamedTextColor.BLUE))
                );
                sendResult(source, msg, false);
                return;
            }

            IPBanInfo result = MongoDataAPI.getDatabase().ban().ip().find(inip).sync();
            if (result != null) {
                Component msg = MessageKey.ALREADY_BANNED.format(
                        new MessageFormat("target", Component.text(result.ip(), NamedTextColor.BLUE)),
                        new MessageFormat("operator", Component.text(result.operator().name(), NamedTextColor.BLUE)),
                        new MessageFormat("duration", Component.text(result.duration(), NamedTextColor.BLUE)),
                        new MessageFormat("reason", Component.text(result.reason(), NamedTextColor.BLUE))
                );
                sendResult(source, msg, false);
                return;
            }

            MongoDataAPI.getDatabase().ban().ip().add(inip, source.getOperator(), time, reason).async().thenAcceptAsync((v) -> {
                Component msg = MessageKey.BAN_MESSAGE.format(
                        new MessageFormat("target", Component.text(target, NamedTextColor.BLUE)),
                        new MessageFormat("operator", Component.text(source.getOperator().name(), NamedTextColor.BLUE)),
                        new MessageFormat("duration", Component.text(time, NamedTextColor.BLUE)),
                        new MessageFormat("reason", Component.text(reason, NamedTextColor.BLUE))
                );
                sendResult(source, msg);
            });
        } catch (Exception ignored) {
            MongoPlayer<?> targetPlayer = Player.getPlayer(target);
            String userIP;
            UUID uuid;

            if (targetPlayer == null) {
                PlayerInfo player = MongoDataAPI.getDatabase().player().find(target).sync();
                if (player == null) {
                    Component msg = MessageKey.TARGET_NOT_FOUND.format(
                            new MessageFormat("target", Component.text(target, NamedTextColor.YELLOW))
                    );
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
                Component msg = MessageKey.ALREADY_BANNED.format(
                        new MessageFormat("target", Component.text(target, NamedTextColor.BLUE)),
                        new MessageFormat("operator", Component.text(pBanInfo.operator().name(), NamedTextColor.BLUE)),
                        new MessageFormat("duration", Component.text(pBanInfo.duration(), NamedTextColor.BLUE)),
                        new MessageFormat("reason", Component.text(pBanInfo.reason(), NamedTextColor.BLUE))
                );
                sendResult(source, msg, false);
                return;
            }

            Component msg = MessageKey.BAN_MESSAGE.format(
                    new MessageFormat("target", Component.text(target, NamedTextColor.BLUE)),
                    new MessageFormat("operator", Component.text(source.getOperator().name(), NamedTextColor.BLUE)),
                    new MessageFormat("duration", Component.text(time, NamedTextColor.BLUE)),
                    new MessageFormat("reason", Component.text(reason, NamedTextColor.BLUE))
            );

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
