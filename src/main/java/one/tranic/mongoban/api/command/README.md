# MongoBan SimpleCommand
SimpleCommand component package has been moved to [TLIB](https://github.com/404Setup/t-base).

SimpleCommand API allows developers to provide command services to multiple platforms 
at the same time through the same set of code (or fine-tuning).

But if your business code is very different in each platform, then you should not use it. 

SimpleCommand API is just to minimize duplicate model code, it is not suitable for this scene.

## Usage

### Create a command class
```java
public class BanCommand<C extends SourceImpl<?, ?>> extends Command<C> {
    public BanCommand() {
        // There is no need to register according to the platform as different names and different 
        // permissions, the SimpleCommand API will automatically handle it.
        this.setName("ban");
        this.setPermission("mongoban.command.ban");
    }

    @Override
    public void execute(C source) {
        source.sendMessage("Ban command not implemented yet.");
    }

    @Override
    public List<String> suggest(C source) {
        return List.of();
    }

    @Override
    public boolean hasPermission(C source) {
        return false;
    }
}
```

### Register
#### Paper
```java
@Override
    public void onEnable() {
        try {
            Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (SimpleCommandMap) commandMapField.get(Bukkit.getPluginManager());

            new BanCommand<PaperSource>().registerWithBukkit(commandMap, "mongoban");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
```

#### Velocity
```java
private void createCommands() {
        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("vban")
                .plugin(this)
                .build();

        @Nullable Command banCommand = new BanCommand<VelocitySource>().unwrapVelocity();

        commandManager.register(commandMeta, banCommand);
}
```