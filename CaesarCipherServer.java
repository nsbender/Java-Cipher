/* CaesarCipherServer.java defines a server that allows a user to communicate
 * via a client that sends plaintext. This server returns text that has been
 * encoded using a Caesar Cipher and a shift value specified by the user at the client
 * end.
 *
 * By: Nathaniel Bender
 * Written for CS232
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class CaesarCipherServer {
	public static void main(String[] args) throws IOException {
		//Check the length of the arg array, and return an error if its wrong
		if (args.length != 1) {
			System.err.println("Usage: java CaesarCipherServer <port number>");
			System.exit(1);
		}
		//Parse the port from the args
		int portNumber = Integer.parseInt(args[0]);
		try(
				//Create a socket on the specified port. Done only once
				final  ServerSocket serverSocket = new ServerSocket(portNumber);
				)
				{
			while (true){
				//Only create a new thread when a new connection has been made
				final Socket clientSocket = serverSocket.accept();
				//Create a date object to keep track of connections and
				//Print that a new connection has been made to a client
				Date date = new Date();
				System.out.println(date.toString() + ": New Connection to Server");
				new Thread(){
					public void run(){
						//Create the server on the specified port
						try (
								PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
								BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
								)
								//If a successful connection has been made, do the following
								{
							String inputLine, outputLine;
							//Save the shift reported by the client locally
							int shift = 0;
							//If the shift comes back as 'null' that means the client quit because
							// the user typed in an illegal value for shift. Just catch this and move on.
							try{
								shift = Integer.parseInt(in.readLine());
								//Confirm with the client that we have recieved a shift value
								out.println(shift);
							}
							catch(NumberFormatException e){
							}


							//If text is received from our input, run the cipher on it and return it
							while ((inputLine = in.readLine()) != null) {
								outputLine = caesarCipher(inputLine, shift);
								out.println(outputLine);
								//If exit is received, exit the loop and kill the thread
								if (outputLine.equals("exit"))
									break;
							}
								}
						//If getting the client stream fails, report the error
						catch (IOException e) {
							System.out.println("Exception caught when trying to listen on port "
									+ portNumber + " or listening for a connection");
							System.out.println(e.getMessage());
						}
					}
				//Execute each thread
				}.start();
			}
				}
		//If opening a socket for the server to listen on fails, report the error
		catch (IOException e){
			System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}

	public static String caesarCipher(String input, int shift){
		String s = "";
		for (char ch: input.toCharArray()) {

			//Convert each char of the string into its ASCII decimal equivalent
			int current = (int) ch;

			//Lowercase letters that wont go past z with the shift
			if ((current >= 97) && (current+shift <= 122)){
				//Add the shift
				current += shift;
				//Append the shifted char to the new string after casting it back to char
				s += (char) current;
			}
			//Lowercase letters that will go past z with the shift
			else if ((current >= 97) && (current + shift > 122)){
				//Add the remaining shift after rotating back to the beginning of the alphabet
				current = 97 + ((current+shift)-123);
				s += (char) current;
			}
			//Uppercase letters that wont go past Z with the shift
			else if ((current >= 65) && (current+shift <= 90)){
				current += shift;
				s += (char) current;
			}
			//Uppercase letters that will go past Z with the shift
			else if ((current >= 65) && (current + shift > 90)){
				current = 65 + ((current+shift)-91);
				s += (char) current;
			}
			//Anything that isn't a letter is simply appended
			else{
				s+= (char) current;
			}
		}
		return s;
	}
}
