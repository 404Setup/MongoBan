package one.tranic.mongoban.api.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.args.BanArgs;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.api.message.MessageKey;
import one.tranic.mongoban.api.form.GeyserForm;
import one.tranic.t.base.TBase;
import one.tranic.t.base.command.source.CommandSource;
import one.tranic.t.base.exception.UnsupportedTypeException;
import one.tranic.t.base.message.MessageFormat;
import one.tranic.t.base.parse.time.TimeParser;
import one.tranic.t.base.player.Player;
import one.tranic.t.network.TNetwork;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.Objects;
import java.util.UUID;

// Todo - Unfinished
public class BanCommand<C extends CommandSource<?, ?>> extends Command<C> {
    public BanCommand() {
        this.setName("ban");
        this.setPermission("mongoban.command.ban");
    }

    @Override
    public void execute(C source) {
        var player = source.asPlayer();

        if (player != null) {
            if (!hasPermission(source)) {
                source.sendMessage(MessageKey.PERMISSION_DENIED.format());
                return;
            }

            if (player.isBedrockPlayer()) {
                player.sendFormAsync(GeyserForm.getDoForm(form -> exec(source, form)));
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

        TBase.runAsync(() -> exec(source, args));
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
            else time = Objects.equals(args.duration_unit(), "forever") ?
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
            if (TNetwork.isPrivateIp(inip)) {
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
            var targetPlayer = Player.getPlayer(target);
            String name;
            String userIP;
            UUID uuid;

            if (targetPlayer == null) {
                PlayerInfo player = MongoDataAPI.getDatabase().player().find(target).sync();
                if (player != null) {
                    userIP = player.ip().getLast();
                    uuid = player.uuid();
                    name = player.name();
                } else {
                    // 1. No online players available
                    // 2. The player was not found in the database
                    // Then pre-populate the ID first, and then complete the ban data when it is added.
                    uuid = null;
                    userIP = null;
                    name = target;
                }
            } else {
                userIP = targetPlayer.getConnectedHost();
                uuid = targetPlayer.getUniqueId();
                name = targetPlayer.getUsername();
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

            if (strict && userIP != null) {
                MongoDataAPI.getDatabase().ban().ip().add(userIP, source.getOperator(), time, reason)
                        .async()
                        .thenAcceptAsync((v) -> {
                            sendResult(source, msg);

                            if (!v.isEmpty()) for (PlayerBanInfo player : v) {
                                var p = Player.getPlayer(player.uuid());
                                if (p != null) p.kick(msg);
                            }
                        }, TBase.executor);
            } else {
                MongoDataAPI.getDatabase().ban().player().add(uuid, name, source.getOperator(), time, null, reason)
                        .async()
                        .thenAcceptAsync((v) -> {
                            sendResult(source, msg);

                            if (targetPlayer != null) targetPlayer.kick(msg);
                        });
            }
        }
    }
}
