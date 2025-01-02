package one.tranic.mongoban.bungee.listener;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import one.tranic.mongoban.api.command.message.Message;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;
import java.util.Objects;

public class LoginListener extends one.tranic.mongoban.api.listener.Listener<LoginEvent> implements Listener {
    @Override
    @EventHandler
    public void onPreLoginEvent(LoginEvent event) {
        if (!isAllowed(event)) return;
        PendingConnection connection = event.getConnection();

        doIt(event, connection.getName(), connection.getUniqueId(), ((InetSocketAddress) connection.getSocketAddress()).getAddress());
    }

    @Override
    public boolean isAllowed(LoginEvent event) {
        return !event.isCancelled();
    }

    @Override
    public void disallow(LoginEvent event, @Nullable Component reason) {
        BaseComponent[] message = Message.toBaseComponent(
                Objects.requireNonNullElse(reason, Message.DEFAULT_KICK_MESSAGE)
        );
        event.setCancelReason(message);
        event.setCancelled(true);
    }
}
