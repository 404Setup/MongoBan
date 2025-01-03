package one.tranic.mongoban.api.command;

import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * The Parse class is responsible for parsing an array of command-line arguments
 * and mapping their corresponding values to predefine flags.
 * <p>
 * It supports both long-form (e.g., --flag) and short-form (e.g., -f) syntax for flags.
 * <p>
 * Flags and their corresponding possible forms are defined within the {@code CommandFlag} enum.
 */
@Deprecated
public class CommandParse {
    private final Map<CommandFlag, String> flags = new EnumMap<>(CommandFlag.class);

    /**
     * Parses an array of command-line arguments and associates each recognized flag
     * with its corresponding value in a map.
     * <p>
     * The method supports both long-form (e.g., --flag) and short-form (e.g., -f) flag syntax.
     *
     * @param args an array of strings representing the command-line arguments to parse;
     *             long-form flags are prefixed with "--", short-form flags are prefixed
     *             with "-", and their values, if any, follow as the next argument
     */
    public void parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String current = args[i];
            if (current.startsWith("--")) {
                String flagName = current.substring(2);
                CommandFlag flag = CommandFlag.fromString(flagName);
                if (flag != null) {
                    String value = (i + 1 < args.length && !args[i + 1].startsWith("-")) ? args[++i] : "bool";
                    flags.put(flag, value);
                }
            } else if (current.startsWith("-")) {
                String flagName = current.substring(1);
                CommandFlag flag = CommandFlag.fromString(flagName);
                if (flag != null) {
                    String value = (i + 1 < args.length && !args[i + 1].startsWith("-")) ? args[++i] : "bool";
                    flags.put(flag, value);
                }
            }
        }
    }

    /**
     * Retrieves the value associated with the specified command-line flag.
     *
     * @param flag the {@code CommandFlag} whose value is to be retrieved.
     *             <p>
     *             This can represent a command-line flag in both long-form (e.g., "--flag")
     *             and short-form (e.g., "-f") notation.
     * @return the value corresponding to the specified flag, or {@code null} if the flag
     * is not present or has no associated value.
     */
    public @Nullable String getFlagValue(CommandFlag flag) {
        return flags.get(flag);
    }

    /**
     * Checks if a specific flag is present in the parsed flags.
     *
     * @param flag the {@code CommandFlag} to check for in the parsed flags
     * @return {@code true} if the specified flag is present, {@code false} otherwise
     */
    public boolean hasFlag(CommandFlag flag) {
        return flags.containsKey(flag);
    }

    /**
     * Retrieves the value associated with the REASON flag from the parsed command-line arguments.
     *
     * @return the value of the REASON flag if specified, or {@code null} if the flag was not provided or has no value.
     */
    public @Nullable String getReasonFlag() {
        return getFlagValue(CommandFlag.REASON);
    }

    /**
     * Retrieves the value associated with the TIME flag from the parsed command-line arguments.
     *
     * @return the value of the TIME flag if specified, or null if the flag was not provided or has no value.
     */
    public @Nullable String getTimeFlag() {
        return getFlagValue(CommandFlag.TIME);
    }

    /**
     * Retrieves the value associated with the TARGET flag from the parsed command-line arguments.
     *
     * @return the value of the TARGET flag if specified, or {@code null} if the flag was not provided or has no value.
     */
    public @Nullable String getTargetFlag() {
        return getFlagValue(CommandFlag.TARGET);
    }

    /**
     * Determines whether the strict mode flag is enabled based on the parsed
     * command-line arguments.
     *
     * @return {@code true} if the strict mode flag is set to "bool", indicating
     * strict mode is active; {@code false} otherwise.
     */
    public boolean getStrictFlag() {
        return Objects.equals(getFlagValue(CommandFlag.STRICT), "bool");
    }
}
