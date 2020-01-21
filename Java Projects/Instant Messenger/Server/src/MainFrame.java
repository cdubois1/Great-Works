import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.*;
//======================================================================================================================================================
//======================================================================================================================================================
/* MAINFRAME SERVERSIDE
 * Opens the server socket on specified port
 * Listens for connections and creates a CTC with associated Talker
 * Manages the hashtable of usersames, passwords, and connections
 */
public class MainFrame extends JFrame
				implements ActionListener
{
	MyHashtable hashtable;
	JButton startButton;
	JButton exitButton;
	JTextField console;
	ServerSocket serverSocket;
	
	MainFrame(){
		
		//create and load hashtable if file exists
		hashtable = new MyHashtable();
		File tempFile = new File("hashtable.dat");
		if(tempFile.exists())
			hashtable.loadUsers();
		
		//simple window for troubleshooting
		console = new JTextField();
		console.setSize(300,300);
		
		exitButton = new JButton("Exit");
		exitButton.addActionListener(this);
		exitButton.setActionCommand("EXIT");
		
		JPanel bp = new JPanel();
		bp.add(exitButton);
		
		Container cp= getContentPane();
		cp.add(bp, BorderLayout.PAGE_END);
		cp.add(console, BorderLayout.CENTER);
		setupMainFrame();
		
		//create server socket on hardcoded port
		boolean serverIsListening = false;
		try {
			
			serverSocket = new ServerSocket(6789);
			serverIsListening = true;
		
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		//listen on port, if connection is made create a Talker and CTC for them
		while(serverIsListening) {
			
			try {
				
				Socket socket = serverSocket.accept();
				Talker talker = new Talker(socket);
				CTC ctc = new CTC(talker, hashtable);
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
//======================================================================================================================================================
	void setupMainFrame() {
		//simple copy/paste display setup using Toolkit
		Toolkit tk;
		Dimension d;
			
		tk= Toolkit.getDefaultToolkit();
		d= tk.getScreenSize();
		setSize(500,500);
		setLocation(d.width/5, d.height/6);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		setTitle("SERVER");
		setVisible(true);
	}
//======================================================================================================================================================
	@Override
	public void actionPerformed(ActionEvent e) {
		//on exit, close the socket and save the hashtable
		
		if(e.getActionCommand().equals("EXIT")) {
			
			try {
				
				hashtable.storeUsers();
				serverSocket.close();
				System.exit(0);
			
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
//======================================================================================================================================================
//======================================================================================================================================================
}
