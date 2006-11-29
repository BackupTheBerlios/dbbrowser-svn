package us.pcsw.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;

import java.awt.event.WindowEvent;

import java.net.URL;

/**
 * us.pcsw.swing.HelpDialog
 * -
 * The display dialog for applications' help file(s).  Help files are expected
 * to be HTML files.  JEditorPane is used to render the help files, so only
 * basic pages are recommended.  Basic browsing capability is provided for
 * moving back one page, forward one page, or to home.
 *
 * <P>Following is example code for showing an application's license from an
 * HTML page stored in the application's classpath or jar file:
 * <pre>
 *	try {
 *	    URL licenseURL = getClass().getResource("resources/license.html");
 *	    HelpDialog hd =
 *		new HelpDialog(this, false, licenseURL, "About My App");
 *	    hd.show();
 *	} catch (java.lang.Throwable e) {
 *	    handleException(e);
 *	}
 * <pre>
 *
 * <P>Revision History:
 * <BR>06/23/2001 This class was created as a member of the dbbrowser package.
 * <BR>07/03/2001 This class was moved to the pac.swing package for future
 *                inclusion in other applications.
 *
 * @author Philip A. Chapman
 */
public class HTMLDialog
    extends javax.swing.JDialog
    implements
		HTMLPanelParent,
		java.awt.event.WindowListener
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	private URL homeURL;
    private HTMLPanel htmlPanel;

    /**
     * The constructor for the class.
     * @param owner The Dialog (most likely a JDialog) which is the owner.
     * @param modal True if the dialog is to be modal.
     * @param homeURL The home page at which the help dialog is to begin and
     *                return to if the Home button is clicked.
     */
    public HTMLDialog(Dialog owner, boolean modal, URL homeURL, String caption)
    {
    	super(owner, caption, modal);
    	initGUI(homeURL, caption);
    }

    /**
     * The constructor for the class.
     * @param owner The Frame (most likely a JFrame) which is the owner.
     * @param modal True if the dialog is to be modal.
     * @param homeURL The home page at which the help dialog is to begin and
     *                return to if the Home button is clicked.
     */
    public HTMLDialog(Frame owner, boolean modal, URL homeURL, String caption)
    {
    	super(owner, caption, modal);
    	initGUI(homeURL, caption);
    }
    
    private void initGUI(URL homeURL, String caption)
    {
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	this.homeURL = homeURL;
	addWindowListener(this);

	// Layout of overall dialog frame
	Container cp = getContentPane();
	cp.setLayout(new BorderLayout());

	htmlPanel = new HTMLPanel(this, homeURL);
	cp.add(htmlPanel, BorderLayout.CENTER);
	
	// Size and Center the dialog
	Dimension mySize = new Dimension(550, 600);
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension halfScreenSize = new Dimension(screenSize.width / 2, screenSize.height / 2);
	if (mySize.height > halfScreenSize.height) {
		mySize.height = halfScreenSize.height;
	}
	if (mySize.width > halfScreenSize.width) {
		mySize.width = halfScreenSize.width;
	}
	setBounds(halfScreenSize.width - (mySize.width / 2), 
			  halfScreenSize.height - (mySize.height / 2),
			  mySize.width, mySize.height);
    }

    public void closeButtonClicked()
    {
    	this.dispose();
    }

	public void windowActivated(WindowEvent e)
	{
		removeWindowListener(this);
		htmlPanel.setURL(homeURL);
	}
	
	public void windowClosed(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {}
	
	public void windowDeactivated(WindowEvent e) {}
	
	public void windowDeiconified(WindowEvent e) {}
	
	public void windowIconified(WindowEvent e) {}
	
	public void windowOpened(WindowEvent e) {}
}
