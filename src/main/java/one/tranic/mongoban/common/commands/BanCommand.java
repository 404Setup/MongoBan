package one.tranic.mongoban.common.commands;

import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.command.Command;
import one.tranic.mongoban.api.command.source.SourceImpl;
import one.tranic.mongoban.api.exception.ParseException;
import one.tranic.mongoban.api.player.MongoPlayer;
import one.tranic.mongoban.common.Parse;
import one.tranic.mongoban.common.form.GeyserForm;

import java.util.Arrays;
import java.util.List;

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
                player.sendForm(GeyserForm.getDoForm());
                return;
            }
        }

        String[] args = source.getArgs();

        if (args.length < 1) {
            source.sendMessage(Component.text(
                    "Invalid usage! Use: /" + getName() + " --name <playerName> --time [time] --reason [reason]", NamedTextColor.RED));
            return;
        }

        String playerName = args[0];
        String timeArg = args.length > 1 ? args[1] : null;
        String reason = null;

        Player target = one.tranic.mongoban.api.player.Player.getPlayer(playerName);
        if (target != null) {
            long parsedTime = 0;
            try {
                parsedTime = Parse.timeArg(timeArg);
            } catch (ParseException ignored) {
                reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            }

            if (reason == null && args.length > 2) {
                reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
            }

            source.sendMessage(Component.text(
                    "Player " + playerName + " has been banned." +
                            (parsedTime > 0 ? " Duration: " + timeArg : " Permanently.") +
                            (reason != null ? " Reason: " + reason : ""), NamedTextColor.GREEN));
        } else {
            source.sendMessage(Component.text("Player not found!", NamedTextColor.RED));
        }
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
