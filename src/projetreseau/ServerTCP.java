/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetreseau;

/**
 *
 * @author pedago
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import javax.crypto.NoSuchPaddingException;
import java.util.Scanner;

public class ServerTCP {

    public static void main(String[] test) throws NoSuchAlgorithmException, NoSuchPaddingException {

        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez rentrer le port (de préférence 4444) : ");
        int port = Integer.parseInt(sc.nextLine());
        System.out.println("Vous avez rentré votre port, veuillez lancer le client.");

        ServerSocket serverSocket;
        Socket clientSocket;
        BufferedReader in;
        PrintWriter out;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
            serverSocket = new ServerSocket(port);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Thread envoi = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    try {
                        while (true) {
                            msg = stdIn.readLine();
                            out.println(msg);
                            System.out.println("Serveur : " + msg);
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            envoi.start();

            Thread recevoir = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    try {
                        msg = in.readLine();

                        while (msg != null) {
                            if (msg.equals("bye")) {
                                break;
                            }
                            System.out.println("Client : " + msg);
                            msg = in.readLine();
                        }

                        System.out.println("Client déconnecté");

                        out.close();
                        clientSocket.close();
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            );
            recevoir.start();

        } catch (IOException e) {
            System.out.println("Could not listen on port" + port);
            System.out.println(-1);
        }
    }
}