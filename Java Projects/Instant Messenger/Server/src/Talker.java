import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
//======================================================================================================================================================
//======================================================================================================================================================
public class Talker {
/* COMMUNICATION BETWEEN CTC AND CTS
 * Used by both Server and Client
 * Sends all messages and acts as the furthest point of contact
 * Set up socket based on constructor called
 * Initialize BufferedReader and DataOutputStream for communication
 * sends all messages, ensuring they are trimmed and end with a new line
 */
	
	Socket socket;
	private BufferedReader br;
	private DataOutputStream dos;
	String message;
	
	Talker(String domain, int port) throws IOException{
		
		socket= new Socket(domain, port);
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		dos = new DataOutputStream(socket.getOutputStream());
	}

	Talker(Socket socket) throws IOException{
		
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		dos = new DataOutputStream(socket.getOutputStream());
	}
//======================================================================================================================================================
	public void send(String message) throws IOException{
		//Robust code to trim the message and ensure it ends in \n
		
		this.message= message;
		message.trim();
		
		if(!message.endsWith("\n"))
			message = message + "\n";
		
		dos.writeBytes(message);
		
		//System.out.println(message);
	}
//======================================================================================================================================================
	public String recieve() throws IOException{
		
		message = br.readLine();
		
		//System.out.println(message);
		
		return message;
	}
//======================================================================================================================================================
//======================================================================================================================================================
}
