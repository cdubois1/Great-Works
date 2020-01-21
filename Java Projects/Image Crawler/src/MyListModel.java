import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.text.html.parser.ParserDelegator;
//===================================================================
//===================================================================
public class MyListModel extends DefaultListModel
/* Populate the list with URL's
 * This class takes a given URl, either from a string or file, and sends it to
 * the ParserDelegator to be thoroughly parsed for images
 */
{
	
	ImagePanel ip;
	URL url;
	URLConnection urlConnection;
	InputStreamReader isr;
	MyParserCallbackTagHandler tagHandler;
	String domain;
	
	MyListModel(ImagePanel ip){
		this.ip = ip;
	}
//===================================================================
	void parseHTML(){
		//attempts to access the URL provided and then parse for images
		
		//---------HTML Setup-----------------------------------
		domain = JOptionPane.showInputDialog("Input the URL: ");
		
		if(!domain.startsWith("http"))
			JOptionPane.showMessageDialog(null, "Please enter a valid URL");
		
		else {
			try {
				
				domain.replace(" ", "");
				url = new URL(domain);
				urlConnection = url.openConnection();
				isr = new InputStreamReader(urlConnection.getInputStream());
				
				tagHandler = new MyParserCallbackTagHandler(domain, this);
					
				new ParserDelegator().parse(isr, tagHandler, true);
				
			} catch(MalformedURLException mue) {
				mue.printStackTrace();
			
			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}
//===================================================================
	void parseDropTarget(File file){
		//takes the HTML file dropped and attempts to parse it for images
		
		this.clear();
		
		//---------HTML Setup-----------------------------------
		domain = file.toURI().toString(); //Will this give me back the URL?
		
		if(!domain.startsWith("http"))
			JOptionPane.showMessageDialog(null, "The URL from the drop target is not valid");
		
		else {
			try {
				
				url = new URL(domain);
				urlConnection = url.openConnection();
				isr = new InputStreamReader(urlConnection.getInputStream());
				
				tagHandler = new MyParserCallbackTagHandler(domain, this);
					
				new ParserDelegator().parse(isr, tagHandler, true);
			
			} catch(MalformedURLException mue) {
				System.out.println("TKL: Bad URL");
				mue.printStackTrace();
			
			} catch(IOException ioe) {
				System.out.println("TKL: Crazy IO Exception of some kind");
				ioe.printStackTrace();
			}
		}
	}
//===================================================================
//===================================================================
}
