import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.text.*;
import java.util.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.parser.*;
import javax.swing.text.html.*;
//===================================================================
//===================================================================
public class MainFrame extends JFrame
			implements ActionListener, ListSelectionListener,
						DropTargetListener, MouseListener
/* GUI
 * This provides functionality to display the images as well as a list of image URL's
 * Receives URl from a string or a file and sends it to MyListModel
 */
{
	//SETUP
	public
		JButton exitButton;
		JButton searchButton;
		JButton clearButton;
		JFileChooser chooser;
		JList list;
		ImagePanel ip;
		JScrollPane listScroller;
		MyListModel model;
		Container cp;
		DropTarget dt;
		File file;

	MainFrame(){
	
		//---------- LIST SETUP--------------------------------
		list = new JList();
		ip = new ImagePanel(list); 
		model = new MyListModel(ip);
		list.setModel(model);
		list.addListSelectionListener(this);
		list.addMouseListener(this);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(-1);
		listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(300, 800));
		dt= new DropTarget(listScroller, this);
		
		//---------Button Setup---------------------------------
		exitButton= new JButton("EXIT");
		exitButton.addActionListener(this);
		exitButton.setActionCommand("EXIT");
		
		searchButton = new JButton("SEARCH");
		searchButton.addActionListener(this);
		searchButton.setActionCommand("SEARCH");
		
		clearButton = new JButton("CLEAR");
		clearButton.addActionListener(this);
		clearButton.setActionCommand("CLEAR");
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill=GridBagConstraints.HORIZONTAL;
		buttonPanel.add(searchButton);
		buttonPanel.add(clearButton);
		buttonPanel.add(exitButton);
		
		//-------------- Setup CP-------------------------------
		this.getRootPane().setDefaultButton(exitButton);
		cp= getContentPane();
		
		cp.add(ip, BorderLayout.CENTER);
		cp.add(buttonPanel, BorderLayout.SOUTH); 
		cp.add(listScroller, BorderLayout.EAST);
		setupMainFrame();
	}
//===================================================================
	void setupMainFrame(){
	
		Toolkit tk;
		Dimension d;
		
		tk= Toolkit.getDefaultToolkit();
		d= tk.getScreenSize();
		setSize(d.width/2, d.height/2);
		setLocation(d.width/5, d.height/6);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("PICTURE CRAWLER");
		setVisible(true);
	}
//===================================================================
	@Override
	public void dragEnter(DropTargetDragEvent arg0) {}
//===================================================================
	@Override
	public void dragExit(DropTargetEvent arg0) {}
//===================================================================
	@Override
	public void dragOver(DropTargetDragEvent arg0) {}
//===================================================================
	@Override
	public void drop(DropTargetDropEvent dtde) {
		//Ensure flavor is supported, then pass the HTML file to MyListModel for parsing
		
		java.util.List<File>        fileList;
		Transferable                transferableData;
		transferableData = dtde.getTransferable();
		
		try{
			
			if (transferableData.isDataFlavorSupported(DataFlavor.allHtmlFlavor)){
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.allHtmlFlavor));
							file = fileList.get(0);
							model.parseDropTarget(file); 
			}
			else
				System.out.println("File list flavor not supported.");
		}
		
		catch (UnsupportedFlavorException  ufe){
			System.out.println("Unsupported flavor found!");
			ufe.printStackTrace();
		}
		
		catch (IOException  ioe){
			System.out.println("IOException found getting transferable data!");
			ioe.printStackTrace();
		}
	}
//===================================================================
	@Override
	public void dropActionChanged(DropTargetDragEvent arg0) {}
//===================================================================
	@Override
	public void valueChanged(ListSelectionEvent lse) {
		ip.repaint();
	}
//===================================================================
	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Get the URL string and pass it to MyListModel for parsing
		if(e.getActionCommand().equals("SEARCH")) {
			model.parseHTML();
			ip.repaint();
		}
		
		if (e.getActionCommand().equals("CLEAR"))
			model.clear();
		
		if (e.getActionCommand().equals("EXIT"))
			Runtime.getRuntime().exit(0);
		
	}
//===================================================================
	@Override
	public void mouseClicked(MouseEvent arg0) {}
//===================================================================
	@Override
	public void mouseEntered(MouseEvent arg0) {}
//===================================================================
	@Override
	public void mouseExited(MouseEvent arg0) {}
//===================================================================
	@Override
	public void mousePressed(MouseEvent arg0) {}
//===================================================================
	@Override
	public void mouseReleased(MouseEvent me) {
		
		if(me.getClickCount() == 2){
			ip.repaint();
		}
	}
//===================================================================
//===================================================================
}
