package one.tranic.mongoban.api.command.message;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import one.tranic.mongoban.api.data.Operator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Message {
    public static TextComponent banMessage(@NotNull String target, @NotNull String duration , @Nullable String reason, @NotNull Operator operator) {
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
}
