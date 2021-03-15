package org.academiadecodigo.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {

    public static final String DOCUMENT_ROOT = "www/";
    public static final int DEFAULT_PORT = 8989;


    public static void main(String[] args) {

        try {

            int port = args.length > 0 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

            WebServer webServer = new WebServer();
            webServer.listen(port);

        } catch (NumberFormatException e) {

            System.err.println("Usage: WebServer [PORT]");
            System.exit(1);

        }
    }

    private void listen(int port) {

        try {

            ServerSocket bindSocket = new ServerSocket(port);

            serve(bindSocket);

        } catch (IOException e) {

            System.exit(1);

        }
    }

    private void serve(ServerSocket bindSocket) {

        ExecutorService cachedPool = Executors.newCachedThreadPool();

        while (true) {

            try {

                Socket clientSocket = bindSocket.accept();

                System.out.println("Accepted new connection.");

                cachedPool.submit(new ClientDispatcher(clientSocket));

                /*ManyConnections connections = new ManyConnections();
                connections.start();*/


            } catch (IOException e) {

                e.printStackTrace();

            }
        }

        //cachedPool.shutdown();
    }
}
