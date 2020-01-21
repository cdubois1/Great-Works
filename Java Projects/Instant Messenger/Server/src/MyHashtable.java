import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;


@SuppressWarnings("serial")
public class MyHashtable extends Hashtable<String, User> {
/* SERVERSIDE HASHTABLE FOR USERS
 * Simple hashtable to keep track of registered users on the server
 * Can store and load itself from file
 * Makes use of .saveToFile and .loadFromFile methods in the User class
 */
	
	User user;
	final String fileName = "hashtable.dat";
	
	void storeUsers(){
		//iterate through each user in the hashtable and store their info on file
		
		try {
			
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(fileName));
			dos.writeInt(this.size()); //store number of users for use when loading
			
			Enumeration<User> e;
			e = this.elements();
			
			//for each element call User.saveToFile: writes username, password and friends
			while(e.hasMoreElements())
				e.nextElement().saveToFile(dos); 
			
			dos.close();
		
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
//======================================================================================================================================================
	void loadUsers(){
		//for each user in the file create a user and add them to the hashtable
		
		try {
			
			DataInputStream dis = new DataInputStream(new FileInputStream(fileName));
			int numUsers = dis.readInt();
			
			for(int i=0; i<numUsers; i++){
				User user = new User();
				
				//call User.loadFromFile: reads username, password and friends
				user.loadFromFile(dis);
				
				this.put(user.username, user);
			}
			
			dis.close();
		
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
//======================================================================================================================================================
//======================================================================================================================================================
}
