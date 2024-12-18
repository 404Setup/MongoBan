package one.tranic.mongoban.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.common.Collections;

import java.util.Arrays;
import java.util.List;

public class BanCommand implements SimpleCommand {
    private final ProxyServer proxy;

    public BanCommand(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();

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
            if (timeArg != null) {
                try {
                    if (timeArg.endsWith("s")) {
                        parsedTime = System.currentTimeMillis() + Long.parseLong(timeArg.replace("s", "")) * 1000;
                    } else if (timeArg.endsWith("m")) {
                        parsedTime = System.currentTimeMillis() + Long.parseLong(timeArg.replace("m", "")) * 60000;
                    } else if (timeArg.endsWith("h")) {
                        parsedTime = System.currentTimeMillis() + Long.parseLong(timeArg.replace("h", "")) * 3600000;
                    } else if (timeArg.endsWith("d")) {
                        parsedTime = System.currentTimeMillis() + Long.parseLong(timeArg.replace("d", "")) * 86400000;
                    } else if (timeArg.endsWith("mo")) {
                        parsedTime = System.currentTimeMillis() + Long.parseLong(timeArg.replace("mo", "")) * 2592000000L;
                    } else if (timeArg.endsWith("y")) {
                        parsedTime = System.currentTimeMillis() + Long.parseLong(timeArg.replace("y", "")) * 31536000000L;
                    } else {
                        reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                    }
                } catch (NumberFormatException e) {
                    reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                }
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
            return proxy.getAllPlayers().stream()
                    .map(Player::getUsername)
                    .filter(playerName -> playerName.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList();
        } else if (args.length == 2) return Collections.newArrayList("10s", "1m", "1h", "1d", "1mo", "1y", "<reason>");
        else if (args.length == 3)
            return Collections.newArrayList("Griefing", "Cheating", "Spamming", "Abusing", "<reason>");
        return List.of();
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("mongoban.ban.player");
    }
}
