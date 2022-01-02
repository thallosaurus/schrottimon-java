package com.prismflux.canvastest.net;

import com.prismflux.canvastest.Game;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;

public class SocketConnection {

    private static Socket socket = null;

    private final String USERNAME = "user";
    private final String PASSWORD = "pw";

    //private Game g;

    public static Socket getSocket() {
        return socket;
    }

    public SocketConnection() {
        //this.g = g;

        IO.Options options = IO.Options.builder().build();

        if (socket == null) {
            socket = IO.socket(URI.create("http://localhost:9000"), options);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected: " + socket.connected() + ", Socket ID: " + socket.id()); // true

                    //socket.emit("clientjoin", USERNAME, PASSWORD);
                }
            });

            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected: " + socket.connected()); // false
                }
            });
        }

        //socket.on("loadlevel", this);
    }

    protected void registerSocketListener(String tag, Emitter.Listener listener) {
        socket.on(tag, listener);
    }
}
