package net.station47.planetSelector;

import fr.station47.stationAPI.api.StationAPI;
import fr.station47.stationAPI.api.customItem.CustomItem;
import fr.station47.stationAPI.api.customItem.InteractAction;
import fr.station47.stationAPI.api.customItem.Interactable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;

public class TeleporterItem extends CustomItem implements Interactable {

    private InteractAction actions;
    private SelectorInventory selectorInventory;

    public TeleporterItem(SelectorInventory inventory) {
        super("teleporteur");
        StationAPI.customItemHandler.registerInteractable(this);
        selectorInventory = inventory;
        defineItems();
        defineActions();
    }

    public void defineItems(){
        item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("Téléporteur");
        meta.setUnbreakable(true);
        meta.setLore(Collections.singletonList("Clic droit pour ouvrir le menu de selection des planètes"));
        item.setItemMeta(meta);
    }

    public void defineActions(){
        actions = new InteractAction() {
            @Override
            public boolean leftClickAir(PlayerInteractEvent playerInteractEvent) {
                openTeleportMenu(playerInteractEvent);
                return false;
            }

            @Override
            public boolean leftClickBlock(PlayerInteractEvent playerInteractEvent) {
                openTeleportMenu(playerInteractEvent);
                return false;
            }

            @Override
            public boolean rightClickAir(PlayerInteractEvent playerInteractEvent) {
                openTeleportMenu(playerInteractEvent);
                return false;
            }

            @Override
            public boolean rightClickBlock(PlayerInteractEvent playerInteractEvent) {
                openTeleportMenu(playerInteractEvent);
                return false;
            }
        };
    }

    public void openTeleportMenu(PlayerInteractEvent event){
        selectorInventory.open(event.getPlayer());
    }

    @Override
    public boolean condition(ItemStack itemStack, Player player) {
        return itemStack.isSimilar(item);
    }

    @Override
    public InteractAction getAction() {
        return actions;
    }
}
