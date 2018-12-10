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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import static projetreseau.CleAES.readKey;


public class ClientTCP {

   public static void main(String[] args) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException {
        // TODO Auto-generated method stub
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
                            System.out.println("result: "+new String(result));
                            out.println(data);
                            System.out.println("echo : "+msg);
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
                        if(msg.equals("bye")){
                            break;
                        }
                        cipher.init(Cipher.DECRYPT_MODE, skey);
                        byte[] original = cipher.doFinal(msg.getBytes());
                        System.out.println("Decrypted data: "+new String(original));
                        System.out.println("Serveur : "+msg);
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

        } catch (UnknownHostException e){
                System.err.println("Don't know about host: 10.163.6.2.8 ");
                System.exit(1);
        }
        catch(IOException e){
                System.err.println("Couldn't get I/O for the connection to : 10.163.6.2 ");
                System.exit(1);
        }
}
}
