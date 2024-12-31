package one.tranic.mongoban.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.MongoDataAPI;
import one.tranic.mongoban.api.command.message.Message;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;

import java.net.InetAddress;

public class PlayerListener {
    @Subscribe(priority = 0, order = PostOrder.CUSTOM)
    public void onPlayerLogin(LoginEvent event) {
        if (!event.getResult().isAllowed()) return;
        Player player = event.getPlayer();
        InetAddress ip = player.getRemoteAddress().getAddress();

        IPBanInfo result = MongoDataAPI.getDatabase().ban().ip().find(ip.getHostAddress()).sync();
        if (result != null) {
            MongoDataAPI.getDatabase().ban()
                    .player().find(player.getUniqueId())
                    .async()
                    .thenAcceptAsync((p) -> {
                        if (p != null) return;
                        MongoDataAPI.getDatabase().ban()
                                .player()
                                .add(player.getUniqueId(), MongoBanAPI.console, result.duration(), ip.getHostAddress(), result.reason())
                                .sync();
                    }, MongoBanAPI.executor);
            event.setResult(ResultedEvent.ComponentResult.denied(Message.kickMessage(result)));
            return;
        } else {
            PlayerBanInfo playerResult = MongoDataAPI.getDatabase().ban().player()
                    .find(player.getUniqueId())
                    .sync();
            if (playerResult != null) {
                event.setResult(ResultedEvent.ComponentResult.denied(Message.kickMessage(playerResult)));
                return;
            }
        }

        updatePlayerInfoAsync(player, ip);
    }

    private void updatePlayerInfoAsync(Player player, InetAddress ip) {
        MongoDataAPI.getDatabase().player().add(player.getUsername(), player.getUniqueId(), ip.getHostAddress());
    }
}
