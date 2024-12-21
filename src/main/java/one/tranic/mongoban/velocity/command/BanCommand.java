package one.tranic.mongoban.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.exception.ParseException;
import one.tranic.mongoban.common.Parse;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.geysermc.cumulus.form.CustomForm;

import java.util.Arrays;
import java.util.List;

public class BanCommand implements SimpleCommand {
    private final ProxyServer proxy;

    public BanCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();

        if (source instanceof Player player) {
            if (!hasPermission(invocation)) {
                source.sendMessage(Component.text("Permission denied!", NamedTextColor.RED));
                return;
            }

            // TODO： Bedrock command executors will pass a form instead of a command
            if (MongoBanAPI.isBedrockPlayer(player.getUniqueId())) {
                @NonNull CustomForm form = CustomForm.builder()
                        .title("MongoBan Console - Ban")
                        .dropdown("Player", one.tranic.mongoban.velocity.utils.Parse.getPlayers())
                        .input("Duration")
                        .dropdown("Duration unit", MongoBanAPI.TIME_SUGGEST)
                        .input("Reason")
                        .toggle("Ban IP")
                        .validResultHandler(response -> {

                        })
                        .build();
                MongoBanAPI.sendForm(player.getUniqueId(), form);
                return;
            }
        }



        String[] args = invocation.arguments();

        if (args.length < 1) {
            source.sendMessage(Component.text(
                    "Invalid usage! Use: /vban <playerName> [time] [reason]", NamedTextColor.RED));
            return;
        }

        String playerName = args[0];
        String timeArg = args.length > 1 ? args[1] : null;
        String reason = null;

        Player player = proxy.getPlayer(playerName).orElse(null);
        if (player != null) {
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
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) {
            return one.tranic.mongoban.velocity.utils.Parse.getPlayers();
        } else if (args.length == 2) return MongoBanAPI.TIME_SUGGEST;
        else if (args.length == 3)
            return MongoBanAPI.REASON_SUGGEST;
        return MongoBanAPI.EMPTY_LIST;
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("mongoban.ban.player");
    }
}
