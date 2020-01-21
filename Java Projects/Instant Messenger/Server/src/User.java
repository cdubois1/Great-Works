import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

public class User {
/* USER CLASS FOR SERVER
 * Holds the username, password, and friends list for a registered user
 * Created for each registered user when the server is initiated, or when a new user registers an account
 * For use in the user hashtable
 * Can write its data to a DataOutputStream and load its data from a DataInputStream
 */

	String username;
	String password;
	Vector<String> friendVector;
	CTC ctc;
	
	User(CTC ctc){ //constructor given CTC
		this.ctc = ctc;
		friendVector = new Vector<String>();
	}
	
	User(){ //Constructor without CTC
		friendVector = new Vector<String>();
	}
//======================================================================================================================================================
	void setCTC(CTC ctc) {
		this.ctc = ctc;
	}
//======================================================================================================================================================
	void saveToFile(DataOutputStream dos) throws IOException{
		//write username, password and friends to given DOS
		
		dos.writeUTF(username);
		dos.writeUTF(password);
		
		int numFriends = friendVector.size();
		dos.writeInt(numFriends);
		
		for(int i=0; i<numFriends; i++)
			dos.writeUTF(friendVector.elementAt(i));
	}
//======================================================================================================================================================
	void loadFromFile(DataInputStream dis) throws IOException{
		//set username, password, and fill friendVector from given DIS
		
		this.username = dis.readUTF();
		this.password = dis.readUTF();
		
		int numFriends = dis.readInt();
		
		for(int i =0; i<numFriends; i++){
			String friendUsername = dis.readUTF();
			this.friendVector.add(friendUsername);
		}
	}
//======================================================================================================================================================
//======================================================================================================================================================
}
