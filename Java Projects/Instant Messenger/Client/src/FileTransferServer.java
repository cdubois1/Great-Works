import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransferServer implements Runnable{
/* SERVER TO RECEIVE FILE
 * Gets file name and size from sender
 * Listens for connection to the server then creates inputstream and a destination file
 * Receives file one buffer of bytes at a time and writes to output file
 * Closes server socket when finished
 */

	ServerSocket serverSocket;
	String filename;
	int filesize;
	
	FileTransferServer(ServerSocket serverSocket, String filename, String filesize){
		this.serverSocket = serverSocket;
		this.filename = filename;
		this.filesize = Integer.parseInt(filesize);
		
		Thread thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		try {
			Socket socket = serverSocket.accept();
			
			InputStream is = socket.getInputStream();
			
			File file = new File(filename);
			FileOutputStream fos = new FileOutputStream(file);
			
			byte[] buffer = new byte[256];
			int numBytesRead = is.read(buffer); //prime read
			int totalBytesRead = 0;
			
			while(totalBytesRead < filesize) {
				totalBytesRead += numBytesRead;
				fos.write(buffer, 0, numBytesRead);
				numBytesRead = is.read(buffer);
			}
			
			is.close();
			fos.close();
			socket.close();
			
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

}
