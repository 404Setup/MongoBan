package one.tranic.mongoban.api.command;

import java.util.EnumMap;
import java.util.Map;

public class Parse {
    private final Map<CommandFlag, String> flags = new EnumMap<>(CommandFlag.class);

    public void parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String current = args[i];
            if (current.startsWith("--")) {
                String flagName = current.substring(2);
                CommandFlag flag = CommandFlag.fromString(flagName);
                if (flag != null) {
                    String value = (i + 1 < args.length && !args[i + 1].startsWith("-")) ? args[++i] : null;
                    flags.put(flag, value);
                }
            } else if (current.startsWith("-")) {
                String flagName = current.substring(1);
                CommandFlag flag = CommandFlag.fromString(flagName);
                if (flag != null) {
                    String value = (i + 1 < args.length && !args[i + 1].startsWith("-")) ? args[++i] : null;
                    flags.put(flag, value);
                }
            }
        }
    }
}
