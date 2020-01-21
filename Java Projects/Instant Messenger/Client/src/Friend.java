import java.util.Vector;

public class Friend {
/* FRIEND CLASS FOR CLIENT
 * keeps the username and status of a friend, as well as pointer to his/her chat box
 * for use with the friend list
 * 
 */

	String username;
	int status;
	ChatBox chatBox;
	
	Friend(String username, int status){
		this.username = username;
		this.status = status;
	}
//======================================================================================================================================================
	void setChatBox(ChatBox chatBox) {
		this.chatBox = chatBox;
	}
//======================================================================================================================================================
	@Override
	public String toString() {
		//sets the color of the friend to be diplayed in the friend list based on current status, 1 being online
		
		if(status==1)
			return "<html><Font Color =\"Green\">" + username + "</Font></html>";
		else
			return "<html><Font Color = \"Red\">" + username + "</Font></html>";
		
	}
}
