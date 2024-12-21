package one.tranic.mongoban.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import one.tranic.mongoban.common.Collections;
import one.tranic.mongoban.velocity.MongoBan;

import java.util.Iterator;
import java.util.List;

public class Parse {
    /**
     * Retrieves a list of usernames for all players currently managed by the proxy.
     *
     * @return a list of usernames of all players.
     */
    public static List<String> getPlayers() {
        List<String> matchingPlayers = Collections.newArrayList();
        Iterator<Player> iter = MongoBan.getProxy().getAllPlayers().iterator();
        while (iter.hasNext()) {
            matchingPlayers.add(iter.next().getUsername());
        }
        return matchingPlayers;
    }
}
