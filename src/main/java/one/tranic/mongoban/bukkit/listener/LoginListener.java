package one.tranic.mongoban.bukkit.listener;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.message.MessageKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LoginListener extends one.tranic.mongoban.api.listener.Listener<AsyncPlayerPreLoginEvent> implements Listener {
    @Override
    @EventHandler(priority = EventPriority.LOW)
    public void onPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        if (!isAllowed(event)) return;

        doIt(event, event.getName(), event.getUniqueId(), event.getAddress());
    }

    @Override
    public boolean isAllowed(AsyncPlayerPreLoginEvent event) {
        return event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED;
    }

    @Override
    public void disallow(AsyncPlayerPreLoginEvent event, @Nullable Component reason) {
        event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                Objects.requireNonNullElse(reason, MessageKey.DEFAULT_KICK.format())
        );
    }
}
