import java.awt.List;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class MyListModel extends DefaultListModel<SurveyCall>
			implements DataManager
{
	SurveyCall call;
	DataOutputStream dops;
	DataInputStream dips;
	int len;
	public Point2D.Double[] startPoints;
	public Point2D.Double[] endPoints;
	DeedPanel dp;
	
	MyListModel(MainFrame f)
	{
		this.dp= f.dp;
	}
	
	public void loadDeed(File file)
	{
		this.clear();
		dips = null;
		try 
		{
			dips = new DataInputStream(new FileInputStream(file));
			len = dips.readInt();
			System.out.println(len);
			for (int i = 0; i < len; i++) 
			{	
				this.addElement(new SurveyCall(dips));
				call = null; 
			}
		}
		catch(IOException e) 
		{
			JOptionPane.showMessageDialog(null, "Error: Could not read file", "alert", JOptionPane.ERROR_MESSAGE);
		}
		
	}
	public void saveDeed(File file)
	{
		dops= null;
		try 
		{
			dops = new DataOutputStream(new FileOutputStream(file));
			int len = this.getSize();
			dops.writeInt(len);
			for (int i = 0; i < len; i++) 
			{
				this.getElementAt(i).saveToFile(dops);
			}
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Error: Could not write to file", "alert", JOptionPane.ERROR_MESSAGE);
		}
	}
	public Point2D.Double[] getStartPoints()
	{
		System.out.println(getSize());
		startPoints= new Point2D.Double[getSize()];
		for(int i=0; i< getSize(); i++)
			startPoints[i]= new Point2D.Double(0,0);
		for(int j=1; j< getSize(); j++)
		{
			startPoints[j].x= startPoints[j-1].getX() + getElementAt(j).getDeltaX();
			startPoints[j].y= startPoints[j-1].getY() + getElementAt(j).getDeltaY();
		}
		return startPoints;
	}
	public Point2D.Double[] getEndPoints(Point2D.Double[] startPoints)
	{
		endPoints= new Point2D.Double[getSize()];
		for(int i=0; i< getSize(); i++)
			endPoints[i]= new Point2D.Double();
		if(getSize()==1)
		{
			endPoints[0].x=getElementAt(0).getDeltaX();
			endPoints[0].y=getElementAt(0).getDeltaY();
		}
		else
		{
			for(int j=0; j<getSize()-1; j++)
			{
				if (j!=getSize()-2)
				{
					endPoints[j].x=startPoints[j+1].getX();
					endPoints[j].y=startPoints[j+1].getY();
				}
				else
					endPoints[j].x= startPoints[j].getX() + getElementAt(j).getDeltaX();
					endPoints[j].y= startPoints[j].getY() + getElementAt(j).getDeltaY();
			}
		}
		return endPoints;
	}
	public double[] getMinMax()
	{
		double tempX;
		double tempY;
		double minX=0.0;
		double minY=0.0;
		double maxX=0.0;
		double maxY=0.0;
		startPoints= getStartPoints();
		endPoints= getEndPoints(startPoints);
		for(int i=0; i<getSize(); i++)
		{
			tempX= startPoints[i].getX();
			tempY= startPoints[i].getY();
			if(tempX<minX)
				minX=tempX;
			if(tempX>maxX)
				maxX=tempX;
			if(tempY<minY)
				minY=tempY;
			if(tempY>maxY)
				maxY=tempY;
			tempX= endPoints[i].getX();
			tempY= endPoints[i].getY();
			if(tempX<minX)
				minX=tempX;
			if(tempX>maxX)
				maxX=tempX;
			if(tempY<minY)
				minY=tempY;
			if(tempY>maxY)
				maxY=tempY;
		}
		double[] minMax={minX, minY, maxX, maxY};
		System.out.println("min x,y: " + minX + "," + minY + "  "+ "max x,y: " + maxX + "," + maxY);	
		return minMax;
	}
	public void addSurveyCall(SurveyCall c)
	{
		this.addElement(c);
		dp.repaint();
	}
	public void replaceSurveyCall(int index, SurveyCall c)
	{
		this.removeElementAt(index);
		this.add(index, c);
		dp.repaint();
	}
}
