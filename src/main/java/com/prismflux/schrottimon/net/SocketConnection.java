package com.prismflux.schrottimon.net;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;
import java.util.ArrayList;

public class SocketConnection {

    private static Socket socket = null;

    //private final String USERNAME = "user";
    //private final String PASSWORD = "pw";

    private final String SERVER_HOST = "http://localhost:9000";

    public final ArrayList<SocketConnection> socketObjects = new ArrayList<>();

    public static Socket getSocket() {
        return socket;
    }

    public static String getSocketId() {
        return getSocket().id() != null ? getSocket().id() : "";
    }

    public SocketConnection() {
        //this.g = g;

        //Properties props = new Properties();
        //props.setProperty("connect.sid", AuthenticationClient.getCookieString());

        if (socket == null) {
            IO.Options options = IO.Options.builder()
                    .build();

            socket = IO.socket(URI.create(SERVER_HOST), options);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected: " + socket.connected() + ", Socket ID: " + socket.id()); // true
                    for (int i = 0; i < socketObjects.size(); i++) {
                        socketObjects.get(i).validate();
                    }
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected: " + socket.connected()); // false
                    System.out.println("Lost Connection");
                    for (int i = 0; i < socketObjects.size(); i++) {
                        socketObjects.get(i).invalidate();
                    }
                }
            });
        }

        socketObjects.add(this);
    }

    public void invalidate() {}

    public void validate() {}

    protected void registerSocketListener(String tag, Emitter.Listener listener) {
        socket.on(tag, listener);
    }

    protected void unregisterSocketListener(String tag, Emitter.Listener fn) {
        socket.off(tag, fn);
    }
}
