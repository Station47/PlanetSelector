package net.station47.planetSelector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ServerConnector {
    private Plugin plugin;

    public ServerConnector(Plugin plugin){
        this.plugin = plugin;
    }

    public void sendPlayerToServer(Player player, String serverName){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(serverName);
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

}
