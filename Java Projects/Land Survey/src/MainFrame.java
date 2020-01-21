import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.lang.*;
import java.util.Hashtable;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.awt.print.*;
import java.io.File;
import java.io.IOException;

//===================================================================

public class MainFrame extends JFrame
			implements ActionListener, ChangeListener, ListSelectionListener,
						MouseListener, DropTargetListener
{
	public static int DISPLAY_MODE;
	JButton addCall;
	JButton loadDeed;
	JButton saveDeed;
	JButton editCall;
	JButton deleteCall;
	JButton printPlot;
	JButton exitButton;
	JSlider displaySlider;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem menuItem;
	JFileChooser chooser;
	File file;
	JList<SurveyCall> list;
	MyListModel model;
	JScrollPane listScroller;
	DeedPanel dp;
	Container cp;
	SurveyCall call;
	DropTarget dt;
//===================================================================
	MainFrame()
	{
		
	//--------Initial Setup-------------------------------------------
		chooser = new JFileChooser(".");
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("*.txt", "txt"));
		file = null;
		DISPLAY_MODE=1;
		
	//---------Setup for List----------------------------------------
		list = new JList<SurveyCall>();
		dp= new DeedPanel(list);
		model = new MyListModel(this);
		list.setModel(model);
		list.setVisible(true);
		list.addListSelectionListener(this);
		list.addMouseListener(this);
		list.setLayoutOrientation(JList.VERTICAL);
		list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		list.setVisibleRowCount(-1);
		listScroller = new JScrollPane(list);
		listScroller.setPreferredSize(new Dimension(300, 800));
		dt= new DropTarget(listScroller, this);

	//---------Prep for Buttons---------------------------------------
		addCall = new JButton("Add a Call");
		addCall.addActionListener(this);
		addCall.setActionCommand("ADD");
		
		loadDeed = new JButton("Load a Deed");
		loadDeed.addActionListener(this);
		loadDeed.setActionCommand("LOAD");
		
		saveDeed = new JButton("Save Current Deed");
		saveDeed.addActionListener(this);
		saveDeed.setActionCommand("SAVE");
		
		editCall = new JButton("Edit Selected");
		editCall.addActionListener(this);
		editCall.setActionCommand("EDIT");
		editCall.setEnabled(false);
		
		deleteCall = new JButton("Delete Selected");
		deleteCall.addActionListener(this);
		deleteCall.setActionCommand("DELETE");
		deleteCall.setEnabled(false);
		
		printPlot = new JButton("Print Current Plot");
		printPlot.addActionListener(this);
		printPlot.setActionCommand("PRINT");
		
		exitButton = new JButton("EXIT");
		exitButton.addActionListener(this);
		exitButton.setActionCommand("EXIT");
		
	//--------Setup Button Panel and Layout----------------------------------------
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill=GridBagConstraints.HORIZONTAL;
		c.gridx=0;
		c.gridy=1;
		buttonPanel.add(loadDeed, c);
		c.gridx=1;
		c.gridy=1;
		buttonPanel.add(editCall, c);
		c.gridx=2;
		c.gridy=1;
		buttonPanel.add(deleteCall, c);
		c.gridx=0;
		c.gridy=2;
		buttonPanel.add(saveDeed, c);
		c.gridwidth= 2;
		c.gridx=1;
		c.gridy=2;
		buttonPanel.add(printPlot, c);
		c.gridwidth= 3;
		c.gridx=0;
		c.gridy=3;
		buttonPanel.add(exitButton, c);
		c.gridx=0;
		c.gridy=0;
		buttonPanel.add(addCall, c);
		
	//---------Setup for Display Slider and Labels-------------------------------
		displaySlider = new JSlider(JSlider.HORIZONTAL, 1, 3, 1);
		displaySlider.setVisible(true);
		displaySlider.addChangeListener(this);
		displaySlider.setMajorTickSpacing(1);
		Hashtable lbl= new Hashtable();
		lbl.put(1, new JLabel("Degrees"));
		lbl.put(2, new JLabel("Radians"));
		lbl.put(3, new JLabel("DX, DY"));
		displaySlider.setLabelTable(lbl);
		displaySlider.setPaintLabels(true);
		
		JLabel displayLabel = new JLabel("Choose the Display Mode");
		
		JPanel otherPanel = new JPanel();
		otherPanel.setLayout(new BoxLayout(otherPanel, BoxLayout.Y_AXIS));
		otherPanel.add(displayLabel);
		otherPanel.add(displaySlider);
		
	//---------Create the Menu Bar-----------------------------------------------
		menuBar = new JMenuBar();
		
		menu = new JMenu ("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.setToolTipText("Funtions for loading/saving to file");
		menuItem = new JMenuItem("Load a Deed");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("LOAD");
		menuItem.setMnemonic(KeyEvent.VK_L);
		menuItem.setToolTipText("This will load a list of calls from the selected File");
		menu.add(menuItem);
		menuItem = new JMenuItem("Save Current Deed");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("SAVE");
		menuItem.setMnemonic(KeyEvent.VK_S);
		menuItem.setToolTipText("This will save all the calls in the list to the selected File");
		menu.add(menuItem);
		menuBar.add(menu);
		
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_E);
		menu.setToolTipText("Functions for editing and deleting calls");
		menuItem = new JMenuItem("Edit the Selected Call");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("EDIT");
		menuItem.setMnemonic(KeyEvent.VK_C);
		menuItem.setToolTipText("Allows field by field alteration of the call");
		menu.add(menuItem);
		menuItem = new JMenuItem("Delete the Selected Deed");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("DELETE");
		menuItem.setMnemonic(KeyEvent.VK_D);
		menuItem.setToolTipText("This will delete the data for the call and remove it from the list");
		menu.add(menuItem);
		menuBar.add(menu);
		
		menu = new JMenu("Print");
		menu.setMnemonic(KeyEvent.VK_P);
		menu.setToolTipText("Function for printing");
		menuItem = new JMenuItem("Print Current Call");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("PRINT");
		menuItem.setMnemonic(KeyEvent.VK_I);
		menuItem.setToolTipText("Prints the current plotted Deed");
		menu.add(menuItem);
		menuBar.add(menu);
		
		menu = new JMenu("Dump Data");
		menu.setToolTipText("For ADMIN use only");
		menuItem = new JMenuItem("dump angle degrees");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("DEGREES");
		menu.add(menuItem);
		menuItem = new JMenuItem("dump angle radians");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("RADIANS");
		menu.add(menuItem);
		menuItem = new JMenuItem("dump DX");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("DX");
		menu.add(menuItem);
		menuItem = new JMenuItem("dump DY");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("DY");
		menu.add(menuItem);
		menuBar.add(menu);
		
		menu = new JMenu("Quit");
		menu.setMnemonic(KeyEvent.VK_X);
		menu.setToolTipText("Exit the Program");
		menuItem = new JMenuItem("EXIT");
		menuItem.addActionListener(this);
		menuItem.setActionCommand("EXIT");
		menuItem.setMnemonic(KeyEvent.VK_T);
		menu.add(menuItem);
		menuBar.add(menu);
		
	//-----------Setup the Content pane-----------------------------------
		this.getRootPane().setDefaultButton(exitButton);
		cp= getContentPane();
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(otherPanel);
		bottomPanel.add(buttonPanel);
		
		cp.add(dp, BorderLayout.CENTER);
		cp.add(bottomPanel, BorderLayout.SOUTH);
		cp.add(listScroller, BorderLayout.EAST);
		setJMenuBar(menuBar);
		setupMainFrame();
	}
//===================================================================
	void setupMainFrame()
	{
	Toolkit tk;
	Dimension d;

	tk= Toolkit.getDefaultToolkit();
	d= tk.getScreenSize();
	setSize(d.width/2, d.height/2);
	setLocation(d.width/5, d.height/6);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	setTitle("Picture Puzzle");
	setVisible(true);
	}
//===================================================================
	public void actionPerformed(ActionEvent e)
	{
		int numCalls = model.getSize();
		if(e.getActionCommand().equals("ADD"))
			new InputDialog(model);
		if (e.getActionCommand().equals("LOAD"))
			loadDeed();
		if (e.getActionCommand().equals("SAVE"))
			saveDeed();
		if (e.getActionCommand().equals("EDIT"))
		{
			if(!list.isSelectionEmpty())
				new InputDialog(model, list.getSelectedValue(), list.getSelectedIndex());
		}
		if (e.getActionCommand().equals("DELETE"))
		{
			if(!list.isSelectionEmpty())
				deleteCall();
		}
		if (e.getActionCommand().equals("PRINT"))
		{
			try{
			doPrint();
			}
			catch(PrinterException pe){
				System.out.println("Failed to print");
			}
		}
		if (e.getActionCommand().equals("DEGREES"))
		{
			for(int i=0; i<numCalls; i++)
			{
				call=model.getElementAt(i);
				System.out.println(call.getAngleDegrees());
			}
		}
		if (e.getActionCommand().equals("RADIANS"))
		{
			for(int i=0; i<numCalls; i++)
			{
				call=model.getElementAt(i);
				System.out.println(call.getAngleRadians());
			}
		}
		if (e.getActionCommand().equals("DY"))
		{
			for(int i=0; i<numCalls; i++)
			{
				call=model.getElementAt(i);
				System.out.println(call.getDeltaX());
			}
		}
		if (e.getActionCommand().equals("DX"))
		{
			for(int i=0; i<numCalls; i++)
			{
				call=model.getElementAt(i);
				System.out.println(call.getDeltaY());
			}
		}
		if (e.getActionCommand().equals("EXIT"))
			Runtime.getRuntime().exit(0);
		list.repaint();
		dp.repaint();
	}
//===================================================================
	public void stateChanged(ChangeEvent ce)
	{
		if(displaySlider.getValue()== 1)
			DISPLAY_MODE= 1;
		if(displaySlider.getValue()== 2)
			DISPLAY_MODE= 2;
		if(displaySlider.getValue()== 3)
			DISPLAY_MODE=3;
		list.repaint();
	}
//===================================================================
	public void valueChanged(ListSelectionEvent lse)
	{
		if(!list.isSelectionEmpty())
		{
			editCall.setEnabled(true);
			deleteCall.setEnabled(true);
		}
		else
		{
			editCall.setEnabled(false);
			deleteCall.setEnabled(false);
		}
		dp.repaint();
	}
//===================================================================	
	void loadDeed()
	{
		try
		{
			int response= chooser.showOpenDialog(this);
			if (response== JOptionPane.YES_OPTION)
			{
				file= chooser.getSelectedFile();
				model.loadDeed(file);
			}
			else
				JOptionPane.showMessageDialog(null, "Nothing was loaded", "No Data", JOptionPane.ERROR_MESSAGE);
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog
				(null, "You tried to do something you shouldn't! I cannot load that type of file", "BAD DATA", JOptionPane.ERROR_MESSAGE);
		}
	}
//===================================================================
	void saveDeed()
	{
		if(list.getModel().getSize()== 0)
			JOptionPane.showMessageDialog(null, "There are no calls to save", "Error", JOptionPane.ERROR_MESSAGE);
		else if (list.getModel().getSize()>0)
		{	
			try
			{
				int response= chooser.showSaveDialog(this);
				if(response== JOptionPane.YES_OPTION)
				{
					file = chooser.getSelectedFile();
					model.saveDeed(file);
				}
				else
					JOptionPane.showMessageDialog
						(null, "Deed not saved", "warning", JOptionPane.ERROR_MESSAGE);
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(null, "Could not save that file, sorry", "warning", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
//===================================================================
	void deleteCall()
	{
		int response = JOptionPane.showConfirmDialog
				(null, "Are you sure you want to delete this call?", "Warning", JOptionPane.YES_NO_OPTION);
		if(response == JOptionPane.YES_OPTION)
			model.remove(list.getSelectedIndex());
		else
			JOptionPane.showMessageDialog
				(null, "Delete canceled", "DONT WORRY", JOptionPane.INFORMATION_MESSAGE);
	}
	void print()
	{
		
	}
//===================================================================

	@Override
	public void dragEnter(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragExit(DropTargetEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dragOver(DropTargetDragEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void drop(DropTargetDropEvent dtde) 
	{
		java.util.List<File>        fileList;
		Transferable                transferableData;
		transferableData = dtde.getTransferable();
		try
			{
			if (transferableData.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
			{
				dtde.acceptDrop(DnDConstants.ACTION_COPY);
				fileList = (java.util.List<File>)(transferableData.getTransferData(DataFlavor.javaFileListFlavor));
							file = fileList.get(0);
							model.loadDeed(file);
							dp.repaint();
			}
			else
				System.out.println("File list flavor not supported.");
			}
		catch (UnsupportedFlavorException  ufe)
			{
			System.out.println("Unsupported flavor found!");
			ufe.printStackTrace();
			}
		catch (IOException  ioe)
			{
			System.out.println("IOException found getting transferable data!");
			ioe.printStackTrace();
			}
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) 
	{
		return;
	}
	public void mouseClicked(MouseEvent me) 
	{
		return;
	}
	public void mouseEntered(MouseEvent me) 
	{
		return;
	}
	public void mouseExited(MouseEvent me) 
	{
		return;
	}
	public void mousePressed(MouseEvent me) 
	{
		return;
	}
	public void mouseReleased(MouseEvent me) 
	{
		if(me.getClickCount() == 2)
		{
			new InputDialog(model, list.getSelectedValue(), list.getSelectedIndex()); 
		}
	}
	void doPrint() throws PrinterException
	{
		PrinterJob pj;
		PageFormat pageFormat;
		
		pj= PrinterJob.getPrinterJob();
		pageFormat= pj.pageDialog(pj.defaultPage());
		pj.setPrintable(dp, pageFormat);
		if(pj.printDialog())
			pj.print();
	}
}























