package one.tranic.mongoban.paper.util;

//import one.tranic.irs.PluginSchedulerBuilder;
import one.tranic.mongoban.paper.MongoBan;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;

// This is just a test class, it will not be used in mongoban
public class RegionMapRender {
    public void renderMapForPlayer(Player player) {
        /*PluginSchedulerBuilder.builder(MongoBan.getInstance())
                .sync(player)
                .task(() -> {
                    ItemStack offHandItem = player.getInventory().getItemInOffHand();
                    if (offHandItem.getType() != Material.FILLED_MAP) return;

                    MapView mapView = Bukkit.getMap(offHandItem.getDurability());
                    if (mapView == null) return;

                    mapView.setCenterX(player.getLocation().getBlockX());
                    mapView.setCenterZ(player.getLocation().getBlockZ());
                    mapView.setScale(MapView.Scale.NORMAL);
                })
                .run();*/
    }
}
