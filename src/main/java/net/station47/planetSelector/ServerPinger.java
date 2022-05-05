package net.station47.planetSelector;

import java.io.IOException;
import java.net.Socket;

public class ServerPinger {

    public static void throwExceptionIfUnreachable(ServerInfo info) throws IOException {
        Socket socket = new Socket(info.getIp(),info.getPort());
        socket.getOutputStream().write((byte) '\n');
        socket.close();
    }
}
