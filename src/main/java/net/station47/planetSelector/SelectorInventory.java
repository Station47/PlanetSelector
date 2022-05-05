package net.station47.planetSelector;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.station47.inventoryGuiApi.InventoryBuilder;
import fr.station47.inventoryGuiApi.inventoryAction.InventoryItem;
import fr.station47.stationAPI.api.Utils;
import fr.station47.stationAPI.api.commands.MainCommand;
import fr.station47.stationAPI.api.config.ConfigObject;
import fr.station47.stationAPI.api.gui.GUI;
import fr.station47.stationAPI.api.runnableChain.RunnableChain;
import fr.station47.theme.Theme;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.DataInput;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class SelectorInventory extends MainCommand implements GUI, PluginMessageListener{
    private ConfigObject config;
    private int serverStartIndex = 0;
    private Vector<ServerInfo> serverList;
    private String currentServerName;
    private InventoryBuilder builder;
    private Inventory inventory;
    private Plugin plugin;

    public SelectorInventory(JavaPlugin plugin){
        super("server", plugin);
        this.plugin = plugin;
        config = new ConfigObject();
        config.put("selectorName","Teleporteur");
        config.put("playerCount","Il y a {0} joueur(s) connecté(s)");
        config.put("alreadyConnected","Vous êtes sur ce serveur");
        config.put("offline","Ce serveur est hors-ligne");

        PlanetSelector.configs.loadOrDefault("config",config);
        serverList = new Vector<>();
        YamlConfiguration yamlConfig = PlanetSelector.configs.getConfig("config");
        if (yamlConfig.getConfigurationSection("servers")==null){
            yamlConfig.createSection("servers");
        }

        PlanetSelector.configs.save("config");
        Set<String> servers = yamlConfig.getConfigurationSection("servers").getKeys(false);
        ConfigObject serverConfig = new ConfigObject();
        serverConfig.put("description", new ArrayList<String>());
        serverConfig.put("item","DIRT");
        serverConfig.put("displayName","Exemple");
        for (String serverName: servers){
            ConfigurationSection section = yamlConfig.getConfigurationSection("servers").getConfigurationSection(serverName);
            serverConfig.loadFrom(section);
            List<String>description = serverConfig.getStringList("description");
            description.add(config.getString("offline"));
            serverList.add(new ServerInfo(serverName,serverConfig.getString("displayName"), description, Material.valueOf(serverConfig.getString("item"))));
        }
        builder = new InventoryBuilder(9,config.getString("selectorName"), plugin);
        builder.unregisterListenerOnInvclose(false).listenTo(true);

        for (int i = serverStartIndex; i<serverList.size(); i++) {
            final ServerInfo info = serverList.get(i);
            builder.setOnAction(i,event -> {
                event.setCancelled(true);
                if (info.getServerName().equals(currentServerName)){
                    Theme.sendMessage(event.getWhoClicked(), config.getString("alreadyConnected"));
                } else if (info.isOnline()){
                    Theme.sendMessage(event.getWhoClicked(), "Connexion");
                    PlanetSelector.serverConnector
                            .sendPlayerToServer((Player) event.getWhoClicked(),info.getServerName());
                } else {
                    Theme.sendMessage(event.getWhoClicked(), ChatColor.RED+config.getString("offline"));
                }
            });

        }
        inventory = builder.build();
        scheduleFetchUpdate();
    }

    public void scheduleFetchUpdate(){
        BukkitRunnable updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                fetchUpdate();
            }
        };
        updateTask.runTaskTimerAsynchronously(plugin,40,40);
    }

    private void fetchUpdate(){
        if (!serverIsEmpty()) {
            Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
            if (player != null){
                askCurrentServer(player);
                for (ServerInfo serverInfo : serverList) {
                    askServerPlayerCountFromPlayer(serverInfo, player);
                    askServerIPFromPlayer(serverInfo, player);
                }
            }
            scheduleNextInventoryUpdate();
        }
    }
    public boolean serverIsEmpty(){
        return Bukkit.getOnlinePlayers().size() == 0;
    }
    public void askServerPlayerCountFromPlayer(ServerInfo server, Player player){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("PlayerCount");
        out.writeUTF(server.getServerName());
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
    public void askServerIPFromPlayer(ServerInfo server, Player player){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("ServerIP");
        out.writeUTF(server.getServerName());
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }
    public void askCurrentServer(Player player){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("GetServer");
        player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
    }

    public void scheduleNextInventoryUpdate(){
        BukkitRunnable update = new BukkitRunnable() {
            @Override
            public void run() {
                updateInventory();
            }
        };
        update.runTaskLater(plugin, 10);
    }

    private void updateInventory(){
        for (int i = serverStartIndex; i < serverList.size(); i++) {
            ServerInfo info = serverList.get(i);
            InventoryItem item = new InventoryItem(i,info.getDisplayName(), info.isOnline()?info.getIcon():Material.BARRIER);
            List<String> description = info.getDescription();
            if (info.getServerName().equals(currentServerName)){
                description.set(description.size()-1, ChatColor.WHITE+config.getString("alreadyConnected"));
            } else if (info.isOnline()){
                description.set(description.size()-1, ChatColor.WHITE+Utils.fill(config.getString("playerCount"),String.valueOf(info.getPlayerCount())));
            } else {
                description.set(description.size()-1,ChatColor.RED+config.getString("offline"));
            }
            item.setLore(description);
            inventory.setItem(item.getSlot(),item.getItemStack());
        }
    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public boolean noArgs(CommandSender sender){
        if (sender instanceof Player){
            open(((Player) sender));
        }
        return true;
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;
        DataInput in = ByteStreams.newDataInput(message);
        String command = "";
        try {
            command = in.readUTF();
            switch (command) {
                case "PlayerCount": {
                    final String serverName = in.readUTF();
                    final ServerInfo info = getServerInfo(serverName);
                    final int playerCount = in.readInt();
                    if (playerCount == 0) {
                        RunnableChain runnable = new RunnableChain(plugin);
                        runnable.then(() -> ServerPinger.throwExceptionIfUnreachable(info))
                                .then(() -> info.setPlayerCount(0))
                                .catchException(() -> info.setPlayerCount(-1));
                        runnable.startAsync();
                    } else {
                        info.setPlayerCount(playerCount);
                    }
                    break;
                }
                case "ServerIP": {
                    final String serverName = in.readUTF();
                    final String ip = in.readUTF();
                    final int port = in.readUnsignedShort();
                    getServerInfo(serverName).setIpAndPort(ip, port);
                    break;
                }
                case "GetServer":
                    currentServerName = in.readUTF();
                    break;
            }
        } catch (Exception ex){
            Bukkit.getLogger().severe("Pluginchannel exception with command "+ command);
        }
    }

    private ServerInfo getServerInfo(String serverName){
        return serverList.stream().filter(s->s.getServerName().equals(serverName)).findFirst().get();
    }
}
