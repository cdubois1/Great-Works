import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
//======================================================================================================================================================
//======================================================================================================================================================
/* CONNECTION TO SERVER
 * Handles all communication from the client to the server
 * Receives incoming messages from the Talker class and determines what to do with them
 * Sends all outgoing messages to the Talker class with the proper commands
 * Sends login and registration information to the CTC
 * Maintains the users friendlist
 */
public class CTS implements Runnable {

	Talker talker;
	String message;
	String username;
	MyListModel model;
	JList<Friend> friendList;
	int fileSize;
	String fileName;
	
	
	public CTS(Talker talker, MyListModel model, JList<Friend> friendList, String action, String username, String password) throws IOException {
		
		this.talker = talker;
		this.model = model;
		this.friendList = friendList;
		this.username = username;
		
		//Send login information
		talker.send(action + " " + username + " " + password);
		//start the listening thread
		Thread thread = new Thread(this);
		thread.start();
	
	}
//======================================================================================================================================================
	public void handleChat(String message) {
		//Handles INCOMING chats from friends
		//If no chat window exists, create a new one
		//Sends instructions to the chatbox to display message in green on left side
		//<verb><sender><message>
		
		CTS tmpCts = this;
		
		javax.swing.SwingUtilities.invokeLater(new Runnable()  { 
			public void run(){
				
				int firstSpace = message.indexOf(" ");
				int secondSpace = message.indexOf(" ", firstSpace+1);
				String senderUsername = message.substring(firstSpace+1, secondSpace);
				String messageText = message.substring(secondSpace+1, message.length());
				Friend tmpFriend = model.getFriend(senderUsername);
				
				if(tmpFriend.chatBox == null)
					tmpFriend.setChatBox((new ChatBox(tmpCts, tmpFriend)));
					
				tmpFriend.chatBox.addMessage(messageText, "green", "left");
			}
		});
	}
//======================================================================================================================================================
	public void handleDeniedMessage(String message) {
		//Universal error message, shows JDialog containing whatever message the CTC sent
		//<verb><error message>
		
		String errorMessage = message.substring(message.indexOf(" "), message.length());
		
		javax.swing.SwingUtilities.invokeLater(new Runnable()  { 
		public void run(){
			JOptionPane.showMessageDialog(null, errorMessage);
			}
		});
	}
//======================================================================================================================================================
	public void sendFriendRequest(String receipient) throws IOException{
		//called from MainFrame when "add friend" button is clicked
		//<verb><sender>receiver
		
		talker.send("BEFRIEND " + username + " " + receipient);
	}
//======================================================================================================================================================
	public void handleBefriend(String message){
		//Handles incoming friend requests
		//Ask the user if he/she wants to be friends
		//If yes, add user to friendslist and return "ACCEPT_FRIEND" to CTC
		//If no, inform sender the recipient declined friend request
		//<verb><sender>
		
		String parts[] = message.split(" ");
		
		javax.swing.SwingUtilities.invokeLater(new Runnable()  { 
			public void run(){
				if(JOptionPane.showConfirmDialog(null, "Do you want to be friends with " + parts[1] + "?", "Yay Friends!", JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						try {
							Friend friend = new Friend(parts[1], 1);
							model.addElement(friend);
							talker.send("ACCEPT_FRIEND " + parts[1] + " " + username );
						} catch (IOException e) {
							e.printStackTrace();
						}

				} else
					try {
						talker.send("DENIED This user does not want to be your friend");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
	}
//======================================================================================================================================================
	public void handleInitiateTransfer(String message){
		//Ask if user wants to accept files
		//If yes, set up a file transfer server using hardcoded port and IP and send info for server back to sender
		//If no, inform sender the recipient declined file transfer
		//<verb><filename><filesize><sender>
		
		String parts[] = message.split(" ");
		
		javax.swing.SwingUtilities.invokeLater(new Runnable()  { 
			public void run(){
				if(JOptionPane.showConfirmDialog(null, "Do you want to accept a file from " + parts[3] + "?", "File transfer", JOptionPane.YES_NO_OPTION, 
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
						try {
							fileName = parts[1];
							fileSize = Integer.parseInt(parts[2]);
							
							//instantiate the file transfer server
							ServerSocket serverSocket = new ServerSocket(5678);
							 new FileTransferServer(serverSocket, parts[1], parts[2]);
							talker.send("CONFIRM_TRANSFER " + parts[1] + " " + parts[3] + " " + "127.0.0.1 " + "5678");
						
						} catch (IOException e) {
							e.printStackTrace();
						}

				} else
					try {
						talker.send("DENIED This user does not want your files");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
	}
//======================================================================================================================================================
	public void handleConfirmTransfer(String message) throws IOException{
		//Recipient has accepted file transfer, create a file transfer client and connect to the file transfer server
		//Client receives the file from the chat box and transfers data behind the scenes
		//<verb><filename><original receiver><ip><port>
		
		String parts[] = message.split(" ");
		
		//instantiate the file transfer client, no need for try-catch
		 new FileTransferClient(parts[3], parts[4], model.getFriend(parts[2]).chatBox.file);
	}
//======================================================================================================================================================
	void handleStatus(String message) {
		//Updates the status of friends
		//If friend was online and chatting, inform user they have gone offline
		//<verb><sender><status>
		
		String msgParts[] = message.split(" ");
		
		Friend tmpFriend = model.getFriend(msgParts[1]); //get new status
		tmpFriend.status = Integer.parseInt(msgParts[2]); //change friends status
		
		javax.swing.SwingUtilities.invokeLater(new Runnable()  { 
			public void run(){
				friendList.repaint();
				if(tmpFriend.chatBox != null)
					tmpFriend.chatBox.addMessage(msgParts[1] + " has gone offline", "red", "center");
				}
			});
	}
//======================================================================================================================================================
	void handleSuicide() throws IOException{
		//robust code in the case that user logs in from two locations or other unexpected things
		//closes the client on this machine, user can then re-login from new location
		
		javax.swing.SwingUtilities.invokeLater(new Runnable()  { 
			public void run(){
				JOptionPane.showMessageDialog(null,"You have logged in at another location, this client has been logged out");
				System.exit(0);
				}
			});
		return;
	}
//======================================================================================================================================================
	public void sendMessage(String message) throws IOException {
		talker.send(message);
	}
//======================================================================================================================================================
	@Override
	public void run() {
		//Runs when thread is initiated
		//Ensure user successfully logged in, then drop into while loop and listen for messages
		//Determines what to do with each incoming message based on <verb>
		
		try {
			
			//get the login confirmation message
			message = talker.recieve();
			
			if(!message.startsWith("CONNECTED")) { //if the login failed, exit thread
				handleDeniedMessage(message);
				return;
			}
			
			//if we are logged in drop into the loop
			while(true) {
				
				message = talker.recieve(); //listen for message
				
				String msgParts[] = message.split(" ");
				
				switch(msgParts[0]) {
						
					case "DENIED":
						handleDeniedMessage(message);
						break;
						
					case "CHAT":
						handleChat(message);
						break;
						
					case "BEFRIEND":
						handleBefriend(message);
						break;
						
					case "GET_FRIEND":
						//Populate the Friendslist
						//<verb><sender><status>
						
						//System.out.println("adding friend: " + msgParts[1]);
						Friend friend = new Friend(msgParts[1], Integer.parseInt(msgParts[2]));
						model.addElement(friend);
						friendList.repaint();
						break;
						
					case "STATUS_UPDATE":
						handleStatus(message);
						break;
						
					case "INITIATE_TRANSFER":
						handleInitiateTransfer(message);
						break;
						
					case "CONFIRM_TRANSFER":
						handleConfirmTransfer(message);
						break;
					
					case "SUICIDE":
						handleSuicide();
						break;
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
			return;
		}
	}
//======================================================================================================================================================
//======================================================================================================================================================
}
