import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
//======================================================================================================================================================
//======================================================================================================================================================
public class LoginFrame extends JDialog
		implements ActionListener 
/* GUI FOR LOGGING IN OR REGISTERING
 * Uses JTextField for username and JPasswordField for the password
 * Has login and register buttons
 * Sends all data to the "connect" method of the client MainFrame
 * Very unsafe, password is stored as a string and not protected at all
 */
{
	JButton loginButton;
	JButton registerButton;
	JLabel usernameLabel;
	JLabel passwordLabel;
	JTextField usernameField;
	JPasswordField passwordField;
	MainFrame client;
	
	LoginFrame(MainFrame client){
		
		this.client = client;
		
		Container contentPane = getContentPane();
		SpringLayout layout = new SpringLayout();
		contentPane.setLayout(layout);
		
		loginButton = new JButton("Login");
		loginButton.addActionListener(this);
		loginButton.setActionCommand("LOGIN");
		
		registerButton = new JButton("Register");
		registerButton.addActionListener(this);
		registerButton.setActionCommand("REGISTER");
		
		usernameLabel = new JLabel("Username: ");
		contentPane.add(usernameLabel);
		
		usernameField = new JTextField(15);
		contentPane.add(usernameField);
		
		layout.putConstraint(SpringLayout.WEST, usernameLabel, 5, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, usernameLabel, 5, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.WEST, usernameField, 5, SpringLayout.EAST, usernameLabel);
		layout.putConstraint(SpringLayout.NORTH, usernameField, 5, SpringLayout.NORTH, contentPane);
		
		passwordLabel = new JLabel("Password: ");
		contentPane.add(passwordLabel);

		passwordField = new JPasswordField(15);
		contentPane.add(passwordField);
		
		layout.putConstraint(SpringLayout.WEST, passwordLabel, 5, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.NORTH, passwordLabel, 5, SpringLayout.NORTH, usernameLabel);
		layout.putConstraint(SpringLayout.WEST, passwordField, 5, SpringLayout.EAST, passwordLabel);
		layout.putConstraint(SpringLayout.NORTH, passwordField, 5, SpringLayout.NORTH, usernameLabel);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(loginButton);
		buttonPanel.add(registerButton);
		
		//layout.putConstraint(SpringLayout.EAST, contentPane, 5, SpringLayout.EAST, usernameField);
		//layout.putConstraint(SpringLayout.SOUTH, contentPane, 5, SpringLayout.SOUTH, passwordField);
		//SpringUtilities.makeCompactGrid(this, 2, 2, 6, 6, 6, 6);
		contentPane.add(buttonPanel, BorderLayout.PAGE_END);
		
		setTitle("User Login");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pack();
		setResizable(false);
		Toolkit tk;
		Dimension d;
		tk= Toolkit.getDefaultToolkit();
		d= tk.getScreenSize();
		setLocationRelativeTo(getParent());
		setSize(d.width/8, d.height/6);
		
		this.requestFocus();
		this.isModal();
		setVisible(true);
		
	}
//======================================================================================================================================================
	@Override
	public void actionPerformed(ActionEvent e) {
		//When the user hits login or register, call the connect method of the client MainFrame and pass input, then close
		
		if(e.getActionCommand().equals("LOGIN")) {
			client.connect("LOGIN", usernameField.getText(), new String(passwordField.getPassword()));
			dispose();
		}
		
		if(e.getActionCommand().equals("REGISTER")) {
			client.connect("REGISTER", usernameField.getText(), new String(passwordField.getPassword()));
			dispose();
		}
		
	}
//======================================================================================================================================================
//======================================================================================================================================================
}
