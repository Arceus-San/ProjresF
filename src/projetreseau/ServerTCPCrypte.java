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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;
import static projetreseau.CleAES.readKey;


public class ServerTCPCrypte {
 
    public static void main(String[] test) throws NoSuchAlgorithmException, NoSuchPaddingException{
        // TODO Auto-generated method stub
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez rentrer le port (de préférence 4444) : ");
        int port = Integer.parseInt(sc.nextLine());
        System.out.println("Vous avez rentré votre port, veuillez lancer le client.");
        
        ServerSocket serverSocket  ;
        Socket clientSocket ;
        BufferedReader in;
        PrintWriter out;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {
            SecretKey skey = readKey();
            Cipher cipher = Cipher.getInstance("AES");
            serverSocket = new ServerSocket(5000);
            clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream());
            in = new BufferedReader (new InputStreamReader (clientSocket.getInputStream()));

            Thread envoi= new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    try{
                        while(true){
                            msg = stdIn.readLine();
                            cipher.init(Cipher.ENCRYPT_MODE, skey);
                            byte[] data = msg.getBytes();
                            byte[] result = cipher.doFinal(data);
                            out.println(DatatypeConverter.printBase64Binary(result));
                            System.out.println("Serveur : "+msg+" -> "+DatatypeConverter.printBase64Binary(result));
                            out.flush();
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            envoi.start();

            Thread recevoir= new Thread(new Runnable() {
                String msg ;
                @Override
                public void run() {
                    try {
                        msg = in.readLine();

                    while(msg!=null){
                        if(msg.equals("bye")){
                            break;
                        }
                        
                        cipher.init(Cipher.DECRYPT_MODE, skey);
                        byte[] result  = DatatypeConverter.parseBase64Binary(msg);
                        byte[] original = cipher.doFinal(result);
                        System.out.println("Client : "+msg+" -> "+new String(original));
                        if(new String(original).equals("bye")){
                            break;
                        }
                        msg = in.readLine();
                    }

                    System.out.println("Client déconnecté");

                    out.close();
                    clientSocket.close();
                    serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(ServerTCPCrypte.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalBlockSizeException ex) {
                        Logger.getLogger(ServerTCPCrypte.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadPaddingException ex) {
                        Logger.getLogger(ServerTCPCrypte.class.getName()).log(Level.SEVERE, null, ex);
                    }
             }
            });
            recevoir.start();
            
            }catch (IOException e) {
                System.out.println("Could not listen on port" + port);
                System.out.println(-1);
            }
    }
}
