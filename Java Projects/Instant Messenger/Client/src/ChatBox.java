import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.Element;
import javax.swing.JEditorPane;
import javax.swing.text.BadLocationException;
import java.io.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;

class ChatBox extends JDialog
	implements DropTargetListener, ActionListener
/* CHAT BOX FOR MESSAGING CLIENT-to-CLIENT
 * Creates a GUI for messaging using a JEditorPane to display text and a JTextArea for input text
 * Sends messages and displays them using "addMessage" method, also by CTS when receiving a message
 * Listens for file drops and initiates file transfer
 */
{
	DropTarget dt;
	JScrollPane editorScrollPane;
	JTextArea inputTextArea;
	JEditorPane chatEditorPane;
	CTS cts;
	Friend friend;
	JButton sendButton;
	File file;
	
	ChatBox(CTS cts, Friend friend){
		
		this.cts = cts;
		this.friend = friend;
		
		//Setup JEditorPane to be uneditable and display the proper HTML formatted text
		chatEditorPane = new JEditorPane();
		chatEditorPane.setEditable(false);
		chatEditorPane.setContentType("text/html");
		chatEditorPane.setText("<div align=\"center\"><font color=\"red\">You are now chatting with " + friend.username + " say hello!</div>");
		
		//set the JEditorPane as a drop target for file transfer
		dt = new DropTarget(chatEditorPane, this);
		
		//Disable "ENTER" key in the JTextArea and replace it with "SHIFT+ENTER", we need ENTER key for the "SEND" button
		inputTextArea = new JTextArea(5,50);
		inputTextArea.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "doNothing"); //disable enter key for text area
		inputTextArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.SHIFT_DOWN_MASK, true), "shift+enter"); //replace with Shift+Enter
		inputTextArea.getActionMap().put("shift+enter", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				inputTextArea.append("\n");
			}
		});
		
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		sendButton.setActionCommand("SEND");

		editorScrollPane = new JScrollPane(chatEditorPane);
		editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		editorScrollPane.setPreferredSize(new Dimension(250,150));
		editorScrollPane.setMinimumSize(new Dimension(50,50));
		
		//Set send to the default button when you press ENTER
		JRootPane rootPane = SwingUtilities.getRootPane(this); 
		rootPane.setDefaultButton(sendButton);
		
		Container cp = getContentPane();
		cp.add(sendButton, BorderLayout.PAGE_END);
		cp.add(editorScrollPane, BorderLayout.NORTH);
		cp.add(inputTextArea);
		
		setupMainFrame();
	}
//======================================================================================================================================================
	void setupMainFrame() {
		//simple copy/paste display setup using Toolkit
		
		Toolkit tk;
		Dimension d;
			
		tk= Toolkit.getDefaultToolkit();
		d= tk.getScreenSize();
		setSize(300,300);
		setLocation(d.width/3, d.height/6);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			
		setTitle("Chatting with " + friend.username);
		setResizable(false);
		setVisible(true);
	}
//======================================================================================================================================================
	void addMessage(String message, String color, String alignment) {
		//adds messages sent and received to the JEditorPane
		//users the color and alignment to associate between sent and received messages
		
		try {
			
			String msg = "<div align=\"" + alignment + "\"><font color=\"" + color + "\">" + message + "</div>";
			
			//get the document of the JEditorPane
			HTMLDocument doc = (HTMLDocument) chatEditorPane.getDocument();
			Element html = doc.getRootElements()[0];
			Element body = html.getElement(1);
			
			//place new message at the bottom of the pane
			doc.insertBeforeEnd(body, msg);
			
			//align scrollpane with the bottom most message
			int yHeight = editorScrollPane.getMaximumSize().height;
			editorScrollPane.getVerticalScrollBar().setValue(yHeight);
		
		} catch (IOException e) {
			System.out.println("Failed to add message to chatBox for friend " +friend.username);
			e.printStackTrace();
		
		} catch (BadLocationException e) {
			System.out.println("Failed to add message to chatBox for friend " +friend.username);
			e.printStackTrace();
		}
	}
//======================================================================================================================================================
	@Override
	public void actionPerformed(ActionEvent e) {
		//called when the SEND key is pressed
		//gets the text from the JTextArea, ensuring again that there are no new line characters, then sends it to the recipient
		//formats the text correctly and calls addMessage to display it, then clears the text field
		
		try {
			
			String message = inputTextArea.getText().trim();
			message.replace("\n", "\0"); //robust code to remove new lines
			
			cts.sendMessage("CHAT " + friend.username + " " + message);
			addMessage(message, "blue", "right");
			
			inputTextArea.setText("");
		
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
//======================================================================================================================================================
	public void dragEnter(DropTargetDragEvent arg0) {
		//System.out.println("ENTERED"); //troubleshooting
	}
//======================================================================================================================================================
	public void dragExit(DropTargetEvent arg0) {}
//======================================================================================================================================================
	public void dragOver(DropTargetDragEvent arg0) {}
//======================================================================================================================================================
	public void drop(DropTargetDropEvent dtde) {
		//On file drop, ensures file format is supported
		//Gets file name and size
		//Initiates the file transfer
		
		try {
			
			java.util.List<File>        fileList;
			Transferable                transferableData;
			transferableData = dtde.getTransferable();

			 if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
				 	dtde.acceptDrop(DnDConstants.ACTION_COPY);
	                fileList = (java.util.List<File>) (transferableData.getTransferData(DataFlavor.javaFileListFlavor));
					
	                file = fileList.get(0);
					String fileName = file.getName();
					Long fileSize = file.length();
					
					cts.sendMessage("INITIATE_TRANSFER " + fileName + " " + fileSize + " " + friend.username);
					
					dtde.dropComplete(true);
			}
		
		}catch (UnsupportedFlavorException  ufe){
			dtde.rejectDrop();
			System.out.println("Unsupported flavor found!");
			ufe.printStackTrace();
		
		}catch (IOException  ioe){
			dtde.rejectDrop();
			System.out.println("IOException found getting transferable data!");
			ioe.printStackTrace();
		
		}catch(Exception ex) {
			dtde.rejectDrop();
			ex.printStackTrace();
		}
	}
//=======================================================================================================================================================
	public void dropActionChanged(DropTargetDragEvent arg0) {}
//======================================================================================================================================================
//======================================================================================================================================================
}
