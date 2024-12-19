package one.tranic.mongoban.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.common.Collections;
import one.tranic.mongoban.velocity.MongoBan;

import java.net.InetAddress;

public class PlayerListener {
    @Subscribe(priority = 0, order = PostOrder.CUSTOM)
    public void onPlayerLogin(com.velocitypowered.api.event.connection.LoginEvent event) {
        if (!event.getResult().isAllowed()) return;
        Player player = event.getPlayer();

        InetAddress ip = player.getRemoteAddress().getAddress();
        IPBanInfo result = MongoBan.getDatabase().getBanApplication().getIPBanInfoSync(ip);
        if (result != null) {
            if (result.expired()) {
                MongoBan.getDatabase().getBanApplication().removePlayerBanAsync(ip);
            } else {
                MongoBan.getDatabase().getBanApplication().findPlayerBanAsync(player.getUniqueId())
                        .thenAcceptAsync(playerBanInfo -> {
                            if (playerBanInfo == null)
                                MongoBan.getDatabase().getBanApplication().addPlayerBanAsync(player.getUniqueId(), MongoBanAPI.console, result.duration(), ip, result.reason());
                        }, MongoBanAPI.executor);
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text(result.reason())));
                return;
            }
        } else {
            PlayerBanInfo playerResult = MongoBan.getDatabase().getBanApplication().findPlayerBanSync(player.getUniqueId());
            if (playerResult != null) {
                event.setResult(ResultedEvent.ComponentResult.denied(Component.text(playerResult.reason())));
                return;
            }
        }

        updatePlayerInfoAsync(player, ip);
    }

    private void updatePlayerInfoAsync(Player player, InetAddress ip) {
        MongoBan.getDatabase().getPlayerApplication().getPlayerAsync(player.getUniqueId()).thenAcceptAsync(playerInfo -> {
            if (playerInfo != null) {
                if (!playerInfo.ip().contains(ip.getHostAddress()))
                    MongoBan.getDatabase().getPlayerApplication().addPlayerSync(playerInfo.name(), playerInfo.uuid(), ip.getHostAddress());
            } else
                MongoBan.getDatabase().getPlayerApplication().addPlayerSync(player.getUsername(), player.getUniqueId(), ip.getHostAddress());
        }, MongoBanAPI.executor);
    }
}
