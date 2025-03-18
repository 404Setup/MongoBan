package one.tranic.mongoban.api.command;

import net.kyori.adventure.text.Component;
import one.tranic.mongoban.api.MongoBanAPI;
import one.tranic.mongoban.api.form.GeyserForm;
import one.tranic.mongoban.api.parse.player.PlayerParser;
import one.tranic.t.base.TBase;
import one.tranic.t.base.command.simple.SimpleCommand;
import one.tranic.t.base.command.source.CommandSource;

import java.util.List;
import java.util.Map;

/**
 * Abstract base class representing a command in a multi-platform environment.
 * <p>
 * This class provides methods to manage command properties such as name, description, usage,
 * and permissions, as well as utilities to handle platform-specific command registration and unwrapping.
 *
 * @param <C> the type of the command source, extending from {@link CommandSource}
 */
public abstract class Command<C extends CommandSource<?, ?>> extends SimpleCommand<C> {
    private final Map<String, List<String>> suggestionMap = Map.of(
            "--target", PlayerParser.parse(30),
            "--duration", MongoBanAPI.TIME_SUGGEST,
            "--reason", MongoBanAPI.REASON_SUGGEST
    );

    @Override
    public List<String> suggest(C source) {
        if (!hasPermission(source)) return MongoBanAPI.EMPTY_LIST;

        String[] args = source.getArgs();
        int size = source.argSize();
        if (size == 1)
            return filterSuggestions(MongoBanAPI.FLAG_LIST, args[0]);
        if (size > 1) {
            String previousArg = args[size - 2];
            String currentArg = args[size - 1];

            if (suggestionMap.containsKey(previousArg)) {
                return filterSuggestions(suggestionMap.get(previousArg), currentArg);
            }

            if (MongoBanAPI.FLAG_LIST.contains(previousArg)) {
                return filterSuggestions(MongoBanAPI.FLAG_LIST, currentArg);
            }
        }
        return MongoBanAPI.EMPTY_LIST;
    }

    private List<String> filterSuggestions(List<String> suggestions, String prefix) {
        return suggestions.stream()
                .filter(suggestion -> suggestion.startsWith(prefix))
                .toList();
    }


    /**
     * Sends a message result to a given source, taking into account whether the source is
     * a Bedrock player, a standard player, or whether the message should also be sent to the console.
     *
     * @param source      the source to which the result should be sent; can be a player or other entity
     * @param msg         the message to be sent, represented as a {@link Component}
     * @param withConsole if true, the message will also be sent to the console
     */
    @Override
    public void sendResult(C source, Component msg, boolean withConsole) {
        if (source.isBedrockPlayer()) source.asPlayer().sendFormAsync(GeyserForm.getMessageForm(msg));
        else if (source.isPlayer()) source.sendMessage(msg);
        if (withConsole) TBase.getConsoleSource().sendMessage(msg);
    }

    /**
     * Sends a result message to the specified source and optionally to the console.
     *
     * @param source      the source to which the result is sent; it can represent a player or another entity
     * @param msg         the message to be sent
     * @param withConsole whether the message should also be sent to the console
     */
    @Override
    public void sendResult(C source, String msg, boolean withConsole) {
        if (source.isBedrockPlayer()) source.asPlayer().sendFormAsync(GeyserForm.getMessageForm(msg));
        else if (source.isPlayer()) source.sendMessage(msg);
        if (withConsole) TBase.getConsoleSource().sendMessage(msg);
    }
}
