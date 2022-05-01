package Client_Server_Logic;

import Business_Logic.KeyCreator;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

public class Server {

    public static final int PORT = 9191;
    public static LinkedList<ServerThread> serverList = new LinkedList<>();
    private static BigInteger[] openKey;
    private static BigInteger[] secretKey;
    private static BigInteger p;
    private static BigInteger q;

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server started");
            KeyCreator keyCreator = new KeyCreator();
            keyCreator.generateKeys(512);
            openKey = keyCreator.getOpenKey();
            secretKey = keyCreator.getSecretKey();
            p = keyCreator.getP();
            q = keyCreator.getQ();
            System.out.println("//Server keys generated//");
            System.out.println("Waiting for new connections");
            while (true) {
                Socket socket = server.accept();
                System.out.println("Connection made. The address: " + socket.getInetAddress());
                System.out.println("Waiting for new connections...");
                try {
                    serverList.add(new ServerThread(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            System.out.println("The server has shut down.");
        }
    }

    public static BigInteger[] getOpenKey() {
        return openKey;
    }

    public static BigInteger[] getSecretKey() {
        return secretKey;
    }

    public static BigInteger getP() {
        return p;
    }

    public static BigInteger getQ() {
        return q;
    }
}