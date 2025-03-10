package one.tranic.mongoban.api;

import one.tranic.mongoban.api.parse.json.GsonParser;
import one.tranic.mongoban.api.parse.json.JsonParser;
import one.tranic.t.utils.Collections;

import java.util.List;

public class MongoBanAPI {

    public final static List<String> EMPTY_LIST = Collections.newUnmodifiableList();
    public final static List<String> FLAG_LIST = Collections.newUnmodifiableList("--target", "--duration", "--reason", "--strict");
    public final static List<String> REASON_SUGGEST = Collections.newUnmodifiableList("Griefing", "Cheating", "Spamming", "Abusing", "OtherReason");
    public final static List<String> TIME_SUGGEST = Collections.newUnmodifiableList("s", "m", "h", "d", "mo", "y", "forever");

    /**
     * A static instance of {@link JsonParser} that provides JSON parsing and serialization functionalities.
     */
    public final static JsonParser jsonParser = new GsonParser();

    /*private static SourceImpl<?, ?> getConsoleSource() {
        if (Platform.get() == Platform.BungeeCord)
            return new one.tranic.mongoban.api.command.source.BungeeSource(
                    net.md_5.bungee.api.ProxyServer.getInstance().getConsole(), null
            );
        if (Platform.get() == Platform.Velocity)
            return new one.tranic.mongoban.api.command.source.VelocitySource(
                    one.tranic.mongoban.velocity.MongoBan.getProxy().getConsoleCommandSource()
            );
        return new one.tranic.mongoban.api.command.source.PaperSource(
                org.bukkit.Bukkit.getConsoleSender(), null
        );
    }*/
}
