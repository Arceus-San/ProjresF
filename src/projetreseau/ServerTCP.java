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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import static projetreseau.CleAES.readKey;


public class ServerTCP {
 
    public static void main(String[] test) throws NoSuchAlgorithmException, NoSuchPaddingException{
        // TODO Auto-generated method stub
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
                            System.out.println("data: "+new String(data));
                            byte[] result = cipher.doFinal(data);
                            System.out.println("result: "+new String(result));
                            out.println(result);
                            System.out.println("echo :"+msg);
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
                        byte[] original = cipher.doFinal(msg.getBytes());
                        System.out.println("Decrypted data: "+new String(original));
                        System.out.println("Client : "+msg);
                        msg = in.readLine();
                    }

                    System.out.println("Client déconnecté");

                    out.close();
                    clientSocket.close();
                    serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException ex) {
                        Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalBlockSizeException ex) {
                        Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (BadPaddingException ex) {
                        Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
                    }
             }
            });
            recevoir.start();
            
            }catch (IOException e) {
                System.out.println("Could not listen on port 4444");
                System.out.println(-1);
            }
    }
}
