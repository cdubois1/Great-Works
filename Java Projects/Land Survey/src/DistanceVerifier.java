import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class DistanceVerifier extends InputVerifier
{
	int d;
	public boolean verify(JComponent input)
	{
		boolean isValid;
		String str;
		str= ((JTextField)input).getText().trim(); 
		if (str== " ")
			isValid=true;
		else
		{
			try
			{
				d= Integer.parseInt(str);
				if(d<0 || d>1000)
				{
					JOptionPane.showMessageDialog(input.getParent(), "Distance must be between 0 and 1000 chains","Error", JOptionPane.ERROR_MESSAGE);
					isValid = false;
				}
				else
					isValid = true;
			}
			catch(NumberFormatException nfe)
			{
				JOptionPane.showMessageDialog(input.getParent(), "Distance must be a numeral and greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
				isValid = false;
			}
		}
		return isValid;
	}
}
