import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.Object;

public class InputDialog extends JDialog
			implements ActionListener, ChangeListener
{
	JComboBox startingDirectionField;
	JSlider degreesField;
	JSlider feetField;
	JComboBox facingDirectionField;
	JTextField distanceField;
	
	JLabel startingDirectionLabel;
	JLabel degreesLabel;
	JLabel degreesLabelExact;
	JLabel feetLabel;
	JLabel feetLabelExact;
	JLabel facingDirectionLabel;
	JLabel distanceLabel;
	
	JButton cancelButton;
	JButton saveButton;
	JButton updateButton;
	JButton addButton;
	
	JPanel panel;
	JPanel buttonPanel;
	
	Container cp;
	SurveyCall call;
	int index;
	DataManager inter;
	
	InputDialog(DataManager i)
	{
		super(null,"ENTER THE DATA", Dialog.ModalityType.APPLICATION_MODAL);
		this.inter = i;
		setTitle("Add a new Survey Call");
		setupMainGUI();
		
		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("CANCEL");
		cancelButton.addActionListener(this);
		cancelButton.setVerifyInputWhenFocusTarget(false);
		
		updateButton = new JButton("Add");
		updateButton.setActionCommand("ADD");
		updateButton.addActionListener(this);
		
		addButton = new JButton("Add and Close");
		addButton.setActionCommand("CLOSE");
		addButton.addActionListener(this);
		
		buttonPanel = new JPanel();
		buttonPanel.add(addButton);
		buttonPanel.add(updateButton);
		buttonPanel.add(cancelButton);

		cp.add(buttonPanel, BorderLayout.SOUTH);
		
		setupMainDialog();
	}
	InputDialog(DataManager i, SurveyCall c, int index)
	{
		super(null,"ENTER THE DATA", Dialog.ModalityType.APPLICATION_MODAL);
		this.inter= i;
		this.index = index;
		setTitle("Edit the Survey Call");
		setupMainGUI();
		loadFields(c);
		
		cancelButton = new JButton("CANCEL");
		cancelButton.setActionCommand("CANCEL");
		cancelButton.addActionListener(this);
		cancelButton.setVerifyInputWhenFocusTarget(false);
		
		saveButton = new JButton("REPLACE");
		saveButton.setActionCommand("REPLACE");
		saveButton.addActionListener(this);
		
		buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(saveButton);
		buttonPanel.add(cancelButton);
		
		cp.add(buttonPanel, BorderLayout.SOUTH);
		
		setupMainDialog();
	}
	void setupMainGUI()
	{
		startingDirectionField= new JComboBox();
		startingDirectionField.addItem("N");
		startingDirectionField.addItem("S");
		
		degreesField= new JSlider(JSlider.HORIZONTAL, 0, 179, 60);
		degreesField.addChangeListener(this);
		degreesField.setMajorTickSpacing(20);
		degreesField.setMinorTickSpacing(5);
		degreesField.setPaintTicks(true);
		degreesField.setPaintLabels(true);
		
		feetField= new JSlider(JSlider.HORIZONTAL, 0, 59, 0);
		feetField.addChangeListener(this);
		feetField.setMajorTickSpacing(10);
		feetField.setMinorTickSpacing(1);
		feetField.setPaintTicks(true);
		feetField.setPaintLabels(true);
		
		facingDirectionField= new JComboBox();
		facingDirectionField.addItem("E");
		facingDirectionField.addItem("W");
		
		distanceField= new JTextField();
		distanceField.setInputVerifier(new DistanceVerifier());

		startingDirectionLabel= new JLabel("Starting Direction: ");
		degreesLabel= new JLabel("Degrees: ");
		degreesLabelExact= new JLabel("0");
		feetLabel= new JLabel("Feet: ");
		feetLabelExact= new JLabel("0");
		facingDirectionLabel= new JLabel("Facing Direction: ");
		distanceLabel= new JLabel("Distance traveled (CHAINS)");

		panel= new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill=GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.gridwidth=2;
		gbc.gridx=0;
		gbc.gridy=0;
		panel.add(startingDirectionLabel, gbc);
		gbc.gridx=2;
		panel.add(startingDirectionField, gbc);
		gbc.gridx=0;
		gbc.gridy=3;
		panel.add(facingDirectionLabel, gbc);
		gbc.gridx=2;
		panel.add(facingDirectionField, gbc);
		gbc.gridx=3;
		gbc.gridy=1;
		panel.add(degreesField, gbc);
		gbc.gridy=2;
		panel.add(feetField, gbc);
		gbc.gridwidth=1;
		gbc.gridx=0;
		gbc.gridy=1;
		panel.add(degreesLabel, gbc);
		gbc.gridx=1;
		panel.add(degreesLabelExact, gbc);
		gbc.gridx=0;
		gbc.gridy=2;
		panel.add(feetLabel, gbc);
		gbc.gridx=1;
		panel.add(feetLabelExact, gbc);
		gbc.gridwidth=2;
		gbc.gridx=0;
		gbc.gridy=4;
		panel.add(distanceLabel, gbc);
		gbc.gridx=2;
		panel.add(distanceField, gbc);
	
		cp = getContentPane();
		cp.add(panel, BorderLayout.CENTER);
	}
	void setupMainDialog()
	{
		Toolkit         tk;
		Dimension        d;
		tk = Toolkit.getDefaultToolkit();
		d = tk.getScreenSize();
		setSize(d.width/3, d.height/3);
		setLocation(d.width/3, d.height/3);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	public void actionPerformed(ActionEvent e)
	{
		if(e.getActionCommand().equals("REPLACE"))
		{
			if(verifyDegrees())
			{
				if(save(1))
				{
					
					setVisible(false);
					dispose();
				}
			}
		}
		if(e.getActionCommand().equals("CANCEL"))
		{
			setVisible(false);
			dispose();
		}
		if(e.getActionCommand().equals("ADD"))
		{
			if(verifyDegrees())
				save(0);	
		}
		if(e.getActionCommand().equals("CLOSE"))
		{
			if(verifyDegrees())
			{
				if(save(0))
				{
					setVisible(false);
					dispose();
				}
			}
		}
	}
	public void stateChanged(ChangeEvent ce)
	{
		if(degreesField==ce.getSource())
			degreesLabelExact.setText(String.valueOf(degreesField.getValue()));
		if(feetField==ce.getSource())
			feetLabelExact.setText(String.valueOf(feetField.getValue()));
	}
	public boolean save(int i)
	{
		if(distanceField.getText().trim().equals(""))
			call = new SurveyCall(startingDirectionField.getSelectedItem().toString(), String.valueOf(degreesField.getValue()), 
					String.valueOf(feetField.getValue()), facingDirectionField.getSelectedItem().toString(), 0);
		else
			call = new SurveyCall(startingDirectionField.getSelectedItem().toString(), String.valueOf(degreesField.getValue()), 
					String.valueOf(feetField.getValue()), facingDirectionField.getSelectedItem().toString(), Integer.valueOf(distanceField.getText().trim()));
		if (i==0)
		{
			inter.addSurveyCall(call);
			return true;
		}
		else if (i==1)
		{
			inter.replaceSurveyCall(index, call);
			return true;
		}
		else
			return false;
		
	}
	public void loadFields(SurveyCall c)
	{
		startingDirectionField.setSelectedItem(c.startingDirection);
		degreesField.setValue(Integer.valueOf(c.degrees));
		feetField.setValue(Integer.valueOf(c.feet));
		facingDirectionField.setSelectedItem(c.facingDirection);
		distanceField.setText(Integer.toString(c.distance));
	}
	boolean verifyDegrees()
	{
		boolean isVerified=true;
		if(startingDirectionField.getSelectedItem().toString().equals("N"))
		{
			if(facingDirectionField.getSelectedItem().toString().equals("W"))
				if(degreesField.getValue()>=90)
				{
					JOptionPane.showMessageDialog
						(null, "If you are looking North and facing West, then you cannot exceed 89 degrees");
					isVerified=false;
				}		
		}
		if(startingDirectionField.getSelectedItem().toString().equals("S"))
		{
			if(facingDirectionField.getSelectedItem().toString().equals("E"))
				if(degreesField.getValue()>=90)
				{
					JOptionPane.showMessageDialog
						(null, "If you are looking South and facing East, then you cannot exceed 89 degrees");
					isVerified=false;
				}
		}
		return isVerified;
	}
	
}
