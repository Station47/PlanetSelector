package net.station47.planetSelector;

import fr.station47.stationAPI.api.StationAPI;
import fr.station47.stationAPI.api.config.ConfigHelper;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

public class PlanetSelector extends JavaPlugin {

    public static ConfigHelper configs;
    public static ServerConnector serverConnector;

    @Override
    public void onEnable() {
        configs = new ConfigHelper(this);
        serverConnector = new ServerConnector(this);
        LobbyCommand lobbyCommand = new LobbyCommand(this,"lobby");
        SelectorInventory selectorInventory = new SelectorInventory(this);
        if (StationAPI.isCustomItemHandlerActive()) {
            TeleporterItem tpItem = new TeleporterItem(selectorInventory);
        }
        this.getServer().getMessenger().registerIncomingPluginChannel(this,"BungeeCord",selectorInventory);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this,"BungeeCord");
    }
}
