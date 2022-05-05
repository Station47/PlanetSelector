package net.station47.planetSelector;

import org.bukkit.Material;

import java.util.List;

public class ServerInfo {

    private String serverName, displayName, ip;
    List<String> description;
    private int playerCount = 0, port;
    Material icon;
    boolean online = false;

    public ServerInfo(String serverName, String displayName, List<String> description, Material icon) {
        this.serverName = serverName;
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;

    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getServerName() {
        return serverName;
    }

    public List<String> getDescription(){
        return description;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        online = playerCount>=0;
        this.playerCount = playerCount;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort(){return port;}

    public void setIpAndPort(String ip) {
        this.ip = ip;
    }

    public void setIpAndPort(String ip, int port){
        this.ip = ip;
        this.port = port;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Material getIcon() {
        return icon;
    }

    public void setIcon(Material icon) {
        this.icon = icon;
    }
}
