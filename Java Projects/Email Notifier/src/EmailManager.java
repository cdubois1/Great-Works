import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
//====================================================================================
//====================================================================================
public class EmailManager implements WindowListener{
/* CONNECTS USER_INFO to the MainFrame
 * creates a pointer to User_Information and passes it to MainFrame to be used
 * Launches the popup tray icon when closed
 * Not the best way to do this, but was required
 */
	
	User_Information userInfo;
	
	EmailManager(){
	
		userInfo = new User_Information();
	
		MainFrame mf = new MainFrame(userInfo);
		mf.addWindowListener(this);
	}
//====================================================================================
	@Override
	public void windowActivated(WindowEvent arg0) {}
//====================================================================================
	@Override
	public void windowClosed(WindowEvent arg0) {
		//on close, launch the tray icon
		
		new PopUpTrayIcon(userInfo);
	}
//====================================================================================
	@Override
	public void windowClosing(WindowEvent arg0) {}
//====================================================================================
	@Override
	public void windowDeactivated(WindowEvent arg0) {}
//====================================================================================
	@Override
	public void windowDeiconified(WindowEvent arg0) {}
//====================================================================================
	@Override
	public void windowIconified(WindowEvent arg0) {}
//====================================================================================
	@Override
	public void windowOpened(WindowEvent arg0) {}

}
