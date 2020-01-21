import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
//===================================================================
//===================================================================
public class MyParserCallbackTagHandler extends HTMLEditorKit.ParserCallback
/* Retrieve Image URL's
 * searches the HTML page for image tags
 * if the page uses an HREF it will automatically add it to the image URL
 * Adds url's to the list model
 */
{
	String baseDomain;
	MyListModel model;

//===================================================================
	public MyParserCallbackTagHandler(String domain, MyListModel model) {
		this.baseDomain = domain;
		this.model = model;
	}
//===================================================================
	public void handleSimpleTag(HTML.Tag tag, MutableAttributeSet attSet, int pos) {
		
		Object attribute;
		String baseRef=null;
		String fullDomain;
		
		if(tag==HTML.Tag.BASE) { //if there is a base ref override 
			baseRef = attSet.getAttribute(HTML.Attribute.HREF).toString();
			
			if(!baseRef.endsWith("/"))
				baseRef = baseRef + "/"; //ensure it has the end slash
		}
		
		if(tag==HTML.Tag.IMG) { //if this is an image
			attribute = attSet.getAttribute(HTML.Attribute.SRC);
			
			if(attribute!= null) {
				if(attribute.toString().startsWith("http")){ //check if it is a full image link
					model.addElement(attribute.toString());
				}
				
				else if(attribute.toString().contains("/")) {
					fullDomain = baseDomain + attribute.toString(); //check if it is a relative to HTML domain
					model.addElement(fullDomain);
				}
				
				else {
					if(baseRef!=null)
						fullDomain = baseRef + attribute.toString(); //it must be relative to a base ref
					else
						fullDomain = baseDomain + attribute.toString();
					
					model.addElement(fullDomain);
				}
			}
		}
	}
//===================================================================
//===================================================================
}
