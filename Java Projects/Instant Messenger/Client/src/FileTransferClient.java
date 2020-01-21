import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

public class FileTransferClient implements Runnable{
/* CLIENT TO SEND FILE
 * Creates a simple socket to connect to file transfer server
 * Calculates the number of bytes in the file and transfers them in a buffer
 * Closes socket when finished
 */

	String ip;
	int portNum;
	File file;
	
	FileTransferClient(String ip, String port, File file){
		this.ip = ip;
		this.portNum = Integer.parseInt(port);
		this.file = file;
		
		Thread thread = new Thread(this);
		thread.start();
		
	}
	
	
	
	@Override
	public void run() {
		
		try {
			Socket socket = new Socket(ip, portNum);
			FileInputStream fis = new FileInputStream(file);
			OutputStream os = socket.getOutputStream();
			
			byte[] buffer = new byte[256];
			int fileSize = (int)file.length();
			
			int numBytesRead = fis.read(buffer); //prime read
			int totalBytesRead = 0;
			
			while(totalBytesRead < fileSize) {
				totalBytesRead += numBytesRead;
				os.write(buffer, 0, numBytesRead);
				numBytesRead = fis.read(buffer);
			}
			
			fis.close();
			os.close();
			socket.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
