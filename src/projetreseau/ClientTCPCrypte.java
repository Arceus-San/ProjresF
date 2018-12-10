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


public class ClientTCPCrypte {

   public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        // TODO Auto-generated method stub
        
        Scanner sc = new Scanner(System.in);
        System.out.println("Veuillez rentrer l'IP (127.0.0.1) : ");
        String IP = sc.nextLine();
        System.out.println("Vous avez rentré votre adresse IP");
        System.out.println();
        System.out.println("Veuillez rentrer le port (4444) : ");
        int port = Integer.parseInt(sc.nextLine());
        System.out.println("Vous avez rentré votre port");
        System.out.println("Vous pouvez commencer à chatter");
        
        SecretKey skey = readKey();
        Cipher cipher = Cipher.getInstance("AES");
        Socket echoSocket;
        BufferedReader in;
        PrintWriter out;
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        try {

            echoSocket = new Socket("127.0.0.1",5000);
            out = new PrintWriter(echoSocket.getOutputStream());
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

            Thread envoyer = new Thread(new Runnable() {
                String msg;
                @Override
                public void run() {
                    try {
                        while(true){
                            msg = stdIn.readLine();
                            cipher.init(Cipher.ENCRYPT_MODE, skey);
                            byte[] data = msg.getBytes();
                            byte[] result = cipher.doFinal(data);
                            out.println(DatatypeConverter.printBase64Binary(result));
                            System.out.println("Client : "+msg+" -> "+DatatypeConverter.printBase64Binary(result));
                            out.flush();
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    } catch (Exception e){
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
                     while(msg!=null){
                        cipher.init(Cipher.DECRYPT_MODE, skey);
                        byte[] result  = DatatypeConverter.parseBase64Binary(msg);
                        byte[] original = cipher.doFinal(result);
                        System.out.println("Serveur : "+msg+" -> "+new String(original));
                        if(new String(original).equals("bye")){
                            break;
                        }
                        msg = in.readLine();
                     }
                     System.out.println("Serveur déconnecté");
                     out.close();
                     echoSocket.close();
                   } catch(IOException e){
                        e.printStackTrace();
                    } catch (Exception e){
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
