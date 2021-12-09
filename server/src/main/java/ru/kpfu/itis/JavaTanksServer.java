package ru.kpfu.itis;

import ru.kpfu.itis.server.GameServer;
import ru.kpfu.itis.server.exceptions.GameServerException;
import ru.kpfu.itis.server.listeners.EnterWithNameListener;

public class JavaTanksServer extends GameServer {
    private static int PORT = 11903;
    private static JavaTanksServer gameServer;

    public static JavaTanksServer getInstance() {
        if (gameServer == null) {
            gameServer = new JavaTanksServer(PORT);
        }
        return gameServer;
    }

    public static void main(String[] args) {
        gameServer = JavaTanksServer.getInstance();
        try {
            gameServer.registerListener(new EnterWithNameListener());
            gameServer.start();
        } catch (GameServerException e) {
            throw new IllegalStateException(e);
        }
    }

    public JavaTanksServer(int port) {
        super(port);
    }
}
