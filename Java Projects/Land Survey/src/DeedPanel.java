import javax.swing.JPanel;
import java.awt.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.lang.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("serial")
public class DeedPanel extends JPanel
	implements MouseListener, Printable
{
	SurveyCall call;
	JList<SurveyCall> list;
	MyListModel lm;
	double yOffset;
	double xOffset;
	double scalar;
	Point2D.Double[] startPixels;
	Point2D.Double[] endPixels;
	Point2D.Double[] endPoints;
	Point2D.Double[] startPoints;
	double minX;
	double minY;
	double maxX;
	double maxY;
		
	public DeedPanel(JList<SurveyCall> l)
	{
		this.list = l;
		setVisible(true);
		setPreferredSize(new Dimension(500,900));
		setBackground(Color.WHITE);
	}
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		lm= (MyListModel)list.getModel();
		Point2D.Double[] startPoints= lm.getStartPoints();
		Point2D.Double[] endPoints= lm.getEndPoints(startPoints);
		double[] minMax= lm.getMinMax();
		minX= minMax[0];
		minY= minMax[1];
		maxX= minMax[2];
		maxY= minMax[3];
		double panelDX= getWidth();
		double panelDY= getHeight();
		double surveyDX= maxX-minX;
		double surveyDY= maxY-minY;
		System.out.println("survey dx: " + surveyDX + " " + "survey dy: " + surveyDY);
		if((panelDX/surveyDX) > (panelDY/surveyDY))
		{
			yOffset=0;
			scalar= panelDY/surveyDY;
			xOffset=(panelDX-surveyDX*scalar)/2;
		}
		else if ((panelDX/surveyDX) < (panelDY/surveyDY))
		{
			xOffset=0;
			scalar= panelDX/surveyDX;
			yOffset=(panelDY-surveyDY*scalar)/2;
		}
		startPixels = new Point2D.Double[lm.getSize()];
		endPixels = new Point2D.Double[lm.getSize()];
		for(int i=0; i<lm.getSize(); i++)
		{
			startPixels[i]= new Point2D.Double(getX1Pixel(i, startPoints), getY1Pixel(i, startPoints));
			endPixels[i]= new Point2D.Double(getX2Pixel(i, endPoints), getY2Pixel(i, endPoints));
		}
		g2.rotate(1);
		for(int j=0; j<lm.getSize(); j++)
			g2.drawLine((int)startPixels[j].getX(), (int)startPixels[j].getY(), 
					(int)endPixels[j].getX(), (int)endPixels[j].getY());
	}
	double getX1Pixel(int i, Point2D.Double[] sp)
	{
		double x1Pixel= (sp[i].getX() - minX)*scalar + xOffset;
		return x1Pixel;
	}
	double getX2Pixel(int i, Point2D.Double[] ep)
	{
		
		double x2Pixel= (ep[i].getX() - minX)*scalar + xOffset;
		return x2Pixel;
	}
	double getY1Pixel(int i, Point2D.Double[] sp)
	{
		double y1Pixel= (sp[i].getY() - minY)*scalar + yOffset;
		return y1Pixel;
	}
	double getY2Pixel(int i, Point2D.Double[] ep)
	{
		double y2Pixel= (ep[i].getY() - minY)*scalar + yOffset;
		return y2Pixel;
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) throws PrinterException 
	{
		Graphics g2= (Graphics2D) g;
		if(pageIndex > 0)
			return java.awt.print.Printable.NO_SUCH_PAGE;
		g2.translate((int)pf.getImageableX(), (int)pf.getImageableY());
		this.paint(g2);
		return java.awt.print.Printable.PAGE_EXISTS;
	}
	
}
