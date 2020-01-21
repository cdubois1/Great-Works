import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.*;
//======================================================================================================================================================
//======================================================================================================================================================
/* MAINFRAME CLIENT SIDE
 * Manages the GUI
 * Connects to the server socket using hard coded IP and Port (can be changed later)
 * Instantiates CTS once user login is accepted
 * Gets the friends list from server and stores a local copy for use in the friendsHashtable
 * Instantiates the chat panes when a conversation with a friend is initiated
 */
public class MainFrame extends JFrame
				implements ActionListener, ListSelectionListener, MouseListener, WindowListener
{
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItem;
	
	JButton addFriendButton;
	
	Hashtable<String, Friend> friendHashtable;
	String message;
	JList<Friend> friendList;
	JScrollPane listScroller;
	MyListModel model;
	
	CTS cts;
	String ipAddress;
	int port;
	
	MainFrame(){
		
		//setup for socket and friendsHashtable
		ipAddress = "127.0.0.1";
		port = 6789;
		
		friendHashtable = new Hashtable<String, Friend>();
		
		//setup menubar
		menuBar = new JMenuBar();
		
		menu = new JMenu("Options");
		menu.setMnemonic(KeyEvent.VK_O);
		menu.getAccessibleContext().setAccessibleDescription("Here you can edit your options");
		menuBar.add(menu);
		
		menuItem = new JMenuItem("login/register");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("LOG/REG");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Change IP");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("IP");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Change Port");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("PORT");
		menu.add(menuItem);
		
		menuItem = new JMenuItem("Exit");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("EXIT");
		menu.add(menuItem);
		
		//add friend button
		addFriendButton = new JButton("Add Friend");
		addFriendButton.addActionListener(this);
		addFriendButton.setActionCommand("ADD");
		
		//setup Friends list
		JLabel friendListLabel = new JLabel("Friends List:");
		
		friendList = new JList<Friend>();
		friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		model = new MyListModel();
		friendList.setModel(model);
		friendList.addListSelectionListener(this);
		friendList.addMouseListener(this);
		friendList.setLayoutOrientation(JList.VERTICAL);
		friendList.setVisibleRowCount(-1);
		listScroller = new JScrollPane(friendList);
		//listScroller.setPreferredSize(new Dimension(300, 800));
		
		//JPanel uiPanel = new JPanel(new GridLayout());
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(addFriendButton);
		
		Container cp= getContentPane();
		this.setJMenuBar(menuBar);
		//cp.add(uiPanel, BorderLayout.CENTER);
		cp.add(friendListLabel, BorderLayout.NORTH);
		cp.add(listScroller, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.PAGE_END);
		setupMainFrame();
		
		//display login option pane
		showLoginDialog();
	}
//======================================================================================================================================================
	void setupMainFrame() {
		//simple copy/paste display setup using Toolkit
		Toolkit tk;
		Dimension d;
			
		tk= Toolkit.getDefaultToolkit();
		d= tk.getScreenSize();
		setSize(300,500);
		setLocation(d.width/3, d.height/6);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		setTitle("CLIENT");
		setVisible(true);
	}
//======================================================================================================================================================
	//Handles friend requests
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ADD"))
			try {
				String recipient = JOptionPane.showInputDialog("Username of new friend: ");
				cts.sendFriendRequest(recipient);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		if(e.getActionCommand().equals("LOG/REG"))
			showLoginDialog();
		if(e.getActionCommand().equals("IP"))
			setIP();
		if(e.getActionCommand().equals("PORT"))
			setPort();
		if(e.getActionCommand().equals("EXIT")) {
			System.exit(0);
		}
	}
//======================================================================================================================================================
//======================================================================================================================================================
	void showLoginDialog() {
		JTextField usernameField = new JTextField(15);
		JPasswordField passwordField = new JPasswordField(15);
		
		JPanel panel = new JPanel();
		panel.add(new JLabel("Username: "));
		panel.add(usernameField);
		panel.add(Box.createHorizontalStrut(15));
		panel.add(new JLabel("Password: "));
		panel.add(passwordField);
		
		String[] options = new String[] {"Login", "Register", "Cancel"};
		
		int option = JOptionPane.showOptionDialog(null,  panel, "Enter your username and password", JOptionPane.YES_NO_CANCEL_OPTION, 
				JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
		if(option == 0 )
			connect("LOGIN", usernameField.getText(), new String(passwordField.getPassword()));
		else if (option == 1)
			connect("REGISTER", usernameField.getText(), new String(passwordField.getPassword()));
	}
//======================================================================================================================================================
	//Establish connection and CTS if login credentials pass
	void connect(String action, String username, String password) {
		if(!(action.equals("")) && !(username.equals("")) && !(password.equals(""))) {
			try {
				Talker talker = new Talker(ipAddress, port);
				setTitle("You are: " + username);
				cts = new CTS(talker, model, friendList, action, username, password);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		else
			JOptionPane.showMessageDialog(null, "You did not enter any cridentials");
	}
//======================================================================================================================================================
	void setIP() {
		String s = (String)JOptionPane.showInputDialog(this, "Enter new IP Address", "Set IP", JOptionPane.PLAIN_MESSAGE);
		if(s.equals("") || s.contains(" "))
			JOptionPane.showMessageDialog(this, "The IP you Entered is invalid, try again");
		else
			ipAddress=s;
	}
//======================================================================================================================================================
	void setPort() {
		String s = (String)JOptionPane.showInputDialog(this, "Enter new Port number", "Set IP", JOptionPane.PLAIN_MESSAGE);
		if(s.equals("") || s.contains(" "))
			JOptionPane.showMessageDialog(this, "The port you Entered is invalid, try again");
		else
			port= Integer.parseInt(s);
	}
	
	void createChatBox(Friend friend) {
		friend.setChatBox(new ChatBox(cts, friend));
		friend.chatBox.addWindowListener(this);
	}
//======================================================================================================================================================
	//creates a chatbox for the selected friend
	@Override
	public void valueChanged(ListSelectionEvent e) {}

	@Override
	public void mouseClicked(MouseEvent me) {
		
		if(me.getClickCount() == 2) {
			Friend tmpFriend = friendList.getSelectedValue();
			if(tmpFriend.chatBox == null)
				createChatBox(tmpFriend);
			else
				tmpFriend.chatBox.requestFocus();
		}
	}
//======================================================================================================================================================
	@Override
	public void mouseEntered(MouseEvent me) {}
	@Override
	public void mouseExited(MouseEvent me) {}
	@Override
	public void mousePressed(MouseEvent me) {}
	@Override
	public void mouseReleased(MouseEvent me) {}
//======================================================================================================================================================
	@Override
	public void windowActivated(WindowEvent e) {}
	
	//clears pointer to chatbox when window is closed
	@Override
	public void windowClosed(WindowEvent e) {
		//e.getWindow().chatBox = null;
		ChatBox tmpChatBox = (ChatBox) e.getSource();
		tmpChatBox = null;
	}
//======================================================================================================================================================
	@Override
	public void windowClosing(WindowEvent e) {}
	@Override
	public void windowDeactivated(WindowEvent e) {}
	@Override
	public void windowDeiconified(WindowEvent e) {}
	@Override
	public void windowIconified(WindowEvent e) {}
	@Override
	public void windowOpened(WindowEvent e) {}
//======================================================================================================================================================
}
