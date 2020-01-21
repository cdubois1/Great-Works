import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.DefaultListModel;

public class MyListModel extends DefaultListModel<Friend>{
//List model for use on client side
	
	Friend getFriend(String username) {
		//returns the currently selected friend, null otherwise
		
		for(int i=0; i<this.size(); i++) {
			if(this.getElementAt(i).username.equals(username))
				return this.getElementAt(i);
		}
		return null;
	}
}
