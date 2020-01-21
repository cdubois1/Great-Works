import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Hashtable;
//======================================================================================================================================================
//======================================================================================================================================================
/* CONNECTION TO CLIENT
 * Handles all communication from the server to the client
 * Receives incoming messages from the Talker class and determines what to do with them
 * Sends all outgoing messages to the Talker class with the proper commands
 * Keeps the hashtable of users up to date with pointers to CTC's
 * Handles login and registration requests with the server
 */
public class CTC implements Runnable {
	
	Talker talker;
	String username;
	String message;
	MyHashtable hashtable; //hashtable for users
	boolean loggedIn;

	public CTC(Talker talker, MyHashtable hashtable) throws IOException{
		
		this.talker = talker;
		this.hashtable = hashtable;
		loggedIn = false;
		
		//start listening for messages
		Thread thread = new Thread(this);
		thread.start();
	}
//======================================================================================================================================================
	void handleRegister(String message) throws IOException{
		//creates a new user in the hashtable with given credentials
		//<verb><username><password>
		
		String parts[] = message.split(" ");
		User tmpUser = hashtable.get(parts[1]);
		if(tmpUser != null) { //check hash table for username
			talker.send("DENIED USER ALREADY EXISTS");
			return; //stop this thread
		}
		else { //username is not taken and account can be created
			User user = new User(this); //construct a user and pass it this CTC
			user.username = parts[1];
			user.password = parts[2];
			hashtable.put(user.username, user);//add this CTC to the hashtable
			username = parts[1]; //set this CTC's username
			talker.send("CONNECTED");
			loggedIn = true;
		}
	}
//======================================================================================================================================================
	void handleLogin(String message) throws IOException{
		//checks login credentials
		//if valid set up CTC for this user and send friendslist to client
		//<verb> <username> <password>
		
		String parts[] = message.split(" ");
		
		User user = hashtable.get(parts[1]);
		
		if(user == null) { //ensure user is registered
			sendMessage("DENIED USER DOES NOT EXIST PLEASE REGISTER AN ACCOUNT");
			return;
		}
		else if(user.ctc != null) { //ensure user is not logged into another location
			sendMessage("DENIED USER ALREADY LOGGED IN, PLEASE TRY LOGGING IN AGAIN");
			user.ctc.sendMessage("SUICIDE"); 
		}
		else if(user.password.equals(parts[2])) { //check that password given matches actual password
			user.setCTC(this); //give user this CTC
			username = parts[1];
			talker.send("CONNECTED");
			loggedIn = true;
			
			for(int i=0; i<user.friendVector.size(); i++) { //if correct send friends list
				User friend = hashtable.get(user.friendVector.elementAt(i));
				int onlineStatus =0;
				if(friend.ctc != null)
					onlineStatus =1;
				
				talker.send("GET_FRIEND " + friend.username + " " + onlineStatus);
			}
			
			updateFriendStatus("1");
		}
		else {
			talker.send("DENIED INVALID CREDENTIALS");
			return;
		}
	}
//======================================================================================================================================================
	void handleChat(String message) throws IOException {
		//Ensure user is online and is a friend, then send chat
		//<verb> <receiver> <message>
		
		int firstSpace = message.indexOf(" ");
		int secondSpace = message.indexOf(" ", firstSpace+1);
		String receiverUsername = message.substring(firstSpace+1, secondSpace);
		
		if(hashtable.get(receiverUsername).ctc == null || !hashtable.get(username).friendVector.contains(receiverUsername))
			talker.send("DENIED USER OFFLINE OR NOT IN FRIENDS LIST");
		else {
			String messageText = message.substring(secondSpace+1, message.length());
			messageText.replace("\0", "\n"); //newlines must be disabled to send full text area
			hashtable.get(receiverUsername).ctc.talker.send("CHAT " + username + " " + messageText); //get the message from the parts
		}
	}
//======================================================================================================================================================
	void handleBefriend(String message) throws IOException{
		//upon sending friend request,
		//ensure user is registered, is not already friends, and is online
		//then send friend request to the user
		// <verb> <sender> <receiver>
		
		String msgParts[] = message.split(" ");
		
		User receiver = hashtable.get(msgParts[2]);
		
		if(receiver == null) //receiver exists
			talker.send("DENIED USER DOES NOT EXIST");
		
		else if(receiver.friendVector.contains(msgParts[1])) //receiver is not already friends
			talker.send("DENIED ALREADY FRIENDS");
		
		else if(receiver.ctc == null) //receiver is online
			talker.send("DENIED USER IS OFFLINE");
		
		else if(msgParts[1].equals(msgParts[2])) //cannot friend yourself
			talker.send("DENIED CANNOT BE FRIENDS WITH YOURSELF");
		
		else
			receiver.ctc.talker.send("BEFRIEND " + msgParts[1]); //ask recipient if he wants to be friends
	}
//======================================================================================================================================================
	void handleAcceptFriend(String message) throws IOException{
		//Add friend to friendslist on server and client
		//<verb><sender><receiver>
		
		String msgParts[] = message.split(" ");
		hashtable.get(username).friendVector.add(msgParts[1]); //add recipient to sender
		hashtable.get(msgParts[1]).friendVector.add(username); //add sender to recipient
		
		if(hashtable.get(msgParts[1]).ctc != null)
			hashtable.get(msgParts[1]).ctc.sendMessage("GET_FRIEND " + msgParts[2] + " " + 1); //tell sender to add friend locally
		else
			sendMessage("DENIED " + msgParts[1] + " WENT OFFLINE");
	}
//======================================================================================================================================================
	public void sendMessage(String message) throws IOException {
		talker.send(message);
	}
//======================================================================================================================================================
	void updateFriendStatus(String status) {
		//keep the friendslist up to date
		//when a user logs in/out this is called to update the status to all his/her friends
		User user = hashtable.get(username);
		
		for(int i=0; i<user.friendVector.size(); i++) {
			User tmpUser = hashtable.get(user.friendVector.elementAt(i));
			if(tmpUser.ctc != null)
				try {
					tmpUser.ctc.sendMessage("STATUS_UPDATE "+ username + " " + status);
				} catch (IOException e) {
					System.out.println("Could not update friend status for " + tmpUser.username);
					e.printStackTrace();
				}
		}
	}
//======================================================================================================================================================
	void handleInitiateTransfer(String message) throws IOException{
		//begin file transfer
		//ensure user is online, prompt for transfer
		//<verb><filename><filesize><receiver>
		
		String parts[] = message.split(" ");
		User tmpUser = hashtable.get(parts[3]);
		
		if(tmpUser.ctc == null)
			sendMessage("DENIED USER IS OFFLINE");
		else
			tmpUser.ctc.sendMessage("INITIATE_TRANSFER " + parts[1] + " " + parts[2] + " " + username);
	}
//======================================================================================================================================================
	void handleConfirmTransfer(String message) throws IOException {
		//user has accepted file transfer
		//ensure user stays online, send IP, Port, Filename information to begin transfer
		//<verb><filename><original sender><ip><port>
		
		String parts[] = message.split(" ");
		User tmpUser = hashtable.get(parts[2]);
		if(tmpUser.ctc != null)
			tmpUser.ctc.sendMessage("CONFIRM_TRANSFER " + parts[1] + " " + username + " " + parts[3] + " " + parts[4]);
		else
			sendMessage("DENIED USER LOGGED OFF");
	}
//======================================================================================================================================================
	@Override
	public void run() {
		//Runs when thread is initiated
		//First ensure user is logged in, then drop into loop constantly checking for new message
		//Handles all incoming messages by checking the <verb> and calling the corresponding method
		//On user logout, cleans up and closes the thread
		
		try {
			
			message = talker.recieve();
				
			if(message.startsWith("REGISTER")) 
				handleRegister(message);
			else if(message.startsWith("LOGIN"))
				handleLogin(message);
			else { //robust code
				talker.send("DENIED FAILED CREDENTIALS");
				return; //user did not login or register, exit thread
			}
			
			//user is logged in and registered in the hashtable, listen for messages
			while(loggedIn) {
				message = talker.recieve(); //switch...
				String msgParts[] = message.split(" ");
				
				switch(msgParts[0]) {
				
					case "CHAT":
						handleChat(message);
						break;
					
					case "BEFRIEND":
						handleBefriend(message);
						break;
						
					case "ACCEPT_FRIEND":
						handleAcceptFriend(message);
						break;
						
					case "INITIATE_TRANSFER":
						handleInitiateTransfer(message);
						break;
						
					case "CONFIRM_TRANSFER":
						handleConfirmTransfer(message);
						break;
				}
				
			}
			
		}catch(IOException e) {
			hashtable.get(username).setCTC(null); //set user offline
			updateFriendStatus("0"); //let his friends know
			e.printStackTrace();
			return; //exit thread
		}
	}
//======================================================================================================================================================
//======================================================================================================================================================
}
