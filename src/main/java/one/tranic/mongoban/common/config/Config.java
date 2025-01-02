package one.tranic.mongoban.common.config;

public record Config(int cache, database database, redis redis, updater updater) {

    public record database(String host, int port, String database, String user, String password) {
    }

    public record redis(String host, int port, int db, String user, String password) {
    }

    public record updater(boolean enable, boolean simpleMode) {

    }
}
