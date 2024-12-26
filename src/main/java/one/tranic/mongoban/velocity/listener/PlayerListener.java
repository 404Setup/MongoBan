package one.tranic.mongoban.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;

import java.net.InetAddress;

public class PlayerListener {
    @Subscribe(priority = 0, order = PostOrder.CUSTOM)
    public void onPlayerLogin(com.velocitypowered.api.event.connection.LoginEvent event) {
        if (!event.getResult().isAllowed()) return;
        Player player = event.getPlayer();

        InetAddress ip = player.getRemoteAddress().getAddress();
        IPBanInfo result = MongoDataAPI.getDatabase().ban().ip().find(ip.getHostAddress()).sync();
        if (result != null) {
            if (result.expired()) {
                MongoDataAPI.getDatabase().ban().ip().remove(ip.getHostAddress()).async();
            } else {
                // A "permissive mode" option is needed
                MongoDataAPI.getDatabase().ban()
                        .player()
                        .add(player.getUniqueId(), MongoBanAPI.console, result.duration(), ip.getHostAddress(), result.reason())
                        .async();
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text(result.reason())));
                return;
            }
        } else {
            PlayerBanInfo playerResult = MongoDataAPI.getDatabase().ban().player()
                    .find(player.getUniqueId())
                    .sync();
            if (playerResult != null) {
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text(playerResult.reason())));
                return;
            }
        }

        updatePlayerInfoAsync(player, ip);
    }

    private void updatePlayerInfoAsync(Player player, InetAddress ip) {
        MongoDataAPI.getDatabase().player().add(player.getUsername(), player.getUniqueId(), ip.getHostAddress());
    }
}
