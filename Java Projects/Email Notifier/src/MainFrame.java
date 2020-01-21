import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import javax.mail.*;
import javax.swing.*;

public class MainFrame extends JDialog
			implements ActionListener, ItemListener
/* GUI
 * Gathers all the necessary information to set up the notifier and stores it in a file
 */
{
	
	String filename = "/settings.txt";
	File file;
	User_Information userInfo;
	
	JLabel hostLabel, portLabel, usernameLabel, passwordLabel, intervalLabel, soundLabel;
	JTextField hostTF, portTF, usernameTF;
	JButton acceptButton, cancelButton;
	JPasswordField passwordField;
	JSpinner intervalSpinner;
	JCheckBox soundButton;
	String host;
	String port;
	String username;
	String password;
	int checkInterval;
	boolean playSound;
	
	MainFrame(User_Information userInfo){
		
		this.userInfo = userInfo;
		
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbcs = new GridBagConstraints();
		gbcs.fill = GridBagConstraints.HORIZONTAL;
		
		hostLabel = new JLabel("Server Name: ");
		gbcs.gridx=0;
		gbcs.gridy=0;
		gbcs.gridwidth =1;
		panel.add(hostLabel, gbcs);
		//hostLabel.setBounds(80, 70, 200, 30);
		
		portLabel = new JLabel("Port Number: ");
		gbcs.gridx=0;
		gbcs.gridy=1;
		gbcs.gridwidth =1;
		panel.add(portLabel, gbcs);
		//portLabel.setBounds(80, 70, 200, 30);
		
		usernameLabel = new JLabel("Username: ");
		gbcs.gridx=0;
		gbcs.gridy=2;
		gbcs.gridwidth =1;
		panel.add(usernameLabel, gbcs);
		//usernameLabel.setBounds(80, 70, 200, 30);
		
		passwordLabel= new JLabel("Password: ");
		gbcs.gridx=0;
		gbcs.gridy=3;
		gbcs.gridwidth =1;
		panel.add(passwordLabel, gbcs);
		//passwordLabel.setBounds(80, 70, 200, 30);
		
		intervalLabel = new JLabel("Check interval (min): ");
		gbcs.gridx=0;
		gbcs.gridy=4;
		gbcs.gridwidth =1;
		panel.add(intervalLabel, gbcs);
		//intervalLabel.setBounds(80, 200, 200, 30);
		
		soundLabel = new JLabel("New mail sound: ");
		gbcs.gridx=0;
		gbcs.gridy=5;
		gbcs.gridwidth =1;
		panel.add(soundLabel, gbcs);
		//soundLabel.setBounds(80, 150, 200, 30);
		
		hostTF = new JTextField();
		gbcs.gridx=1;
		gbcs.gridy=0;
		gbcs.gridwidth =1;
		panel.add(hostTF, gbcs);
		//hostTF.setBounds(300, 70, 200, 30);
		
		portTF = new JTextField();
		gbcs.gridx=1;
		gbcs.gridy=1;
		gbcs.gridwidth =1;
		panel.add(portTF, gbcs);
		//portTF.setBounds(300, 70, 200, 30);
		
		usernameTF = new JTextField();
		gbcs.gridx=1;
		gbcs.gridy=2;
		gbcs.gridwidth =1;
		panel.add(usernameTF, gbcs);
		//usernameTF.setBounds(300, 70, 200, 30);

		passwordField = new JPasswordField();
		gbcs.gridx=1;
		gbcs.gridy=3;
		gbcs.gridwidth =1;
		panel.add(passwordField, gbcs);
		//passwordField.setBounds(300, 110, 200, 30);
		
		SpinnerModel model = new SpinnerNumberModel(10, 1, 60, 1);
		intervalSpinner = new JSpinner(model);
		gbcs.gridx=1;
		gbcs.gridy=4;
		gbcs.gridwidth =1;
		panel.add(intervalSpinner, gbcs);
		
		soundButton = new JCheckBox("Play Sound");
		soundButton.setSelected(true);
		playSound=true;
		soundButton.addItemListener(this);
		gbcs.gridx=1;
		gbcs.gridy=5;
		gbcs.gridwidth =1;
		panel.add(soundButton, gbcs);
		
		acceptButton = new JButton("ACCEPT");
		acceptButton.addActionListener(this);
		acceptButton.setActionCommand("ACCEPT");
		
		cancelButton = new JButton("CANCEL");
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand("CANCEL");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(acceptButton);
		buttonPanel.add(cancelButton);
		
		getContentPane().add(panel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
		
		setTitle("User Settings");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setResizable(false);
		Toolkit tk;
		Dimension d;
		tk= Toolkit.getDefaultToolkit();
		d= tk.getScreenSize();
		setLocation(d.width/2, d.height/2);
		
		//get user settings if they exist
		String currentDir = System.getProperty("user.dir");
		if(Files.exists(Paths.get(currentDir+filename))) //settings file already exists
			try {
				
				userInfo.loadFromFile(file = new File(currentDir+filename));
				fillFields(userInfo);
			
			}catch(Exception e) {
				e.printStackTrace();
			}
		
		setVisible(true);
	}
//=========================================================================================================//
	boolean checkFields() {
		//Robust code to ensure fields are properly filled in
		
		host = hostTF.getText();
		port = portTF.getText();
		
		username = usernameTF.getText();
		password = new String(passwordField.getPassword());
		checkInterval = (int) intervalSpinner.getValue();
		
		if(host.equals(""))
			return false;
		
		else if(port.equals(""))
			return false;
		
		else if(username.equals(""))
			return false;
		
		else if(password.equals(""))
			return false;
		
		else if(checkInterval <1 || checkInterval>60)
			return false;
		
		else 
			return true;
			
	}
//=========================================================================================================//
	void fillFields(User_Information userInfo) {
		//Fill in fields with info from file (very unsafe)
		
		hostTF.setText(userInfo.host);
		portTF.setText(userInfo.port);
		
		usernameTF.setText(userInfo.username);
		passwordField.setText(userInfo.password);
		
		intervalSpinner.setValue(userInfo.checkInterval);
		soundButton.setSelected(userInfo.playSound);
	}
//=========================================================================================================//
	@Override
	public void actionPerformed(ActionEvent e) {
		//When the user hits accept we check the fields
		//If they pass, we save the info to file to be used by User_Information class, then close
		//This is not the best way to do this, but is what was required
		
		if(e.getActionCommand().equals("ACCEPT"))
			
			if(checkFields()) {
				String currentDir = System.getProperty("user.dir");
				
				try {
					userInfo.saveToFile(host, port, username, password, checkInterval, playSound, file = new File(currentDir+filename));
					System.out.println("created new user settings file");
				
				} catch(Exception exc) {
					exc.printStackTrace();
				}
				dispose();
			}
			
			else
				JOptionPane.showMessageDialog(this, "You have entered bad information");
		
		if(e.getActionCommand().equals("CANCEL")) {
			dispose();
		}
	}
//=========================================================================================================//
	@Override
	public void itemStateChanged(ItemEvent e) {
		//change the boolean value based on the selection
		
		if(soundButton.isSelected())
			playSound=true;
		else
			playSound=false;
	}
//=========================================================================================================//
}
