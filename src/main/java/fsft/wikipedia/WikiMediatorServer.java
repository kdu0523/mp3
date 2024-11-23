package fsft.wikipedia;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class WikiMediatorServer {
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean isRunning;

    /**
     * Start a server at a given port number, with the ability to process
     * upto n requests concurrently.
     *
     * @param port the port number to bind the server to
     * @param n the number of concurrent requests the server can handle
     */
    public WikiMediatorServer(int port, int n) throws IOException {
        serverSocket = new ServerSocket(port);
        executorService = Executors.newFixedThreadPool(n);
        isRunning = true;
        
        // Start accepting client connections
        new Thread(() -> acceptConnections()).start();
    }

    private void acceptConnections() {
        while (isRunning) {
            try {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(() -> handleClient(clientSocket));
            } catch (IOException e) {
                if (isRunning) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        // Client handling logic will be implemented here
    }

    public String addUser(String userName) {
        return "User " + userName + " added successfully";
    }

    public void shutdown() {
        isRunning = false;
        try {
            serverSocket.close();
            executorService.shutdown();
            executorService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }
}
