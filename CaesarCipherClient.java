/* CaesarCipherClient.java defines a client that allows a user to communicate
 * with a server that encodes text using a Caesar Cipher according to a shift
 * value specified by the user.
 *
 * By: Nathaniel Bender
 * Written for CS232
 * 5/12/16
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class CaesarCipherClient {
    public static void main(String[] args) throws IOException {

    	//Check the length of the arg array, and return an error if its wrong
        if (args.length != 2) {
            System.err.println(
                "Usage: java CaesarCipherClient <host name> <port number>");
            System.exit(1);
        }

        //Parse the hostname and port from the args
        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        //Attempt a connection to the server using the given information
        try (
            Socket ccSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(ccSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(ccSocket.getInputStream()));
        )
        //If successful connection, do the following
        {
        	//Create the buffered reader and strings, as well as scanner to get cipher shift
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            Scanner scanner = new Scanner(System.in);
            String fromServer;
            String fromUser;
            System.out.print("Please enter a cipher shift value: ");
            int shift = scanner.nextInt();
			if ((shift < 1) || (shift > 25)){
				System.out.println("Shift needs to be between 0 and 26!\n");
				ccSocket.close();
	            System.exit(-1);
			}
            System.out.println("Connection to Server successful");
            //After user has specified the shift, report it to the server
            out.println(shift);

            //As long as there is input from the user, send that input to the server,
            // and print both what we have sent, and what the server returns.
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("exit"))
                    break;

                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
            }
        }
        //Otherwise, catch the errors and report them
        catch (UnknownHostException e) {
            System.err.println("Error connecting to host: " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
}
