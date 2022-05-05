package net.station47.planetSelector;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.station47.stationAPI.api.commands.MainCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbyCommand extends MainCommand {
    private Plugin plugin;
    private String lobbyServerName;
    public LobbyCommand(JavaPlugin plugin, String lobbyName) {
        super("lobby", plugin);
        this.plugin = plugin;
        lobbyServerName = lobbyName;
    }

    @Override
    protected boolean noArgs(CommandSender sender) {
        if (sender instanceof Player) {
            PlanetSelector.serverConnector.sendPlayerToServer((Player) sender, lobbyServerName);
        }
        return true;
    }
}
