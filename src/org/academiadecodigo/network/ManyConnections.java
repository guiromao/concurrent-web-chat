package org.academiadecodigo.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ManyConnections {

    public void start() throws IOException {
        List<Socket> connections = new ArrayList<>();

        for(int i = 0; i <= 100000; i++){
            InetAddress inetAddress = InetAddress.getByName("localhost");
            Socket socket = new Socket(inetAddress, 8989);
            connections.add(socket);
            System.out.println("Creating socket");
        }
    }

}
