package one.tranic.mongoban.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerInfo;
import one.tranic.mongoban.api.exception.ParseException;
import one.tranic.mongoban.api.parse.player.PlayerParser;
import one.tranic.mongoban.api.parse.time.TimeParser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * @deprecated Merge IPBan logic into Ban
 */
@Deprecated
public class BanIPCommand implements SimpleCommand {
    private final ProxyServer proxy;

    public BanIPCommand(ProxyServer proxy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        String[] args = invocation.arguments();
        CommandSource source = invocation.source();

        if (args.length < 1) {
            source.sendMessage(Component.text(
                    "Invalid usage! Use: /vban-ip <ip or playerName> [time] [reason]", NamedTextColor.RED));
            return;
        }

        String ip = args[0];
        String timeArg = args.length > 1 ? args[1] : null;
        String reason = null;

        try {
            InetAddress inip = InetAddress.getByName(ip);
            IPBanInfo result = MongoDataAPI.getDatabase().ban().ip().find(inip.getHostAddress()).sync();
            if (result != null) {
                if (result.expired()) {
                    MongoDataAPI.getDatabase().ban().ip().remove(inip.getHostAddress()).async();
                } else {
                    source.sendMessage(Component.text("The specified IP address is already banned:", NamedTextColor.YELLOW));
                    source.sendMessage(Component.text("IP: " + result.ip(), NamedTextColor.GOLD));
                    source.sendMessage(Component.text("Operator: " + result.operator().name(), NamedTextColor.GREEN));
                    source.sendMessage(Component.text("Duration: " + (result.duration() != null ? result.duration() : "Permanent"), NamedTextColor.BLUE));
                    source.sendMessage(Component.text("Reason: " + result.reason(), NamedTextColor.RED));
                    return;
                }
            }
        } catch (UnknownHostException e) {
            Player player = proxy.getPlayer(ip).orElse(null);
            if (player != null) ip = player.getRemoteAddress().getAddress().getHostAddress();
            else {
                PlayerInfo mongoPlayer = MongoDataAPI.getDatabase().player().find(ip).sync();
                if (mongoPlayer == null || mongoPlayer.ip().isEmpty()) {
                    source.sendMessage(Component.text("Invalid IP address provided!", NamedTextColor.RED));
                    return;
                }
                ip = mongoPlayer.ip().getLast();
            }
        }
        String parsedTime = null;
        try {
            parsedTime = TimeParser.parse(timeArg);
        } catch (ParseException ignored) {
            reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        }

        if (reason == null && args.length > 2) {
            reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        }

        source.sendMessage(Component.text(
                "IP " + ip + " has been banned." +
                        (parsedTime != null ? " Duration: " + timeArg : " Permanently.") +
                        (reason != null ? " Reason: " + reason : ""), NamedTextColor.GREEN));
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) {
            return PlayerParser.parse();
        } else if (args.length == 2) return MongoBanAPI.TIME_SUGGEST;
        else if (args.length == 3)
            return MongoBanAPI.REASON_SUGGEST;
        return MongoBanAPI.EMPTY_LIST;
    }

    @Override
    public boolean hasPermission(SimpleCommand.Invocation invocation) {
        return invocation.source().hasPermission("mongoban.ban.player");
    }
}
