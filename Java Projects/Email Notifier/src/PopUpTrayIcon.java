import java.awt.*;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import javax.swing.Timer;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;


public class PopUpTrayIcon implements ActionListener{
/* PERFORM NOTIFICATIONS
 * Creates a tray icon, then a connection using the userInfo
 * Periodically checks for new email messages in the INBOX and notifies the user when they receive mail
 */

	Timer 		timer;
	TrayIcon 	trayIcon;
	SystemTray 	tray;
	PopupMenu 	popupMenu;
	
	Session 	session;
	Store 		store;
	Folder 		inboxFolder;
	Authenticator authenticator;
	Message[] 	messages;
	Properties 	props;
	User_Information userInfo;
	MenuItem soundItem;
	boolean playSound;
	
	PopUpTrayIcon(User_Information userInfo){
		
		this.userInfo = userInfo;
		playSound = userInfo.playSound;
		
		MenuItem menuItem;
		popupMenu = new PopupMenu();
		
		menuItem = new MenuItem("Close");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("CLOSE");
		popupMenu.add(menuItem);
		
		menuItem = new MenuItem("Settings");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("SETTINGS");
		popupMenu.add(menuItem);
		
		soundItem = new MenuItem();
		
		if(playSound)
			soundItem.setLabel("Disable Sound");
		else
			soundItem.setLabel("Enable Sound");
		
		soundItem.addActionListener(this);
		soundItem.setActionCommand("SOUND");
		popupMenu.add(soundItem);
		
		//attempt to create TrayIcon
		if(SystemTray.isSupported()) {
			
			tray = SystemTray.getSystemTray();
			trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage("mailIcon.png"), "Email Notifier");
			trayIcon.setImageAutoSize(true);
			trayIcon.setPopupMenu(popupMenu);
			
			try {
				tray.add(trayIcon);
			
			} catch(AWTException e) {
				e.printStackTrace();
				return;
			}
		}
		
		else {
			System.out.println("Failed to run program");
			return;
		}
		
		//attempt to create a connection
		try {
			
			setupConnection();
			
			inboxFolder.open(Folder.READ_WRITE);
			int newMsgs = inboxFolder.getNewMessageCount();
			JOptionPane.showMessageDialog(null, "You have " + newMsgs + " new messages!");
			
			inboxFolder.close(false);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//start the timer
		timer = new Timer(userInfo.checkInterval*1000, this);
		timer.addActionListener(this);
		timer.setActionCommand("TIMER");
		timer.setRepeats(false);
		timer.start();
	
	}
//=========================================================================================================//
	void setupConnection() throws Exception{
		//using IMAPS protocal will attempt to create a session to the specified host and port
		//then gets a store from the session and connects to it so we can access the "INBOX" folder
		
		String protocolProvider = "imaps";
		
		props = new Properties();
		props.put("mail.imap.host", userInfo.host);
		props.put("mail.imap.port", userInfo.port);
		
		//Had problems creating the session, this is the only thing which seemed to work
		//props.put("mail.imap.starttles.enable", "true");
		props.setProperty("mail.imap.socketFactory.port", userInfo.port);
		props.put("mail.imap.auth.plain.disable", "true");
		
		authenticator = null;
		
		session = Session.getDefaultInstance(props, authenticator);
		session.setDebug(true);
		
		store = session.getStore(protocolProvider);
		store.connect(userInfo.host, userInfo.username, userInfo.password);
		
		inboxFolder = store.getFolder("INBOX");
	}
//=========================================================================================================//
	void processInbox(Folder inboxFolder) throws Exception{
		//checks inbox folder for new messages, if it finds one it calls processMessage
		
		inboxFolder.open(Folder.READ_WRITE);
		
		if(inboxFolder.getNewMessageCount() > 0)
			processMessage(inboxFolder.getMessage(inboxFolder.getMessageCount()));
		
		inboxFolder.close(false);
	}
//=========================================================================================================//
	void processMessage(Message message) throws Exception{
		//gets the Sender and Subject of the new message
		//shows JOptionPane message with info and plays sound if desired
		
		Address[] fromAddress = message.getFrom();
		JLabel helloLabel = new JLabel("Hello! You've got mail!");
		JLabel fromLabel = new JLabel("From: " + fromAddress.toString());
		JLabel subjectLabel = new JLabel("Subject: " + message.getSubject());
		JLabel[] arrLabels = {helloLabel, fromLabel, subjectLabel};
		
		if(playSound)
			playNewEmailJingle();
		
		//trayIcon.setImage(Toolkit.getDefaultToolkit().getImage("mailNotificationIcon.png"));
		JOptionPane.showMessageDialog(null, arrLabels);
	}
//=========================================================================================================//
	void playNewEmailJingle() throws Exception{
		//plays the sound file specified
		
		File audioFile = new File("./" + "mailSound.wav");
		AudioInputStream audioIn = AudioSystem.getAudioInputStream(audioFile.toURI().toURL());
		
		Clip clip = AudioSystem.getClip();
		clip.open(audioIn);
		clip.start();
	}
//=========================================================================================================//
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//close the program
		if(e.getActionCommand().equals("CLOSE")) {
			try {
				store.close();
				System.exit(0);
			
			}catch (Exception exc) {
				exc.printStackTrace();
				System.out.println("Failed to close the store, open 24hours now");
			}
		}
		
		//open up the MainFrame and re-enter settings
		if(e.getActionCommand().equals("SETTINGS")) {
			try {
				new MainFrame(userInfo);
				setupConnection();
			
			}catch (Exception exc) {
				exc.printStackTrace();
			}
		}	
		
		//set the sound to on/off
		if(e.getActionCommand().equals("SOUND")) {
			if(playSound) {
				playSound = false;
				soundItem.setLabel("Enable Sound");
			}
			
			else {
				playSound = true;
				soundItem.setLabel("Disable Sound");
			}
		}
		
		//triggered from the timer event to check for new messages
		if(e.getActionCommand().equals("TIMER")) {
			try {
				processInbox(inboxFolder);
				timer.restart();
			
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}
		
	}
//=========================================================================================================//
}
