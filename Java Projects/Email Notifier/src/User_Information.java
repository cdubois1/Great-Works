import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

public class User_Information {
/* STORE USER INFOR
 * Contains the host, port, username and pwassword needed to access email
 * can load and save itself to file
 * saves the interval to check messages and if it should play sound or not
 */
	
	public
		String host;
		String port;
		String username;
		String password;
		int checkInterval;
		boolean playSound;
//=========================================================================================================//
	public void loadFromFile(File file) throws Exception{
		//loads itself from file
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		this.host = br.readLine();
		this.port = br.readLine();
		
		this.username = br.readLine();
		this.password = br.readLine();
		
		this.checkInterval = Integer.parseInt(br.readLine());
		this.playSound = Boolean.valueOf(br.readLine());
		
		br.close();
	}
//=========================================================================================================//	
	public void saveToFile(String host, String port, String username, String password, int checkInterval, boolean playSound, File file) throws Exception{
		//saves itself to the file
		
		this.host = host;
		this.port = port;
		
		this.username = username;
		this.password = password;
		
		this.checkInterval = checkInterval;
		this.playSound = playSound;
		
		PrintWriter pw = new PrintWriter(file);
		
		pw.println(host);
		pw.println(port);
		pw.println(username);
		pw.println(password);
		pw.println(checkInterval);
		pw.println(String.valueOf(playSound));
		
		pw.close();
	}
//=========================================================================================================//
}
