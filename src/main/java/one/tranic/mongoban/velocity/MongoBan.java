package one.tranic.mongoban.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;

@Plugin(
        id = "mongoban",
        name = "MongoBan",
        version = BuildConstants.VERSION,
        url = "https://tranic.one",
        authors = {"404"}
)
public class MongoBan {
    private static ProxyServer proxy;

    @Inject
    public MongoBan(ProxyServer proxy) {
        MongoBan.proxy = proxy;
    }

    public static ProxyServer getProxy() {
        return proxy;
    }
}
