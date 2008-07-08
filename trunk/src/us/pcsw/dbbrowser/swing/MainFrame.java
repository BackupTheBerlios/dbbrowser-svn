/*
 * DBBrowser is software for browsing the structure and contents of databases.
 * Copyright (C) 2001 Philip A. Chapman
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the
 *
 *                     Free Software Foundation, Inc.
 *                    51 Franklin Street, Fifth Floor
 *                      Boston, MA  02110-1301, USA.
 */
package us.pcsw.dbbrowser.swing;

//import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import us.pcsw.dbbrowser.Preferences;
import us.pcsw.dbbrowser.cp.ConnectionProvider;
import us.pcsw.dbbrowser.event.StatusEvent;
import us.pcsw.dbbrowser.event.StatusTypeEnum;
import us.pcsw.swing.HTMLDialog;
import us.pcsw.swing.LookAndFeelMenu;
import us.pcsw.swing.SystemInfoDialog;
import us.pcsw.swing.ToolbarButton;

/**
 * The primary frame for the dbbrowser application.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>05/10/2001 Development began on this class.</LI>
 * <LI>10/18/2002 Made many improvements on user interface and fixed several
 *                null pointer bugs.</LI>
 * <LI>02/16/2003 Fixed a bug in fileViewDBObjects_ActionPerformed where only
 *                the currently selected tab's connection is viewable in the
 *                DB view dialog.  Now, all connections are available in the
 *                dialog, but the DB of the currently selected tab is the
 *                one displayed when the dialog is first shown.</LI>
 * <LI>03/13/2003 Fixed a bug in helpAbout_ActionPerformed() in which the
 *                license.html file was not loaded correctly when contained
 *                in a jar.</LI>
 * <LI>03/20/2003 Removed reference to a ConnectionProviderModel.  The
 *                ConnectionPanel no longer requires one in its constructor.
 *                The only reason this class had a reference was to pass it to
 *                new ConnectionPanels.  PAC</LI>
 * <LI>03/14/2003 (GB) Added keyboard accelerators; modified placement of 
 *                Options, About, and Exit menu items based on platform.</LI>
 * <LI>03/20/2003 (GB) Rearranged the File menu; added View menu;
 *                renamed "Statement" menu to "Query".</LI>
 * <LI>03/20/2003 Made small tweaks to the GUI.  PAC </LI> 
 * <LI>03/21/2003 (GB) Changed "Query" menu to "Statement".</LI>
 * <LI>03/21/2003 (GB) Added html styling to max connection exceeded message;
 *                moved message text to constant. </LI>
 * <LI>03/23/2003 Made changes so that this class listens for status changes
 *                from its connection providers.  PAC </LI>
 * <LI>03/24/3003 Added a Disconnect menu option to the Connection menu.  This
 *                option disconnects the db connection in the selected
 *                connection panel. PAC </LI>
 * <LI>03/24/2003 (GB) Added accelerators to the "Save" and "Disconnect"
 *                menu items.</LI>
 * <LI>03/27/2003 Did some refactoring to clean up the code.  Improved the
 *                control of when menus are enabled and disabled.  PAC </LI>
 * <LI>03/31/2003 (GB) Added bug reporting menu item and dialog, and added 
 *                version check menu item</LI>
 * <LI>03/31/2003 Made change in refreshMenuesAndToolbars so that it a tab
 *                can be closed even if it does not have an open connection to
 *                a database.  PAC </LI>
 * <LI>04/05/2003 Added code to registister the default button of Connection
 *                Panels as their tabs are selected.  PAC </LI>
 * <LI>05/28/2003 Added code to restore the frame's size and location to what
 *                what it was when it was last closed.  PAC </LI>
 * <LI>07/25/2003 Added the ability to listen for status events from the DB
 *                browser dialog.  This allows for the text of procedures and
 *                views to be set into the selected connection panels' SQL
 *                text areas.  PAC </LI>
 * <LI>08/30/2003 Fixed a bug in which connection changes in cloned tabs were
 *                not being shown in tab captions or window title.  PAC </LI>
 * <LI>09/18/2003 Added the use of the Connect.png and Disconnect.png icons in
 *                the initToolbar() method.  Re-enabled the use of the
 *                toolbar.  PAC </LI>
 * <LI>09/22/2003 Added the SQL open button to the toolbar.  PAC </LI>
 * <LI>09/23/2003 Fixed a bug in which menu options and toolbar buttons are
 *                not being enabled/disabled when tabs are cloned.  PAC </LI>
 * <LI>01/04/2004 Made minor changes to refreshMenusAndToolbars so that the
 *                statement execute menu item is not enabled when a statement
 *                is running in the currently selected connection panel.
 *                PAC </LI>
 * <LI>01/20/2004 Added the toolbar button for viewing DB structure. PAC </LI>
 * <LI>02/21/2003 Removed the implementation of the interface
 *                com.apple.eawt.ApplicationListener.  A new implementation
 *                class DBBrowserAppListener was created to handle the Apple
 *                events.  Because the DBBrowserAppListener is only created
 *                if the application is being run on an Apple platform, users
 *                running the app on other platforms do not require Apple's
 *                ui.jar library.  PAC </LI>
 * <LI>03/11/2005 Added a menu item for the new Data Import feature.  PAC</LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public class MainFrame
    extends javax.swing.JFrame
    implements java.awt.event.ActionListener,
	            java.awt.event.WindowListener,
    		    javax.swing.event.ChangeListener,
			    us.pcsw.dbbrowser.event.StatusListener
{
	private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
	private static final long serialVersionUID = 1L;
	
	private static String MAX_NINE_CONNECTIONS_WARNING = "<html><b>Currently, a maximum of only 9 connections are supported.<b><br><font size=\"-1\">Closing some open connections and try again.</font></html>";
    
    /**
     * Database view frame.
     */
    private DBViewDialog dbvDialog = null;
    
    // Gui Elements
    // Menus
    private JMenu connection;
    private JMenuItem
		connectionCloneConnection,
		connectionCloseConnection,
		connectionDisconnect,
		connectionImportData,
		connectionNewConnection,
		connectionSaveConnection;
	//private JCheckBoxMenuItem fileViewDBObjects;
    private JMenu edit;
    private JMenuItem
		editPreferences,
		editReplace;
    private JMenu stmt;
    private JMenuItem
    	stmtExec,
		stmtExit,
		stmtOpenSQL,
		stmtSaveSQL,
		stmtSaveResult,
		stmtPrintSQL,
		stmtPrintResult,
		stmtPrev,
		stmtNext,
		stmtView,
		stmtClear;
	private JMenu view;
	private JCheckBoxMenuItem
		viewViewDBObjects;
    private LookAndFeelMenu lAF;
    private JMenu help;
    private JMenuItem
//		helpContents,
		helpDBBrowserHelp,
		helpAbout,
		helpIndex,
		helpCheckForNewVersion,
		helpReportABug,
		helpSystemInfo;
	// Added by Garrett Baker 2003-03-17
	JToolBar toolbar;
	private ToolbarButton 
		toolbarNewConnection,
		toolbarCloneConnection,
		toolbarCloseConnection,
		toolbarOpenSQL,
		toolbarSaveSQL,
		toolbarSaveResult,
		toolbarViewObjects;
	// End Added

    // The connections
    private JTabbedPane conTab;

    /**
     * Constructor to initialize the Frame.
     */
    public MainFrame()
    {
		super();
		
		// Set the icon of the frame
		ImageIcon icon = new ImageIcon(getClass().getResource
			("/us/pcsw/dbbrowser/resources/images/DBBrowser.png"));
		setIconImage(icon.getImage());
		
		// This allows us to do some special stuff on Mac which makes the
		// DBBrowser application integrate with the OS better.
		boolean isMac = (java.lang.System.getProperty("os.name").toLowerCase().indexOf("mac") > -1);	
		if (isMac) {
			java.lang.System.setProperty("apple.awt.showGrowBox", "true");
			java.lang.System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		
		setJMenuBar(initMenuBar(isMac));
		//	Added by Garrett Baker 2003-03-13
		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		toolbar = initToolbar();
		
		c.gridheight = 1;
		c.weightx = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		getContentPane().setLayout(gb);
		gb.setConstraints(toolbar, c);
		getContentPane().add(toolbar);
		// End Add
	
		// Frame Contents
		conTab = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 12, 12, 12);
		c.weighty = 1.0;
		c.weightx = 1.0;
		gb.setConstraints(conTab, c);
		getContentPane().add(conTab);
		conTab.addChangeListener(this);

		refreshMenusAndToolbars();

		// Finally, at least one ConnectionPanel needs to exist.
		createNewTab(null);
		
		// Do final resizing.
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle saneDefaults = new Rectangle(150,
				                               150, 
				                               screenSize.width - 300, 
				                               screenSize.height - 300);
		Rectangle myBounds = Preferences.getDbBrowserBounds();
		if (myBounds == null) {
			myBounds = saneDefaults;
		} else {
			// Make sure the stored bounds will fit in the current screen
			if (myBounds.height > screenSize.height) {
				// Too tall for the screen, resize to default.
				myBounds.height = saneDefaults.height;
			}
			if (myBounds.width > screenSize.width) {
				// Too wide for the screen, resize to default.
				myBounds.width = saneDefaults.width;
			}
			if ((myBounds.height + myBounds.y) > screenSize.height ||
			    myBounds.y < 0) {
				// It will hang off the screen, center it instead. 
				myBounds.y = (screenSize.height / 2) - (myBounds.height / 2);
			}
			if ((myBounds.width + myBounds.x) > screenSize.width ||
			    myBounds.x < 0) {
				// It will hang off the screen, center it instead.
				myBounds.x = (screenSize.width / 2) - (myBounds.width / 2);
			}
		}
		setBounds(myBounds);
		addWindowListener(this);
    }

	/**
	 * Constructor to initialize the Frame and load saved connection
	 * properties.
	 * @param dbc The connection properties file.
	 */
	public MainFrame(File dbc)
	{
		this();
		ConnectionPanel cp = getSelectedConnectionPanel();
		cp.loadConnectionParameters(dbc);
	}

    /**
     * The event handler for all menu items (except look and feel) and buttons.
     */
    public void actionPerformed(ActionEvent e)
    {
		try {
	    	// Process action events from menus.

		    Object source = e.getSource();
		    if (source == null) {
				// Do nothing
		    } else if (source.equals(editPreferences)) {
				showOptionsDialog();
		    } else if (source.equals(editReplace)) {
				getSelectedConnectionPanel().activateSearchAndReplace();
		    } else if (source.equals(connectionCloneConnection) ||
						source.equals(toolbarCloneConnection))
			{
		    	cloneConnection();
		    } else if (source.equals(connectionCloseConnection) ||
			            source.equals(toolbarCloseConnection))
		    {
		    	removeSelectedTab();
		    } else if (source.equals(connectionDisconnect)) {
		    	ConnectionPanel cp = getSelectedConnectionPanel();
		    	if (cp != null) {
		    		cp.closeDatabaseConnection();
		    	}
		    } else if (source.equals(connectionImportData)) {
		    	getSelectedConnectionPanel().activateDataImport();
		    } else if (source.equals(connectionNewConnection) ||
		                source.equals(toolbarNewConnection))
		    {
		    	createNewTab(null);
		    } else if (source.equals(connectionSaveConnection)) {
		    	saveConnectionInfo();
		    } else if (source.equals(stmtOpenSQL) ||
		                source.equals(toolbarOpenSQL)) {
				loadFile();
		    } else if (source.equals(stmtSaveResult) ||
			            source.equals(toolbarSaveResult))
		    {
				saveResults();
		    } else if (source.equals(stmtSaveSQL) ||
			            source.equals(toolbarSaveSQL))
		    {
				saveFile();
		    } else if (source.equals(viewViewDBObjects) ||
			            source.equals(toolbarViewObjects)) {
				viewDBObjects();
		    } else if (source.equals(stmtExit)) {
				exitApp(0);
		    } else if (source.equals(helpAbout)) {
				showHelpAbout();
		    } else if (source.equals(helpIndex)) {
		    	showHelpIndex();
		    } else if (source.equals(helpReportABug)) {
		    	showHelpReportABug();
			} else if (source.equals(helpCheckForNewVersion)) {
				showHelpCheckForNewVersion();
		    } else if (source.equals(helpSystemInfo)) {
		    	SystemInfoDialog sid = new SystemInfoDialog(this, "System Information");
		    	sid.setVisible(true);
		    } else if (source.equals(lAF)) {
		    	Preferences.setLookAndFeelName(UIManager.getLookAndFeel().getName());
		    	if (dbvDialog != null) {
		    		// Make sure the LAF change is propogated to the DB view
		    		// dialog too.
					SwingUtilities.updateComponentTreeUI(dbvDialog);
		    	}
		    } else if (source.equals(stmtClear)) {
				getSelectedConnectionPanel().historyClear();
		    } else if (source.equals(stmtExec)) {
		    	getSelectedConnectionPanel().executeScript();
		    } else if (source.equals(stmtNext)) {
				getSelectedConnectionPanel().historyNext();
		    } else if (source.equals(stmtPrev)) {
				getSelectedConnectionPanel().historyPrevious();
		    } else if (source.equals(stmtView)) {
				getSelectedConnectionPanel().historyShow();
		    } else {
				throw new Exception
				    ("This functionality is currently not implemented.");
		    }
		} catch (java.lang.Throwable E) {
	    	handleException(E);
		}
    }

    /**
     * Creates a clone of the currently selected tab.
     */
    private void cloneConnection()
    {
    	ConnectionPanel cp = getSelectedConnectionPanel();
	   	if (cp == null) {
			JOptionPane.showMessageDialog
				(null,
				 "Currently, there are no selected open connections.",
                 "Connection Not Selected", JOptionPane.WARNING_MESSAGE);
	   	} else {
	   		ConnectionProvider provider = cp.getConnectionProvider();
	   		if (provider == null) {
				JOptionPane.showMessageDialog
					(null,
					 "The currently selected tab does not have an open connection.",
					 "Connection Cannot Be Cloned", JOptionPane.WARNING_MESSAGE);	
	   		} else {
		   		provider = (ConnectionProvider)provider.clone();
		   		cp = createNewTab(provider);
	   		}
		}
    }

	/**
	 * Creates a new tab and returns the ConnectionPanel displayed by the tab.
	 * @param provider The ConnectionProvider which the ConnectionPanel should
	 *                  be initialized with.
	 * @return the ConnectionProvider displayed by the new tab.
	 */
	private ConnectionPanel createNewTab(ConnectionProvider provider)
	{
		int i = conTab.getTabCount() + 1;
		if (i > 9) {
			JOptionPane.showMessageDialog
				(null,
				 MAX_NINE_CONNECTIONS_WARNING,
				 "Connection Limit Reached", JOptionPane.WARNING_MESSAGE);
			return null;
		} else {
			ConnectionPanel cp = new ConnectionPanel(provider);
			cp.addChangeListener(this);
			cp.addStatusListener(this);
			conTab.addTab(cp.getTitle(), cp);
			renumberTabs();
			conTab.setSelectedComponent(cp);
			refreshMenusAndToolbars();
			return cp;
		}
	}

    /**
     * Exits the application.
     * @param returnCode The return code to pass to the environment on the
     *                   application's exit.
     */
    private void exitApp(int returnCode)
    {
		try {
			// Close all db connections
			int m = conTab.getTabCount();
			for (int i = 0; i < m; i++) {
				((ConnectionPanel)conTab.getComponentAt(i)).closeDatabaseConnection();
			}
			
		    // Save the preferences.
		    try {
				if (dbvDialog != null) {
					Preferences.setDbViewBounds(dbvDialog.getBounds());
					dbvDialog.dispose();
				}
		    	Preferences.setDbBrowserBounds(getBounds());
				Preferences.setLookAndFeelName(UIManager.getLookAndFeel().getName());
				Preferences.save();
		    } catch (Throwable t) {
		    	logger.error("Error saving app prefs", t);
		    	JOptionPane.showMessageDialog
		    		(this, "There was an error saving application preferences.",
                     "Error Saving Preferences", JOptionPane.ERROR_MESSAGE);
		    }
	
		    // Dispose of the frame.
		    dispose();
		} catch (Throwable t) {}
		System.exit(returnCode);
    }

    /**
     * Returns the selected ConnectionPanel
     */
    ConnectionPanel getSelectedConnectionPanel()
    {
		return (ConnectionPanel)conTab.getSelectedComponent();
    }

    /**
     * Called whenever an exception is thrown.
     * @param exception Throwable
     */
    private void handleException(Throwable exception)
    {
		JOptionPane.showMessageDialog(this, exception.getMessage(),
					      "Unexpected Error",
					      JOptionPane.ERROR_MESSAGE);
		logger.error("Error occurred", exception);
    }

    /**
     * Initialize the Menus.
     * @param isMac If true, indicates that the host is a Mac.  If this is the
     *               case some special things are done with the options and
     *               help menues to make them more integrated with the Mac OS. 
     */
    private JMenuBar initMenuBar(boolean isMac)
    {
    	int modifier = isMac ? java.awt.event.InputEvent.META_DOWN_MASK :
    	                       java.awt.event.InputEvent.CTRL_DOWN_MASK;

		// Menus
		JMenuBar menuBar = new JMenuBar();
		
		stmt = new JMenu("File");
		stmt.setMnemonic('F');
		
		stmtExec = new JMenuItem("Execute");
		stmtExec.setMnemonic('x');
		// Added by Garrett Baker 2003-03-13
		stmtExec.setAccelerator
			(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0, false));
		// End Added
		stmtExec.addActionListener(this);
		stmt.add(stmtExec);
		
		stmt.addSeparator();
		
		// Added by Garrett Baker 2003-03-19
		// Open SQL
		stmtOpenSQL = new JMenuItem("Open File...");	
		stmtOpenSQL.setMnemonic('O');
		// Added by Garrett Baker 2003-03-13
		stmtOpenSQL.setAccelerator
			(KeyStroke.getKeyStroke('O', modifier, false));
		// End Added
		stmtOpenSQL.addActionListener(this);
		stmt.add(stmtOpenSQL);

		// Save SQL
		stmtSaveSQL = new JMenuItem("Save File...");
		stmtSaveSQL.setMnemonic('S');
		// Added by Garrett Baker 2003-03-13
		stmtSaveSQL.setAccelerator
			(KeyStroke.getKeyStroke
				('S', modifier, false));
		// End Added
		stmtSaveSQL.addActionListener(this);
		stmt.add(stmtSaveSQL);

		// Save Query Result
		stmtSaveResult = new JMenuItem("Save Result...");
		stmtSaveResult.setMnemonic('a');
		// Added by Garrett Baker 2003-03-13
		stmtSaveResult.setAccelerator
			(KeyStroke.getKeyStroke
				('S', modifier | java.awt.event.InputEvent.SHIFT_MASK, false));
		// End Added
		stmtSaveResult.addActionListener(this);
		stmt.add(stmtSaveResult);

		stmt.addSeparator();
		
		// Print SQL
		stmtPrintSQL = new JMenuItem("Print SQL");
		stmtPrintSQL.setMnemonic('P');
		// Added by Garrett Baker 2003-03-13
		stmtPrintSQL.setAccelerator(KeyStroke.getKeyStroke
			('P', modifier, false));
		// End Added
		stmtPrintSQL.addActionListener(this);
		stmt.add(stmtPrintSQL);

		// Print Query Result
		stmtPrintResult = new JMenuItem("Print Result");
		stmtPrintResult.setMnemonic('r');
		// Added by Garrett Baker 2003-03-13
		stmtPrintResult.setAccelerator(KeyStroke.getKeyStroke
			('P', modifier | java.awt.event.InputEvent.SHIFT_MASK, false));
		// End Added
		stmtPrintResult.addActionListener(this);
		stmt.add(stmtPrintResult);

		stmt.addSeparator();
		// End Added
	
		stmtPrev = new JMenuItem("Previous");
		stmtPrev.setMnemonic('v');
		stmtPrev.setAccelerator
			(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, false));
		stmtPrev.addActionListener(this);
		stmt.add(stmtPrev);
	
		stmtNext = new JMenuItem("Next");
		stmtNext.setMnemonic('N');
		stmtNext.setAccelerator
			(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0, false));
		stmtNext.addActionListener(this);
		stmt.add(stmtNext);

		stmt.addSeparator();
	
		stmtView = new JMenuItem("View History");
		stmtView.setMnemonic('H');
		stmtView.addActionListener(this);
		stmt.add(stmtView);
	
		stmtClear = new JMenuItem("Clear History");
		stmtClear.setMnemonic('l');
		stmtClear.addActionListener(this);
		stmt.add(stmtClear);

		// The Mac doesn't have an Exit menu item on the File menu.
        if (!isMac) {
			stmt.addSeparator();
        	
			stmtExit = new JMenuItem("Exit");
			stmtExit.setMnemonic('x');
			stmtExit.setAccelerator
				(KeyStroke.getKeyStroke('Q', modifier, false));
			stmtExit.addActionListener(this);
			stmt.add(stmtExit);
        }
	
		menuBar.add(stmt);

		connection = new JMenu("Connection");
		connection.setMnemonic('C'); 
		
		// New
		connectionNewConnection = new JMenuItem("New");
		connectionNewConnection.setMnemonic('N');
        // Added by Garrett Baker 2003-03-13
		connectionNewConnection.setAccelerator
			(KeyStroke.getKeyStroke('N', modifier, false));
        // End Added
		connectionNewConnection.addActionListener(this);
		connection.add(connectionNewConnection);
		
		// Close
		connectionCloseConnection = new JMenuItem("Close");
		connectionCloseConnection.setMnemonic('C');
		// End Added
		connectionCloseConnection.addActionListener(this);
		connection.add(connectionCloseConnection);
		
		//Disconnect
		connectionDisconnect = new JMenuItem("Disconnect");
		connectionDisconnect.setMnemonic('D');
		connectionDisconnect.setAccelerator
			(KeyStroke.getKeyStroke('D', modifier, false));
		connectionDisconnect.addActionListener(this);
		connection.add(connectionDisconnect);
		
		connection.addSeparator();
		
		// Save
		connectionSaveConnection = new JMenuItem("Save");
		connectionSaveConnection.setMnemonic('S');
		connectionSaveConnection.addActionListener(this);
		connection.add(connectionSaveConnection);

		connection.addSeparator();
		
		// Clone
		connectionCloneConnection = new JMenuItem("Clone");
		connectionCloneConnection.setMnemonic('l');
		// Added by Garrett Baker 2003-03-13
		connectionCloneConnection.setAccelerator
			(KeyStroke.getKeyStroke('L', modifier, false));
		// End Added
		connectionCloneConnection.addActionListener(this);
		connection.add(connectionCloneConnection);

		connection.addSeparator();
		
		// Data Import
		connectionImportData = new JMenuItem("Import Data...");
		connectionImportData.setMnemonic('I');
		// Added by Garrett Baker 2003-03-13
		connectionImportData.setAccelerator
			(KeyStroke.getKeyStroke('I', modifier, false));
		// End Added
		connectionImportData.addActionListener(this);
		connection.add(connectionImportData);
		
		menuBar.add(connection);

		edit = new JMenu("Edit");
		edit.setMnemonic('E');
/*
		editUndo = new JMenuItem("Undo (Ctrl+Z)");
		editUndo.setMnemonic('U');
		editUndo.addActionListener(this);
		edit.add(editUndo);
		
		editRedo = new JMenuItem("Redo (Ctrl+Y)");
		editRedo.setMnemonic('R');
		editRedo.addActionListener(this);
		edit.add(editRedo);
		
		edit.addSeparator();

		editCut = new JMenuItem("Cut (Ctrl+X");
		editCut.setMnemonic('t');
		editCut.addActionListener(this);
		edit.add(editCut);

		editCopy = new JMenuItem("Copy (Ctrl+C)");
		editCopy.setMnemonic('C');
		editCopy.addActionListener(this);
		edit.add(editCopy);

		editPaste = new JMenuItem("Paste (Ctrl+V)");
		editPaste.setMnemonic('P');
		editPaste.addActionListener(this);
		edit.add(editPaste);

		edit.addSeparator();
*/
		editReplace = new JMenuItem("Find and Replace");
		editReplace.setMnemonic('F');
		// Added by Garrett Baker 2003-03-13
		editReplace.setAccelerator
			(KeyStroke.getKeyStroke('F', modifier, false));
		// End Added
		editReplace.addActionListener(this);
		edit.add(editReplace);
		
		// Added by Garrett Baker 2003-03-13
		if (!isMac) {
			edit.addSeparator();
			
			editPreferences = new JMenuItem("Preferences");
			editPreferences.setMnemonic('P');
			editPreferences.addActionListener(this);
			edit.add(editPreferences);
		} else {
			new DBBrowserAppListener(this);
		}
		// End Added
		
		menuBar.add(edit);
		
		view = new JMenu("View");
		view.setMnemonic('V');
		
		viewViewDBObjects = new JCheckBoxMenuItem("View DB Structure");
		viewViewDBObjects.setMnemonic('w');
		viewViewDBObjects.setAccelerator
					(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0, false));
		viewViewDBObjects.addActionListener(this);
		view.add(viewViewDBObjects);
		
		menuBar.add(view);

		lAF = new LookAndFeelMenu(this, Preferences.getLookAndFeelName());
		menuBar.add(lAF);
		// Just in case the setting was null, (re)set the value in the
		// Preferences.
		Preferences.setLookAndFeelName(UIManager.getLookAndFeel().getName());
		// Because of the look and feel retrieved from the preferences file
		// may not be the default and the menu has not yet been added to the
		// MainFrame, update the menu's UI.
		SwingUtilities.updateComponentTreeUI(menuBar);
		lAF.addActionListener(this);
	
		help = new JMenu("Help");
		help.setMnemonic('H');
		
		if (!isMac) {
			helpIndex = new JMenuItem("Index...");
			helpIndex.setMnemonic('I');
			helpIndex.addActionListener(this);
			helpIndex.setAccelerator
				(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0, false));
			help.add(helpIndex);

			helpAbout = new JMenuItem("About...");
			helpAbout.setMnemonic('A');
			helpAbout.addActionListener(this);
			help.add(helpAbout);
		} else {
			helpDBBrowserHelp = new JMenuItem("DBBrowser Help...");
			helpDBBrowserHelp.addActionListener(this);
			helpDBBrowserHelp.setAccelerator
				(KeyStroke.getKeyStroke('H', modifier, false));
			help.add(helpDBBrowserHelp);
		}
	
//		help.addSeparator();
//		
//		helpReportABug = new JMenuItem("Bug Report/Feature Request");
//		helpReportABug.setAccelerator(KeyStroke.getKeyStroke('B',
//					modifier | java.awt.Event.CTRL_MASK, false));
//		helpReportABug.addActionListener(this);
//		help.add(helpReportABug);
//
//		helpCheckForNewVersion = new JMenuItem("Check For New Version");
//		helpCheckForNewVersion.addActionListener(this);
//		help.add(helpCheckForNewVersion);
		
		help.addSeparator();
		helpSystemInfo = new JMenuItem("System Info...");
		helpSystemInfo.setMnemonic('S');
		helpSystemInfo.addActionListener(this);
		help.add(helpSystemInfo);
		
		menuBar.add(help);
	
		return menuBar;
    }
    
	/**
	 * Initializes the main windows's toolbar.
	 * @return the newly initialized toolbar.
	 */
    private JToolBar initToolbar()
    {
		JToolBar toolbar = new JToolBar();
		boolean display = Preferences.displayToolbarLabels();
		ImageIcon icon = null;
		ImageIcon sepIcon = new ImageIcon(getClass().getClassLoader().getResource
					("us/pcsw/dbbrowser/resources/images/ToolbarSeperator.png"));
					
		toolbar.setFloatable(false);
		toolbar.setBorderPainted(false);

		// New Connection button
		icon = new ImageIcon(getClass().getClassLoader().getResource
					("us/pcsw/dbbrowser/resources/images/Connect.png"));
		toolbarNewConnection = new ToolbarButton("New Connection", icon);
		toolbarNewConnection.setBorderPainted(false);
		toolbarNewConnection.setToolTipText("Open a New Connection");
//		toolbarNewConnection.setDisabledIcon(disabledIcon);
		toolbarNewConnection.setVerticalTextPosition(SwingConstants.BOTTOM);
		toolbarNewConnection.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbarNewConnection.setLabelDisplayed(display);
		toolbarNewConnection.addActionListener(this);
		toolbar.add(toolbarNewConnection);

		// Clone Connection button
		icon = new ImageIcon(getClass().getClassLoader().getResource
					("us/pcsw/dbbrowser/resources/images/CloneConnection.png"));
		toolbarCloneConnection = new ToolbarButton("Clone Connection", icon);
		toolbarCloneConnection.setBorderPainted(false);
		toolbarCloneConnection.setToolTipText("Clone the Current Connection");
//		toolbarCloneConnection.setDisabledIcon(disabledIcon);
		toolbarCloneConnection.setVerticalTextPosition(SwingConstants.BOTTOM);
		toolbarCloneConnection.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbarCloneConnection.setLabelDisplayed(display);
		toolbarCloneConnection.addActionListener(this);
		toolbar.add(toolbarCloneConnection);
				
		// Close Connection button
		icon = new ImageIcon(getClass().getClassLoader().getResource
					("us/pcsw/dbbrowser/resources/images/Disconnect.png"));
		toolbarCloseConnection = new ToolbarButton("Close Connection", icon);
		toolbarCloseConnection.setBorderPainted(false);
		toolbarCloseConnection.setToolTipText("Close the Frontmost Connection");
//		toolbarCloseConnection.setDisabledIcon(disabledIcon);
		toolbarCloseConnection.setVerticalTextPosition(SwingConstants.BOTTOM);
		toolbarCloseConnection.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbarCloseConnection.setLabelDisplayed(display);
		toolbarCloseConnection.addActionListener(this);
		toolbar.add(toolbarCloseConnection);
		
		if (! display) {
			toolbar.add(new JLabel(sepIcon));
		}
		
		// Open SQL button
		icon = new ImageIcon(getClass().getClassLoader().getResource
					("us/pcsw/dbbrowser/resources/images/OpenSQL.png"));
		toolbarOpenSQL = new ToolbarButton("Open File", icon);
		toolbarOpenSQL.setBorderPainted(false);
		toolbarOpenSQL.setToolTipText("Open SQL/Script into Frontmost Tab");
//		toolbarOpenSQL.setDisabledIcon(disabledIcon);
		toolbarOpenSQL.setVerticalTextPosition(SwingConstants.BOTTOM);
		toolbarOpenSQL.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbarOpenSQL.setLabelDisplayed(display);
		toolbarOpenSQL.addActionListener(this);
		toolbar.add(toolbarOpenSQL);
		
		// Save SQL button
		icon = new ImageIcon(getClass().getClassLoader().getResource
					("us/pcsw/dbbrowser/resources/images/SaveSQL.png"));
		toolbarSaveSQL = new ToolbarButton("Save File", icon);
		toolbarSaveSQL.setBorderPainted(false);
		toolbarSaveSQL.setToolTipText("Save Frontmost Tab's SQL/Script");
//		toolbarSaveSQL.setDisabledIcon(disabledIcon);
		toolbarSaveSQL.setVerticalTextPosition(SwingConstants.BOTTOM);
		toolbarSaveSQL.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbarSaveSQL.setLabelDisplayed(display);
		toolbarSaveSQL.addActionListener(this);
		toolbar.add(toolbarSaveSQL);
		
		// Save Result button
		icon = new ImageIcon(getClass().getClassLoader().getResource
			("us/pcsw/dbbrowser/resources/images/SaveResult.png"));
		toolbarSaveResult = new ToolbarButton("Save Result", icon);
		toolbarSaveResult.setBorderPainted(false);
		toolbarSaveResult.setToolTipText("Save Frontmost Tab's Query Results");
//		toolbarSaveResult.setDisabledIcon(disabledIcon);
		toolbarSaveResult.setVerticalTextPosition(SwingConstants.BOTTOM);
		toolbarSaveResult.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbarSaveResult.setLabelDisplayed(display);
		toolbarSaveResult.addActionListener(this);
		toolbar.add(toolbarSaveResult);
		
		if (! display) {
			toolbar.add(new JLabel(sepIcon));
		}
		
		// View DB Objects
		icon = new ImageIcon(getClass().getClassLoader().getResource
			("us/pcsw/dbbrowser/resources/images/DBView.png"));
		toolbarViewObjects = new ToolbarButton("View DB Structure", icon);
		toolbarViewObjects.setBorderPainted(false);
		toolbarViewObjects.setToolTipText("View the structure of the database");
//			 toolbarSaveResult.setDisabledIcon(disabledIcon);
		toolbarViewObjects.setVerticalTextPosition(SwingConstants.BOTTOM);
		toolbarViewObjects.setHorizontalTextPosition(SwingConstants.CENTER);
		toolbarViewObjects.setLabelDisplayed(display);
		toolbarViewObjects.addActionListener(this);
		toolbar.add(toolbarViewObjects);
			 
		return toolbar;
    }

	/**
	 * Causes the currently selected tab to load an SQL script from a file.
	 */
	private void loadFile()
	{
		try {
			getSelectedConnectionPanel().loadFile();
		} catch (java.lang.Throwable e) {
			handleException(e);
		}
	}

	private void refreshMenusAndToolbars()
	{
		boolean connected = false;
		boolean history = false;
		boolean results = false;
		boolean scriptOutput = false;
		boolean sqlStmt = false;
		boolean stmtExecuting = false;

		ConnectionPanel cp = getSelectedConnectionPanel();
		if (cp != null) {
			connected = cp.isConnected();
			history = connected && cp.hasHistory();
			results = connected && cp.hasResultSet();
			scriptOutput = connected && cp.hasScriptOutput();
			sqlStmt = connected && cp.getScriptText().length() > 0;
			stmtExecuting = connected && cp.isStatementExecuting();
		}
		connectionCloseConnection.setEnabled(cp != null);
		toolbarCloseConnection.setEnabled(cp != null);
		connectionCloneConnection.setEnabled(connected);
		toolbarCloneConnection.setEnabled(connected);
		connectionDisconnect.setEnabled(connected);
		connectionImportData.setEnabled(connected);
		connectionNewConnection.setEnabled(conTab.getTabCount() < 9);
		toolbarNewConnection.setEnabled(conTab.getTabCount() < 9);
		connectionSaveConnection.setEnabled(connected);
//		editCopy.setEnabled(sqlStmt);
//		editCut.setEnabled(sqlStmt);
//		editPaste.setEnabled(sqlStmt);
//		editRedo.setEnabled(sqlStmt);
		editReplace.setEnabled(sqlStmt);
//		editUndo.setEnabled(sqlStmt);
		stmtExec.setEnabled(sqlStmt && (! stmtExecuting));
		stmtOpenSQL.setEnabled(connected);
		toolbarOpenSQL.setEnabled(connected);
		stmtSaveSQL.setEnabled(sqlStmt);
		toolbarSaveSQL.setEnabled(sqlStmt);
		if (results) {
			stmtSaveResult.setEnabled(true);
			toolbarSaveResult.setEnabled(true);
			if (Preferences.displayToolbarLabels()) {
				stmtSaveResult.setText("Save Result...");
				toolbarSaveResult.setText("Save Result");
			} else {
				stmtSaveResult.setText(null);
				toolbarSaveResult.setText(null);
			}
		} else if (scriptOutput) {
			stmtSaveResult.setEnabled(true);
			toolbarSaveResult.setEnabled(true);
			if (Preferences.displayToolbarLabels()) {
				stmtSaveResult.setText("Save Output...");
				toolbarSaveResult.setText("Save Output");
			} else {
				stmtSaveResult.setText(null);
				toolbarSaveResult.setText(null);
			}
		} else {
			stmtSaveResult.setEnabled(false);
			toolbarSaveResult.setEnabled(false);
		}
		stmtPrintSQL.setEnabled(false);    // setEnabled.(sqlStmt);
		stmtPrintResult.setEnabled(false); // setEnabled.(results);
		stmtPrev.setEnabled(history);
		stmtNext.setEnabled(history);
		stmtView.setEnabled(connected);
		stmtClear.setEnabled(history);
		connected = false;
		for (int i = 0; i < conTab.getTabCount(); i++) {
			connected =
				connected ||
			    ((ConnectionPanel)conTab.getComponentAt(i)).isConnected();
		}
		viewViewDBObjects.setEnabled(connected);
		toolbarViewObjects.setEnabled(
				connected & ! viewViewDBObjects.getState()
			);
	}

    private void refreshTitle()
    {
		ConnectionPanel cp = getSelectedConnectionPanel();
		if (cp == null || ! cp.isConnected()) {
			setTitle("Database Browser Application [Not Connected]");
		} else {
			setTitle("Database Browser Application " +
					 cp.getConnectionDescription());
		}
    }

	/**
	 * Removes the currently selected tab.
	 */
	private void removeSelectedTab()
	{
		ConnectionPanel cp = getSelectedConnectionPanel();
		if (cp != null) {
			cp.closeDatabaseConnection();
			conTab.remove(cp);
			renumberTabs();
		}
	}

	/**
	 * Each tab's title begins with a number which is used as the mnemonic.
	 * This method renumbers the tabs.  It should be called after tab(s) are
	 * added or removed.
	 */
	private void renumberTabs()
	{
		ConnectionPanel cp = null;
		StringBuffer sb = null;
		int j = 0;
		
		int m = conTab.getTabCount();
		for (int i = 0; i < m; i++) {
			j = i + 1;
			cp = (ConnectionPanel)conTab.getComponentAt(i);
			sb = new StringBuffer(String.valueOf(j));
			sb.append(' ');
			sb.append(cp.getTitle());
			conTab.setTitleAt(i, sb.toString());
			conTab.setMnemonicAt(i, String.valueOf(j).charAt(0));
		}	
	}

    private void saveAppSettings() {
    	try {
		   	Preferences.save();
    	} catch (Exception ioe) {
    		handleException(new Exception("Unable to save application perferences."));	
    	}
    }

	/**
	 * Saves the connection info of the currently selected tab to file.
	 */
	private void saveConnectionInfo()
	{
		try {
			getSelectedConnectionPanel().saveConnectionInfo();
		} catch (java.lang.Throwable e) {
			handleException(e);
		}
	}

	/**
	 * Shows the options dialog.
	 */
	private void showOptionsDialog()
	{
		OptionsDialog od = new OptionsDialog(this);
		od.setModal(true);
		od.setVisible(true);
		saveAppSettings();
		// Cause each connection panel to reload the preferences.
		for (int i = 0; i < conTab.getTabCount(); i++) {
			((ConnectionPanel)conTab.getComponentAt(i)).reloadPreferences();
		}

/* Commented out until toolbar is finished.		
		// Update toolbar
		Component c = null;
		int size = toolbar.getComponentCount();
		boolean display = Preferences.displayToolbarLabels();
		for (int i = 0; i < size; i++) {
			if (c instanceof ToolbarButton) {
				((ToolbarButton)c).setLabelDisplayed(display); 
			}
		}
*/
	}

	/**
	 * Saves the currently selected tab's results to file. 
	 */
	private void saveResults()
	{
		try {
			getSelectedConnectionPanel().saveResultSet();
		} catch (java.lang.Throwable e) {
			handleException(e);
		}
	}

	/**
	 * Causes the currently selected tab to save it's SQL statment(s) to file.
	 */
	private void saveFile()
	{
		try {
			getSelectedConnectionPanel().saveFile();
		} catch (java.lang.Throwable e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			handleException(e);
		}
	}

	/**
	 * Action event called when the Help->About menu item is selected.
	 * @param event ActionEvent The event that was sent.
	 */
	private void showHelpAbout()
	{
		try {
			URL licenseURL = getClass().getClassLoader().getResource
				("us/pcsw/dbbrowser/resources/license.html");
			HTMLDialog hd =
				new HTMLDialog(this, false, licenseURL, "About DB Browser");
			hd.setVisible(true);
		} catch (java.lang.Throwable e) {
			handleException(e);
		}
	}
	
	private void showHelpIndex()
	{
		try {
			URL licenseURL = getClass().getClassLoader().getResource
				("us/pcsw/dbbrowser/resources/index.html");
			HTMLDialog hd =
				new HTMLDialog(this, false, licenseURL, "DB Browser Help");
			hd.setVisible(true);
		} catch (java.lang.Throwable e) {
			handleException(e);
		}
	}
	
	/**
	 * Action event called when the Help->Report A Bug menu item is selected.
	 * @param event ActionEvent The event that was sent.
	 */
	private void showHelpReportABug()
	{
		BugReportDialog brd = new BugReportDialog(this);
		brd.setVisible(true);
	}
		
	/**
	 * Action event called when the Help->Report A Bug menu item is selected.
	 * @param event ActionEvent The event that was sent.
	 */
	private void showHelpCheckForNewVersion()
	{
		// do something...
	}

	/**
	 * Handles stateChanged event imitted by the connection tab.
	 */
	public void stateChanged(ChangeEvent ce)
	{
		refreshTitle();
		refreshMenusAndToolbars();
		ConnectionPanel cp = getSelectedConnectionPanel();
		if (cp == null) {
			getRootPane().setDefaultButton(null);
		} else {
			cp.setDefaultButtonForRootPanel(getRootPane());
		}
	}

	public void statusChanged(StatusEvent se)
	{
		ConnectionProvider cp;
		Object source = se.getSource();
		StatusTypeEnum ste = se.getType();
		
		if (source instanceof ConnectionPanel) {
			if (ste == StatusTypeEnum.CONNECTED) {
				renumberTabs();
				refreshTitle();
				if (dbvDialog != null) {
					cp = ((ConnectionPanel)se.getSource()).getConnectionProvider();
					if (cp != null) {
						dbvDialog.add(cp);
					}
				}
			} else if (ste == StatusTypeEnum.DISCONNECTED) {
				renumberTabs();
				refreshTitle();
				ConnectionPanel cpnl = getSelectedConnectionPanel();
				if (cpnl == null) {
					getRootPane().setDefaultButton(null);
				} else {
					cpnl.setDefaultButtonForRootPanel(getRootPane());
				}
				// It would be nice to remove the previous connection from the
				// DB view, but for now it is not easily done.
			}
			if (se.getSource().equals(getSelectedConnectionPanel()) &&
			    ste != StatusTypeEnum.BUSY && ste != StatusTypeEnum.NOT_BUSY)
			{
				refreshMenusAndToolbars();		    
			}
		} else {
			// This is from the DB View
			if (ste == StatusTypeEnum.DATA_AVAIL) {
				getSelectedConnectionPanel().setSQLText(se.getData().toString());
			}
		}
	}

	/**
	 * Opens a frame showing the available databases and their objects.
	 * @param event ActionEvent The event that was sent.
	 */
	private void viewDBObjects()
	{
		ConnectionPanel cPanel = null;
		try {
			if (dbvDialog == null) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				// Add current connection as first listed in dialog.
				cPanel = getSelectedConnectionPanel();
				ConnectionProvider cp = cPanel.getConnectionProvider();
				dbvDialog = new DBViewDialog(cp);
				dbvDialog.addStatusListener(this);
				dbvDialog.addWindowListener(this);
				
				// Add additional ConnectionProviders for any other panels.
				// If the ConnectionProvider is already represented in the
				// dialog, it will be ignored in the dbvDialog's add method.
				int m = conTab.getTabCount();
				for (int i = 0; i < m; i++) {
					cPanel = (ConnectionPanel)conTab.getComponentAt(i);
					dbvDialog.add(cPanel.getConnectionProvider());
				}	
				
				dbvDialog.setVisible(true);
				
				viewViewDBObjects.setState(true);
				
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			} else {
				if (dbvDialog != null) {
					dbvDialog.dispose();
					dbvDialog = null;
				}
				
				viewViewDBObjects.setState(false);
			}
			refreshMenusAndToolbars();
		} catch (java.lang.Throwable e) {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			handleException(e);
		}
	}

    public void windowActivated(WindowEvent e) {}

    public void windowClosed(WindowEvent e)
    {
    	Object o = e.getSource();
    	if (o == this) {
			exitApp(0);
    	} else if (o == dbvDialog) {
    		viewViewDBObjects.setState(false);
    		dbvDialog = null;
    	}
    }

    public void windowClosing(WindowEvent e)
    {
    	Object o = e.getSource();
    	if (o == this) {
			exitApp(0);
    	} else if (o == dbvDialog) {
    		Preferences.setDbViewBounds(dbvDialog.getBounds());
    		viewViewDBObjects.setState(false);
    		dbvDialog = null;
    	}
    }

    public void windowDeactivated(WindowEvent e) {}

    public void windowDeiconified(WindowEvent e) {}        

    public void windowIconified(WindowEvent e) {}

    public void windowOpened(WindowEvent e) {}
    
    // Added by Garrett Baker 2003-03-14
    boolean handleQuit()
    {
		return true;
    }
    
	boolean handleOpenApplication()
	{
		return false;
	}
	
	boolean handlePrintFile()
	{
		return false;
	}
	
	boolean handleOpenFile()
	{
		return false;
	}
	
	boolean handlePreferences(){
		showOptionsDialog();
		return true;
	}
	
	boolean handleAbout()
	{
		showHelpAbout();
		return true;
	}
    // End Added
}
