package com.prismflux.schrottimon.net;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;

public class SocketConnection {

    private static Socket socket = null;

    //private final String USERNAME = "user";
    //private final String PASSWORD = "pw";

    private final String SERVER_HOST = "http://localhost:9000";

    public static Socket getSocket() {
        return socket;
    }

    public static String getSocketId() {
        return getSocket().id() != null ? getSocket().id() : "";
    }

    public SocketConnection() {
        //this.g = g;

        IO.Options options = IO.Options.builder()
                .build();

        //Properties props = new Properties();
        //props.setProperty("connect.sid", AuthenticationClient.getCookieString());

        if (socket == null) {
            socket = IO.socket(URI.create(SERVER_HOST), options);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected: " + socket.connected() + ", Socket ID: " + socket.id()); // true
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected: " + socket.connected()); // false
                }
            });
        }
    }

    protected void registerSocketListener(String tag, Emitter.Listener listener) {
        socket.on(tag, listener);
    }

    protected void unregisterSocketListener(String tag, Emitter.Listener fn) {
        socket.off(tag, fn);
    }
}
