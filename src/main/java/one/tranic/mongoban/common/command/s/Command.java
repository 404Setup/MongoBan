package one.tranic.mongoban.common.command.s;

import one.tranic.mongoban.common.command.CommandImpl;
import one.tranic.mongoban.common.source.SourceImpl;

public abstract class Command<C extends SourceImpl<?>> implements CommandImpl<C> {
    private final C source;
    private String name;
    private String description;
    private String usage;
    private String permission;

    public Command(C source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public C getSource() {
        return source;
    }
}
