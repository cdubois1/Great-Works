import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JList;
import javax.swing.JPanel;
//===================================================================
//===================================================================
public class ImagePanel extends JPanel
		implements Printable
//Displays the selected image URL
{

	JList list;
	
	public ImagePanel(JList l)
	{
		this.list = l;
		setVisible(true);
		setPreferredSize(new Dimension(500,900));
		setBackground(Color.WHITE);
	}
//===================================================================
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		try {
			URL url = new URL(list.getSelectedValue().toString());
			BufferedImage bimg = ImageIO.read(url); //get the image from the domain
			g2.drawImage(bimg, 0, 0, null);
		}
		
		catch(Exception e) {
			System.out.println("error in drawing image");
		}
	}
//===================================================================
	@Override
	public int print(Graphics arg0, PageFormat arg1, int arg2) throws PrinterException {
		return 0;
	}
//===================================================================
//===================================================================
}

