package one.tranic.mongoban.velocity.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.command.message.Message;
import one.tranic.mongoban.api.listener.Listener;
import org.jetbrains.annotations.Nullable;

import java.net.InetAddress;
import java.util.Objects;

public class PlayerListener extends Listener<LoginEvent> {
    @Override
    public boolean isAllowed(LoginEvent event) {
        return event.getResult().isAllowed();
    }

    @Override
    public void disallow(LoginEvent event, @Nullable Component reason) {
        event.setResult(
                ResultedEvent.ComponentResult.denied(
                        Objects.requireNonNullElse(reason, Message.DEFAULT_KICK_MESSAGE)
                )
        );
    }

    @Override
    @Subscribe(priority = 0, order = PostOrder.CUSTOM)
    public void onPreLoginEvent(LoginEvent event) {
        if (!isAllowed(event)) return;
        Player player = event.getPlayer();
        InetAddress ip = player.getRemoteAddress().getAddress();

        doIt(event, player.getUsername(), player.getUniqueId(), ip);
    }
}
