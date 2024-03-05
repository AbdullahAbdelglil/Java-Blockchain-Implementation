package com.example.ISOFTBlockchain.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
public class Server {
    private final ServerSocket serverSocket;
    public static List<Client> clients = new ArrayList<>();
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }


    public void startServer() {
        try {
            System.out.println("Server Started");
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept();
                System.out.println("New Client has been entered");
            }
        } catch (IOException e) {
            closeServer();
        }
    }

    public static List<Client> getClients() {
        return clients;
    }
    public void closeServer() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addClient(Client client) {
        clients.add(client);
    }

    public static void main(String[] args) throws IOException{
        ServerSocket serverSocket = new ServerSocket(2024);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
