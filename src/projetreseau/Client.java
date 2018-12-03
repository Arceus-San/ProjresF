/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projetreseau;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.System.in;
import static java.lang.System.out;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author mgresse
 */
public class Client {
    
    public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Socket echoSocket=null;
		PrintWriter out=null;
		BufferedReader in=null;
		try {
			echoSocket = new Socket("10.163.6.2",4444);
			out = new PrintWriter(echoSocket.getOutputStream(), true);
			in = new BufferedReader( new InputStreamReader(echoSocket.getInputStream()));
                        
                        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
                        String userInput;
		
                        while((userInput = stdIn.readLine()) != null){
                            out.println(userInput);
                            System.out.println("echo :"+in.readLine());
                        }
		
		out.close();
		in.close();
		stdIn.close();
		echoSocket.close();
		}
		catch (UnknownHostException e){
			System.err.println("Don't know about host: 10.163.6.2.8 ");
			System.exit(1);
		}
		catch(IOException e){
			System.err.println("Couldn't get I/O for the connection to : 10.163.6.2 ");
			System.exit(1);
		}
		
	}
    
    
}
