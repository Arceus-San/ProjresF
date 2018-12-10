/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetreseau;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.crypto.*;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author Maxime
 */
public class CleAES {
    
   
    public static void writeKey(Key key) throws NoSuchAlgorithmException, FileNotFoundException, IOException{
        File f = new File("cle");
        FileOutputStream out = new FileOutputStream(f);
        out.write(key.getEncoded());
    }
        
    public static SecretKey readKey() throws FileNotFoundException, IOException{
        File f = new File("cle");
        DataInputStream in = new DataInputStream(new FileInputStream(f));
        byte[] rawkey = new byte[(int)f.length()];
        in.readFully(rawkey);
        in.close();

        SecretKey skey = new SecretKeySpec(rawkey, "AES");     
        return skey;
        
    }
       
    public static void main(String[] args){

        try {
            KeyGenerator  kg = KeyGenerator.getInstance("AES");
            kg.init(128);
            SecretKey key = kg.generateKey();
            writeKey(key);
            
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] data = "Hello World!".getBytes();
            System.out.println("data: "+new String(data));
            String b = (new String(data));
            System.out.println(b.getClass());
            byte[] result = cipher.doFinal(data);
            System.out.println("result: "+result);
            
            SecretKey skey = readKey();
            
            cipher.init(Cipher.DECRYPT_MODE, skey);
            byte[] original = cipher.doFinal(result);
            System.out.println("Decrypted data: "+new String(original));
            
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
            
    
}
