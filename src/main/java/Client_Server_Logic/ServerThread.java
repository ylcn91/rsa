package Client_Server_Logic;

import Business_Logic.Decryptor;
import Business_Logic.Encryptor;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;

class ServerThread extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;


    private static final BigInteger[] serverOpenKey = Server.getOpenKey();
    private BigInteger[] clientOpenKey = new BigInteger[2];
    private static final BigInteger[] serverSecretKey = Server.getSecretKey();
    private static BigInteger p = Server.getP();
    private static BigInteger q = Server.getQ();

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        start();
    }
    @Override
    public void run() {
        String word;
        try {
            clientOpenKey[0] = new BigInteger(in.readLine());
            clientOpenKey[1] = new BigInteger(in.readLine());
            System.out.println("//Client's public key received//");

            out.write((serverOpenKey[0]) + "\n");
            out.flush();
            out.write(serverOpenKey[1] + "\n");
            out.flush();
            System.out.println("//Server public key sent//");
            while (true) {
                word = in.readLine();
                System.out.println("Message received: " + word);
                if(word.equals("/stop")) {
                    System.out.println(" The client is disabled. The address: " + socket.getInetAddress());
                    break;                }
                for (ServerThread vr : Server.serverList) {
                    if (vr.equals(this)) continue;
                    vr.send(word);
                }
            }

        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void send(String msg) {
        try {
            BigInteger newMsg = Decryptor.decrypt(new BigInteger(msg),serverSecretKey,q,p);
            msg = Encryptor.encrypt(newMsg,clientOpenKey).toString();
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }
}