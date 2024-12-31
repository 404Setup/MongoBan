package one.tranic.mongoban.api.command.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.data.IPBanInfo;
import one.tranic.mongoban.api.data.Operator;
import one.tranic.mongoban.api.data.PlayerBanInfo;
import one.tranic.mongoban.api.exception.UnsupportedTypeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Message {
    public static TextComponent alreadyBannedMessage(@NotNull String target, @NotNull Operator operator, String duration, String reason) {
        TextComponent.Builder message = Component.text();

        message.append(Component.text("The specified target has been banned:", NamedTextColor.YELLOW));
        message.append(Component.text("\nTarget: ", NamedTextColor.GREEN));
        message.append(Component.text(target, NamedTextColor.BLUE));
        message.append(Component.text("\nOperator: ", NamedTextColor.GREEN));
        message.append(Component.text(operator.name(), NamedTextColor.BLUE));
        message.append(Component.text("\nDuration: ", NamedTextColor.GREEN));
        message.append(Component.text(duration, NamedTextColor.BLUE));
        message.append(Component.text("\nReason: ", NamedTextColor.GREEN));
        message.append(Component.text(reason, NamedTextColor.BLUE));

        return message.build();
    }

    public static TextComponent banMessage(@NotNull String target, @NotNull String duration, @Nullable String reason, @NotNull Operator operator) {
        TextComponent.Builder message = Component.text();

        message.append(Component.text("Target ", NamedTextColor.GREEN));
        message.append(Component.text(target, NamedTextColor.BLUE));
        message.append(Component.text(" has been banned.\n", NamedTextColor.GREEN));
        message.append(Component.text("Operator: ", NamedTextColor.GREEN));
        message.append(Component.text(operator.name(), NamedTextColor.BLUE));
        message.append(Component.text("\nDuration: ", NamedTextColor.GREEN));
        message.append(Component.text(duration, NamedTextColor.BLUE));
        message.append(Component.text("\nReason: ", NamedTextColor.GREEN));
        message.append(Component.text(reason != null ? reason : "<None>", NamedTextColor.BLUE));

        return message.build();
    }

    public static TextComponent kickMessage(@NotNull Object banInfo) {
        TextComponent.Builder message = Component.text();
        message.append(Component.text("Being kicked from the server\n", NamedTextColor.RED));

        String reason;
        String duration;
        if (banInfo instanceof IPBanInfo info) {
            reason = info.reason();
            duration = info.duration();
        } else if (banInfo instanceof PlayerBanInfo info) {
            reason = info.reason();
            duration = info.duration();
        } else throw new UnsupportedTypeException(banInfo);

        message.append(Component.text("Reason: ", NamedTextColor.GREEN));
        message.append(Component.text(reason, NamedTextColor.BLUE));
        message.append(Component.text("\nDuration: ", NamedTextColor.GREEN));
        message.append(Component.text(duration, NamedTextColor.BLUE));

        return message.build();
    }
}
