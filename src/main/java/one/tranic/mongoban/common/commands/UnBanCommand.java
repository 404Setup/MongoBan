package one.tranic.mongoban.common.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.args.UnBanArgs;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.api.message.MessageKey;
import one.tranic.mongoban.common.form.GeyserForm;
import one.tranic.t.base.TBase;
import one.tranic.t.base.command.source.CommandSource;
import one.tranic.t.base.exception.UnsupportedTypeException;
import one.tranic.t.base.message.MessageFormat;
import one.tranic.t.network.TNetwork;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.List;
import java.util.UUID;

// Todo
public class UnBanCommand<C extends CommandSource<?, ?>> extends Command<C> {
    public UnBanCommand() {
        setName("unban");
        setPermission("mongoban.command.unban");
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
                player.sendFormAsync(GeyserForm.getUndoForm(form -> exec(source, form)));
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

        if (arg instanceof GeyserForm.SimpleForm args) {
            target = args.player();
        } else if (arg instanceof UnBanArgs args) {
            target = args.target().orElse(null);
            if (target == null || target.isEmpty()) {
                source.sendMessage(MessageKey.TARGET_MISSIONG.format());
                return;
            }
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

            MongoDataAPI.getDatabase().ban().ip().remove(inip).async();
        } catch (Exception ignored) {
            PlayerInfo player = MongoDataAPI.getDatabase().player().find(target).sync();
            if (player == null) {
                Component msg = MessageKey.TARGET_NOT_FOUND.format(
                        new MessageFormat("target", Component.text(target, NamedTextColor.YELLOW))
                );
                sendResult(source, msg, false);
                return;
            }
            UUID uuid = player.uuid();

            MongoDataAPI.getDatabase().ban().player().find(uuid).async().thenAcceptAsync(result -> {
                Component msg;
                if (result == null) {
                    msg = MessageKey.TARGET_NOT_FOUND.format(
                            new MessageFormat("target", Component.text(target, NamedTextColor.YELLOW))
                    );
                } else {
                    MongoDataAPI.getDatabase().ban().player().remove(result.uuid()).sync();
                    msg = MessageKey.ACTION_MESSGAE.format(
                            new MessageFormat("target", Component.text(target, NamedTextColor.BLUE)),
                            new MessageFormat("action", Component.text("Unbanned", NamedTextColor.GREEN))
                    );
                }
                sendResult(source, msg);
            }, TBase.executor);
        }
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
