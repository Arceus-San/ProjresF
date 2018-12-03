/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetreseau;


import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author mgresse
 */
public class ResolutionDeNom {
    
    public static void main (String[] args){
        InetAddress address;
        try{
            address = InetAddress.getLocalHost();
            System.out.println(address.getHostName()+" : "+address.getHostAddress());
        }
        catch(UnknownHostException e){
            e.printStackTrace();
        }
    
    }
    
    
}
