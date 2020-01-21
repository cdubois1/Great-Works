import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import javax.swing.JOptionPane;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.lang.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.lang.Object;

public class SurveyCall 
{
	String startingDirection;
	String degrees;
	String feet;
	String facingDirection;
	int distance;
	
	SurveyCall()
	{}
	
	SurveyCall(String startingDirection, String degrees, 
			String feet, String facingDirection, int distance)
	{
		this.startingDirection= startingDirection;
		this.degrees= degrees;
		this.feet= feet;
		this.facingDirection= facingDirection;
		this.distance= distance;
	}
	SurveyCall(DataInputStream dips)
	{
		try
		{
			this.startingDirection= dips.readUTF();
			this.degrees= dips.readUTF();
			this.feet= dips.readUTF();
			this.facingDirection= dips.readUTF();
			this.distance= dips.readInt();
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Error: failed to load specific call", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	void saveToFile(DataOutputStream dops)
	{
		try
		{
			dops.writeUTF(startingDirection);
			dops.writeUTF(degrees);
			dops.writeUTF(feet);
			dops.writeUTF(facingDirection);
			dops.writeInt(distance);
		}
		catch(IOException ioe)
		{
			JOptionPane.showMessageDialog(null, "Error: failed to write specific call", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	public String toString()
	{
		DecimalFormat df = new DecimalFormat("#.##");
		if(MainFrame.DISPLAY_MODE== 2)
			return startingDirection + " " + df.format(((Integer.valueOf(degrees)*0.0174533) + (Integer.valueOf(feet)/60)*0.0174533)) + " " + facingDirection + " " + distance;
		else if(MainFrame.DISPLAY_MODE== 3)
			return startingDirection + " " + df.format(getDeltaX()) + " " + df.format(getDeltaY()) + " " + facingDirection + " " + distance;
		else //DISPLAY_MODE== 1
			return startingDirection + " " + degrees + " "+ feet + " " + facingDirection + " " + distance;
	}
	public void draw(Graphics g)
	{
		Graphics2D g2= (Graphics2D) g;
		g2.setColor(Color.BLACK);
		
	}
	public double getAngleDegrees()
	{
		double initialDegrees=0.00;
		DecimalFormat df = new DecimalFormat("#.##");
		if(startingDirection.equals("N"))
		{
			initialDegrees=0;
			if(facingDirection.equals("W"))
				initialDegrees=90;
		}
		if(startingDirection.equals("S"))
		{
			initialDegrees=180;
			if(facingDirection.equals("E"))
				initialDegrees=270;
		}
		initialDegrees= initialDegrees + Integer.valueOf(degrees);
		if(feet.equals("0"))
			return Double.parseDouble(df.format(initialDegrees));
		else
		{
			initialDegrees= initialDegrees + (Double.parseDouble(feet)/60);
			return Double.parseDouble(df.format(initialDegrees));
		}
	}
	public double getAngleRadians()
	{
		double angle = getAngleDegrees();
		DecimalFormat df = new DecimalFormat("#.##");
		angle = angle * 0.0174533;
		return Double.parseDouble(df.format(angle));
	}
	public double getDeltaX()
	{
		double dX=0.00;
		dX= distance * Math.cos(getAngleRadians());
		System.out.println("delta x: " + dX);
		return dX;
	}
	public double getDeltaY()
	{
		double dY=0.00;
		dY= distance * Math.sin(getAngleRadians());
		System.out.println("delta y: " + dY);
		return dY;
	}
	
	
	
	
	
	
	
	
	
	
	
}
