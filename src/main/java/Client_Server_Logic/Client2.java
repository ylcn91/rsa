package Client_Server_Logic;

import Business_Logic.Decryptor;
import Business_Logic.Encryptor;
import Business_Logic.KeyCreator;

import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Client2 {
    private static Socket clientSocket;
    private static final Scanner scanner = new Scanner(System.in);
    private static BufferedReader in;
    private static BufferedWriter out;

    private static BigInteger[] openKey;
    private static BigInteger[] serverOpenKey = new BigInteger[2];
    private static BigInteger[] secretKey;
    private static BigInteger p;
    private static BigInteger q;

    private static ArrayList<BigInteger> blocks = new ArrayList<>();
    private static ArrayList<BigInteger> inputBlocks = new ArrayList<>();
    private static int countOfInputBlocks = 0;

    public Client2() {
        try {
            KeyCreator keyCreator = new KeyCreator();
            System.out.println("Enter the number of bits for numbers q and p");
            keyCreator.generateKeys(scanner.nextInt());
            openKey = keyCreator.getOpenKey();
            secretKey = keyCreator.getSecretKey();
            p = keyCreator.getP();
            q = keyCreator.getQ();
            System.out.println("//Client keys generated//");

            clientSocket = new Socket("localhost", 8080);

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            WriteMsg writeMsg = new WriteMsg();
            writeMsg.start();
            ReadMsg readMsg = new ReadMsg();
            readMsg.setDaemon(true);
            readMsg.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BigInteger stringToBigInt(String message){
        if (message.equals("")) message = "...Empty Message...";
        char[] symbols = message.toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < symbols.length; i++) {
            int intSymbol = symbols[i];
            String stringSymbol = Integer.toString(intSymbol);
            for (int j = 0; j < 4-stringSymbol.length(); j++) {
                result.append("0");
            }
            result.append(stringSymbol);
        }
        return new BigInteger(String.valueOf(result));
    }
    private static void splitByBLocks(BigInteger message, BigInteger n){
        blocks.clear();
        String msg = message.toString();
        int ind = msg.length()-1;
        BigInteger subNumber = new BigInteger(msg.substring(ind));

        while (msg.length()>0) {
            String sub = "";
            while (subNumber.compareTo(n.subtract(BigInteger.ONE)) < 0) {
                ind--;
                if (ind<0) break;
                sub = msg.substring(ind);
                subNumber = new BigInteger(sub);
            }

            int counter = 0;
            for (int i = 0; i < sub.length(); i++) {
                if (sub.charAt(i)=='0') counter++;
                else break;
            }
            ind += counter;

            subNumber = new BigInteger(msg.substring(ind+1));
            blocks.add(subNumber);
            msg = msg.substring(0, ind+1);
        }
    }

    private static String blocksToString(ArrayList<BigInteger> inputBlocks){
        StringBuilder result = new StringBuilder();
        for (int i = inputBlocks.size()-1; i >= 0; i--) {
            result.append(inputBlocks.get(i));
        }
        char[] message = new char[result.length()];
        int position = message.length-1;
        while (result.length()>0){
            int newEnd = result.length()-4;
            if (newEnd<0) newEnd = 0;
            String sub = result.substring(newEnd);
            char symbol = (char) Integer.parseInt(sub);
            message[position] = symbol;
            position--;
            result = new StringBuilder(result.substring(0, newEnd));
        }
        return String.valueOf(message);
    }

    public static void main(String[] args) {
        new Client2();

    }

    private static class ReadMsg extends Thread {
        @Override
        public void run() {
            try {
                serverOpenKey[0] = new BigInteger(in.readLine());
                serverOpenKey[1] = new BigInteger(in.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("//Server public key received//");

            String str;
            try {
                int counter = 0;
                while (true) {
                    str = in.readLine();
                    str = Decryptor.decrypt(new BigInteger(str),secretKey,q,p).toString();
                    if (countOfInputBlocks == 0) countOfInputBlocks = Integer.parseInt(str);
                    else {
                        inputBlocks.add(new BigInteger(str));
                        counter++;
                        if (counter == countOfInputBlocks){
                            counter = 0;
                            countOfInputBlocks = 0;
                            System.out.println(blocksToString(inputBlocks));
                            inputBlocks.clear();
                        }
                    }

                    if (str.equals("stop")) {

                        break;
                    }
                }
            } catch (IOException e) {

            }
        }
    }

    public static class WriteMsg extends Thread {
        @Override
        public void run() {
            try {
                out.write(openKey[0].toString() + "\n");
                out.flush();
                out.write(openKey[1].toString() + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                String userWord;
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    userWord = consoleReader.readLine();
                    if (userWord.equals("/stop")) {
                        out.write(userWord + "\n");
                        out.flush();
                        break;
                    } else {
                        BigInteger message = stringToBigInt(userWord);
                        splitByBLocks(message,p.multiply(q));
                        out.write(Encryptor.encrypt(BigInteger.valueOf(blocks.size()),serverOpenKey) + "\n");
                        out.flush();
                        for (int i = 0; i < blocks.size(); i++) {
                            BigInteger msg = Encryptor.encrypt(blocks.get(i),serverOpenKey);
                            out.write(msg.toString() + "\n");
                            out.flush();
                        }
                    }
                    out.flush();
                } catch (IOException e) {

                }

            }
        }
    }
}

