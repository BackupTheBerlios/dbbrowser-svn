package us.pcsw.swing;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Frame;

import java.awt.event.ActionEvent;

import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import javax.swing.event.HyperlinkEvent;

import us.pcsw.util.BrowserLauncher;

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
public class HTMLPanel
    extends JPanel
    implements
		java.awt.event.ActionListener,
	    javax.swing.event.HyperlinkListener
{
	// CONSTANTS
	
	private static final long serialVersionUID = 1L;

	private static final String BACK_ICON = "/us/pcsw/dbbrowser/resources/images/Back24.gif";
	private static final String FORWARD_ICON = "/us/pcsw/dbbrowser/resources/images/Forward24.gif";
	private static final String HOME_ICON = "/us/pcsw/dbbrowser/resources/images/Home24.gif";
	private static final String LOGO_ICON = "/us/pcsw/dbbrowser/resources/images/pcsw_logo.png";
	private static final String THROBBER_ICON = "/us/pcsw/dbbrowser/resources/images/pcsw_throbber.gif";
	
	private JButton browserButton = null;
    private JButton closeButton = null;
    private URL currentURL = null;
    private URL homeURL = null;
    private JEditorPane htmlEditorPane;
    private HTMLPanelParent parent;
    private JScrollPane htmlScrollPane;
	private Icon logoIcon;
    private JButton navBackButton;
    private JButton navForwardButton;
    private JButton navHomeButton;
    private URL nextURL = null;
    private URL previousURL = null;
    private Icon throbberIcon;
    private JLabel throbberLabel;

    /**
     * The constructor for the class.
     * @param homeURL The home page at which the help dialog is to begin and
     *                return to if the Home button is clicked.
     */
    public HTMLPanel(HTMLPanelParent parent, URL homeURL)
    {
    	super();
    	this.homeURL = homeURL;
    	this.parent = parent;
    	initGUI();
    }
    
    private void initGUI()
    {
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

	// Layout of overall dialog frame
	this.setLayout(new BorderLayout());

	// Navigation buttons provide basic web-browsing control.
	JPanel navPanel = new JPanel();
	navPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
	navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.X_AXIS));

	ImageIcon icon = new ImageIcon(getClass().getResource(BACK_ICON));
	navBackButton = new JButton("Back", icon);
	navBackButton.setEnabled(false);
	navBackButton.setMnemonic('B');
	navBackButton.addActionListener(this);
	navPanel.add(navBackButton);
	
	navPanel.add(new HorizontalStrut(10));

	icon = new ImageIcon(getClass().getResource(HOME_ICON));
	navHomeButton = new JButton("Home", icon);
	navHomeButton.setEnabled(false);
	navHomeButton.setMnemonic('H');
	navHomeButton.addActionListener(this);
	navPanel.add(navHomeButton);

	navPanel.add(new HorizontalStrut(10));

	icon = new ImageIcon(getClass().getResource(FORWARD_ICON));
	navForwardButton = new JButton("Forward", icon);
	navForwardButton.setEnabled(false);
	navForwardButton.setMnemonic('F');
	navForwardButton.addActionListener(this);
	navPanel.add(navForwardButton);
	
	navPanel.add(new HorizontalGlue());

	throbberIcon = new ImageIcon(getClass().getResource(THROBBER_ICON));
	logoIcon = new ImageIcon(getClass().getResource(LOGO_ICON));
	throbberLabel = new JLabel(logoIcon);
	navPanel.add(throbberLabel);

	this.add(navPanel, BorderLayout.NORTH);

	// HTML viewer inside a JScrollPane
	htmlEditorPane = new JEditorPane();
	htmlEditorPane.setEditable(false);
	htmlEditorPane.setContentType("text/html");
	htmlEditorPane.addHyperlinkListener(this);
	htmlScrollPane = new JScrollPane(htmlEditorPane);
	JPanel borderPanel = new JPanel(new BorderLayout());
	borderPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
	borderPanel.add(htmlScrollPane, BorderLayout.CENTER);
	this.add(borderPanel, BorderLayout.CENTER);
	
	// Buttons at the bottom of the panel
	JPanel buttonPanel = new JPanel(new BorderLayout());
	
	browserButton = new JButton("Load in Browser...");
	browserButton.addActionListener(this);
	browserButton.setEnabled(false);
	browserButton.setMnemonic('B');
	buttonPanel.add(browserButton, BorderLayout.WEST);
	
	buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
	closeButton = new JButton("Close");
	closeButton.addActionListener(this);
	closeButton.setMnemonic('C');
	buttonPanel.add(closeButton, BorderLayout.EAST);
	this.add(buttonPanel, BorderLayout.SOUTH);

	// Set the size of the frame and load the page.
	setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * The event handler for all buttons.
     */
    public void actionPerformed(ActionEvent ae) {
	Object source = ae.getSource();
	if (source == null) {
	    // do nothing
	} else if (source.equals(browserButton)) {
		BrowserLauncher launcher = new BrowserLauncher();
		launcher.displayURL(currentURL.toExternalForm());
	} else if (source.equals(closeButton)) {
		parent.closeButtonClicked();
	} else if (source.equals(navBackButton)) {
	    setURL(previousURL);
	} else if (source.equals(navForwardButton)) {
	    setURL(nextURL);
	} else if (source.equals(navHomeButton)) {
	    setURL(homeURL);
	}
    }

    /**
     * The event handler which is evoked if a hyperlink is clicked.
     */
    public void hyperlinkUpdate(HyperlinkEvent event) {
	// This is called if a hyperlink is clicked on

		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		    setURL(event.getURL());
		}
    }

    public static void main(String[] args)
    {
    	try {
	    	HTMLDialog dialog;
	    	URL url;
	    	if (args.length == 0) {
	    		url = new URL("http://www.google.com");
	    	} else if (args.length == 2) {
	    		BrowserLauncher launcher = new BrowserLauncher();
	    		launcher.displayURL(args[0]);
	    		url = new URL(args[1]);
	    	} else {
	    		url = new URL(args[0]);
	    	}
	    	dialog = new HTMLDialog((Frame)null, false, url, "PCSW.us Help Dialog Test");
	    	dialog.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    	dialog.setVisible(true);
    	} catch (Throwable t) {
    		t.printStackTrace();
    	}
    }
    
    /**
     * Sets the URL of the page to be viewed.
     */
    public void setURL(URL newURL) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		throbberLabel.setIcon(throbberIcon);
		browserButton.setEnabled(false);
		navBackButton.setEnabled(false);
		navForwardButton.setEnabled(false);
		navHomeButton.setEnabled(false);
		URLThread t = new URLThread(this, newURL);
		t.start();
	}
	
	void urlFailed(URL vailedURL, String message)
	{
		navBackButton.setEnabled(previousURL != null);
		navForwardButton.setEnabled(nextURL != null);
		navHomeButton.setEnabled(true);
		throbberLabel.setIcon(logoIcon);
	    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    JOptionPane.showMessageDialog(this, message,
					  "Error Opening URL", 
					  JOptionPane.ERROR_MESSAGE);
	}

	void urlLoaded(URL loadedURL)
	{
		if (loadedURL.equals(currentURL)) {
		    // Refresh, do not alter navigation controls
		} else if (loadedURL.equals(nextURL)) {
		    // Forward button was clicked.
		    previousURL = currentURL;
		    currentURL = nextURL;
		    nextURL = null;
		} else if (loadedURL.equals(previousURL)) {
		    // Back button was clicked.
		    nextURL = currentURL;
		    currentURL = previousURL;
		    previousURL = null;
		} else {
		    // A new page is being visited.
		    previousURL = currentURL;
		    nextURL = null;
		    currentURL = loadedURL;
		}
		String urlString = loadedURL.toExternalForm().toLowerCase();
		browserButton.setEnabled(
				urlString.startsWith("file://") ||
				urlString.startsWith("http://")
			);
		navBackButton.setEnabled(previousURL != null);
		navForwardButton.setEnabled(nextURL != null);
		navHomeButton.setEnabled(true);
		throbberLabel.setIcon(logoIcon);
	    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	class URLThread extends Thread
	{
		private String errorMsg;
		private URL newURL;
		private HTMLPanel htmlPanel;
		
		URLThread(HTMLPanel htmlPanel, URL newURL)
		{
			this.htmlPanel = htmlPanel;
			this.newURL = newURL;
		}
		
		public void run() {
			Thread thread = null;
			try {
			    htmlEditorPane.setPage(newURL);
			    thread = new Thread() {
			    	public void run() {
			    		htmlPanel.urlLoaded(newURL);
			    	}
			    };
			} catch (Exception e) {
			    errorMsg = "Error : " + e;
			    thread = new Thread() {
			    	public void run() {
			    		htmlPanel.urlFailed(newURL, errorMsg);
			    	}
			    };
			}
			SwingUtilities.invokeLater(thread);
	    }
	}
}
