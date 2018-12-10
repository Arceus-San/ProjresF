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
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import javax.crypto.NoSuchPaddingException;

public class ClientTCP {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException {

        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez rentrer l'IP (127.0.0.1) : ");
        String IP = sc.nextLine();
        System.out.println("Vous avez rentré votre adresse IP");
        System.out.println();
        System.out.println("Veuillez rentrer le port (4444) : ");
        int port = Integer.parseInt(sc.nextLine());
        System.out.println("Vous avez rentré votre port");
        System.out.println("Vous pouvez commencer à chatter");

        Socket echoSocket;
        BufferedReader in;
        PrintWriter out;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
            echoSocket = new Socket(IP, port);
            out = new PrintWriter(echoSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

            Thread envoyer = new Thread(new Runnable() {
                String msg;

                @Override
                public void run() {
                    try {
                        while (true) {
                            msg = stdIn.readLine();
                            out.println(msg);
                            System.out.println("Client : " + msg);
                            out.flush();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            envoyer.start();

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
                            System.out.println("Serveur :" + msg);
                            msg = in.readLine();
                        }
                        System.out.println("Serveur déconnecté");
                        out.close();
                        echoSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            recevoir.start();

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + IP);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to  : " + IP);
            System.exit(1);
        }
    }
}