package com.prismflux.canvastest.net;

import com.prismflux.canvastest.Game;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;
import java.util.HashMap;

public class SocketConnection implements Emitter.Listener {

    private static Socket socket = null;

    private final String USERNAME = "user";
    private final String PASSWORD = "pw";

    private Game g;

    public static Socket getSocket() {
        return socket;
    }

    public SocketConnection(Game g) {
        this.g = g;

        IO.Options options = IO.Options.builder().build();

        socket = IO.socket(URI.create("http://localhost:4000"), options);
        socket.connect();

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Connected: " + socket.connected() + ", Socket ID: " + socket.id()); // true

                socket.emit("clientjoin", USERNAME, PASSWORD);
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Connected: " + socket.connected()); // false
            }
        });

        socket.on("update", this);
    }

    @Override
    public void call(Object... objects) {
        System.out.println(objects[0]);
    }
}
