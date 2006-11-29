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

import us.pcsw.dbbrowser.ExecutionWorker;
import us.pcsw.dbbrowser.HistoryListModel;
import us.pcsw.dbbrowser.Preferences;
import us.pcsw.dbbrowser.ResultSetTableModel;
import us.pcsw.dbbrowser.SQLExecutionResults;
import us.pcsw.dbbrowser.SQLExecutionWorker;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.TabSet;
import javax.swing.text.TabStop;

import javax.swing.undo.UndoManager;

import bsh.EvalError;
import bsh.TargetError;

import us.pcsw.dbbrowser.bsh.BshExecutionResults;
import us.pcsw.dbbrowser.bsh.BshExecutionWorker;
import us.pcsw.dbbrowser.cp.ConnectionProvider;

import us.pcsw.dbbrowser.event.ElapsedTimeEventTimer;
import us.pcsw.dbbrowser.event.StatusEvent;
import us.pcsw.dbbrowser.event.StatusListener;
import us.pcsw.dbbrowser.event.StatusTypeEnum;
import us.pcsw.dbbrowser.swing.text.HighlightDocument;

import us.pcsw.util.Debug;

import us.pcsw.util.tablemodelexport.TableModelExport;

import us.pcsw.swing.BasicFileFilter;
import us.pcsw.swing.SearchAndReplaceDialog;

/**
 * us.pcsw.dbbrowser.swing.ConnectionPanel
 * -
 * A panel which allows the user to connect to a database, submit queries, and
 * view results.  One of these are embedded in each tab displayed by the
 * MainFrame.
 * 
 * <P><B>EVENTS:</B>
 * This class emits StatusEvents.  Any class wich wishes to listen to
 * StatusEvents emitted from this class must implement
 * us.dbbrowser.event.StatusListener.  StatusEvents emitted are as follows:
 * <BR><TABLE>
 * <TR align="left" valign="top">
 *     <TD>StatusTypeEnum.CONNECTED</TD>
 *     <TD>Indicates a new database connection has been made</TD></TR>
 * <TR align="left" valign="top">
 *     <TD>StatusTypeEnum.DISCONNECTED</TD>
 *     <TD>Indicates the new database connection has been closed</TD></TR>
 * <TR align="left" valign="top">
 *     <TD>StatusTypeEnum.DATA_AVAIL</TD>
 *     <TD>Indicates that there is a SQL statment available to be executed or
 *         persisted to file.</TD></TR>
 * <TR align="left" valign="top">
 *     <TD>StatusTypeEnum.DATA_NOT_AVAIL</TD>
 *     <TD>Indicates that there is not a SQL statment available to be executed
 *         or persisted to file.</TD></TR>
 * <TR align="left" valign="top">
 *     <TD>StatusTypeEnum.QUERY_STARTED</TD>
 *     <TD>Indicates that a SQL statement has been submitted to the connected
 *         server.</TD></TR>
 * <TR align="left" valign="top">
 *     <TD>StatusTypeEnum.QUERY_ENDED</TD>
 *     <TD>Indicates that the connected server has responded to a submitted
 *         SQL statement.  This StatusEvent is emitted when results or an error
 *         are returned by the server.</TD></TR>
 * <TR align="left" valign="top">
 *     <TD>StatusTypeEnum.BUSY</TD>
 *     <TD>Indicates that a long running process has begin.  This event is
 *         emitted when connecting to a database, loading, saving, or executing
 *         an SQL script, saving results, or printing.  This event will always
 *         be fired before CONNECTED, QUERY_STARTED and DATA_AVAIL.
 *         (note: to be implemented)</TD></TR>
 * <TR align="left" valign="top">
 *     <TD>StatusTypeEnum.NOT_BUSY</TD>
 *     <TD>Indicates that a long running process has ended.  This event is
 *         emitted after connecting to a database, loading, saving, or executing
 *         an SQL script, saving results, or printing.  This event will always
 *         be fired after CONNECTED, QUERY_ENDED and DATA_AVAIL.
 *         (note: to be implemented)</TD></TR>
 * </TABLE></P>
 *
 * <P><B>Revision History:</B><UL>
 * <LI>10/01/2002 Development began on this class.</LI>
 * <LI>10/18/2002 Development completed.</LI>
 * <LI>10/29/2002 Tweaked UI so that the statement pane gets focus after the
 *                execute button is pressed.</LI>
 * <LI>11/08/2002 Added capability to use SQLFont preference for SQL text.</LI>
 * <LI>11/16/2002 Added undo capability to stmtPane, accessed by the key
 *                sequence Ctrl+Z.</LI>
 * <LI>11/18/2002 Added capability for adjusting the number of characters
 *                between tabs in the statement pane.</LI>
 * <LI>03/12/2003 Fixed a bug in executeStatement(String) which caused the app
 *                to hang when updates with subselects were executed.  The
 *                returnsResultset() method of ConnectionProvider subclasses
 *                were not doing a good enough job of parsing the SQL.  The
 *                update statements were being run with
 *                Statement.executeQuery(String) even though they do not
 *                return result sets.  The Statement.execute(String) method is
 *                now used for all statements.</LI>
 * <LI>03/20/2003 Removed reference to a ConnectionProviderModel.  The login
 *                dialog now gets the model directly from the Preferences
 *                class.  PAC </LI>
 * <LI>03/20/2003 (GB) Rearranged UI: Moved buttons to right side of statement pane,
 *                added GridBagLayout, and added some more buttons.</LI>
 * <LI>03/21/2003 (GB) Added cool HTML styling to DB connection error dialog. :-)</LI>
 * <LI>03/22/2003 Made changes so that the login dialog is shown in the upper
 *                side of the split pane, not in a seperate dialog window.
 *                PAC </LI>
 * <LI>03/24/2003 (GB) Added code to set the location of the JSplitPane's
 *                divider. </LI>
 * <LI>03/25/2003 Added code to emit DATA_AVAIL, DATA_NOT_AVAIL, QUERY_ENDED,
 *                and QUERY_STARTED status events.  PAC </LI>
 * <LI>03/27/2003 Moved the saveConnectionInfo() method into this class from
 *                the MainFrame class.  This keeps it consitant with other save
 *                features.  PAC </LI>
 * <LI>04/04/2003 Added label which shows the caret position in the statement
 *                pane.  PAC </LI>
 * <LI>04/05/2003 Added the method setDefaultButtonForRootPanel(JRootPane) so
 *                that the parent object can choose to have this panel's
 *                login connect button as the default button.  PAC </LI>
 * <LI>05/16/2003 Added reporting of row count for selects and execution
 *                time for updates and selects.  PAC </LI>
 * <LI>05/30/2003 Added code to autosize a column when the left boarder of the
 *                column header is double-clicked.  PAC </LI>
 * <LI>09/24/2003 Added the ability to change the font of the results by
 *                honoring the ResultsFont preference in reloadPreferences.
 *                Also changed it so that the message area uses the preferred
 *                font for SQL text. PAC </LI>
 * <LI>11/13/2003 Added the ability to save results as insert statements.
 *                PAC </LI>
 * <LI>01/02/2004 Did some minor refactoring.  I moved the code which surrounds
 *                a string with quote identifiers into the
 *                us.pcsw.util.StringUtil class.  The code is too generic and
 *                does not belong here.  Also, I fixed some issues with the
 *                resultset save code's handling of null values.  Made some
 *                minor tweaks to the GUI.  Changed the statement execution
 *                code to use the new SQLExceptionWorker class rather than
 *                running queries in the GUI event thread.  Added
 *                implementation of StatusListener.  PAC </LI>
 * <LI>01/04/2004 Added code so that the SQL statement is executed within its
 *                own thread.  This allows the GUI to refresh while
 *                long-running statements are executed.  PAC </LI>
 * <LI>01/23/2004 Fixed a bug in which the MainFrame was not being notified of
 *                DATA_AVAILABLE status events after an error was thrown
 *                during the running of a SQL statement.  This bug was
 *                introduced when the multithreading code was added. PAC </LI>
 * <LI>05/02/2004 Fixed minor bug where an extra newline is inserted at the
 *                top of SQL loaded from file. PAC </LI>
 * <LI>03/11/2005 Added a method to spawn the import frame for the new data
 *                import feature.  PAC </LI>
 * <LI>03/11/2005 PAC Added the ability to cancel a statement that is being
 *                executed. </LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public class ConnectionPanel
	extends javax.swing.JPanel
	implements java.awt.event.ActionListener, java.awt.event.KeyListener,
	            javax.swing.event.CaretListener,
	            us.pcsw.dbbrowser.event.StatusListener
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Constant used by reloadPreferences to help determine the average
	 * character width for the statement pane's default font.
	 */
	private static final String ALPHANUMERIC =
		"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	private static final String RESULTS_TAB_PREFIX = "Results ";
	
	private static final String SCRIPT_IDENTIFIER = "//bsh";

	/**
	 * A reference to the database connection
	 */
	private Connection db = null;
	
	/**
	 * The icon to be displayed for error messages.
	 */
	private Icon errorIcon = null;

	/**
	 * The filter used in the statement file selection dialog for DBBrowser scripts.
	 */
	private BasicFileFilter dbsFileFilter = new BasicFileFilter("dbs", "DBBrowser Script");
	
	/**
	 * The filter used in the statement file selection dialog.
	 */
	private BasicFileFilter sqlFileFilter = new BasicFileFilter("sql", "SQL Script");
	
	/**
	 * The icon to be displayed for information messages.
	 */
	private Icon infoIcon = null;
	
	/**
	 * The SQL statement history list.
	 */
	private HistoryListModel historyList = null;
	
	/**
	 * This dialog used to display the SQL statement history list.
	 */
	private HistoryListDialog hld = null;
	
	/**
	 * Keeps track of which SQL statement in the history is selected.
	 */
	private DefaultListSelectionModel historySelection = null;
	
	/**
	 * Holds connection paramaters.
	 */
	private ConnectionProvider provider = null;
	
	/**
	 * Manages undo operations for the SQL text pane.
	 */
	private UndoManager sqlUndoMgr = new UndoManager();
	
	/**
	 * Listeners to be notified of status change events.
	 */
	private Vector statusListeners;
	
	/**
	 * A timer object used to update the UI with info on how long a SQL
	 * statement has been running.
	 */
	private Timer timer;

	/**
	 * The last type of data status event provided to the listeners.  (Used to
	 * determine if another one is due.)
	 */
	private StatusTypeEnum stmtDataStatus;
	
	// Gui Elements
	private LoginPanel loginPane;
	private JSplitPane outputSplitPane;
	private JTabbedPane outputTabbedPane;
	private JTextPane shellOutputPane;
	private JScrollPane shellOutputScrollPane;
	private JSplitPane sPane;
	private JLabel positionLabel;
	private JPanel topPane;
	
	// SQL statement edit area
	private JTextPane stmtPane;
	private JScrollPane stmtPaneScrollPane;
	
	// Message area
	private JTextArea msgArea;
	private JScrollPane msgAreaPane;
	private JLabel msgIconLabel;
	
	// Command buttons
	private JButton executeButton, testButton, cancelButton, clearButton;
	
	// Filechoosers for user input
	private JFileChooser sqlChooser = null;
	
	private ExecutionWorker worker;

	/**
	 * Constructor to initialize the Panel.
	 * @param cp the connection provider to use to connect to the DB.
	 */
	public ConnectionPanel
		(ConnectionProvider cp)
	{
		this.provider = cp;
		historyList = new HistoryListModel();
		historySelection = new DefaultListSelectionModel();
		historySelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		try {
			errorIcon = new ImageIcon(getClass().getClassLoader().getResource
						("us/pcsw/dbbrowser/resources/images/msg_error.png"));
			infoIcon = new ImageIcon(getClass().getClassLoader().getResource
			           ("us/pcsw/dbbrowser/resources/images/msg_inform.png"));
		} catch (Throwable t) {
			// Unable to load the icons, but that is not catastrophic.
			Debug.log(t);
		}
		
		initGUI();
		reloadPreferences();
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
		    } else if (source.equals(cancelButton)) {
		    	cancelStatement();
		    } else if (source.equals(executeButton)) {
				executeScript();
		    } else if (source.equals(hld)) {
				stmtPane.setText((String)(e.getActionCommand()));
		    } else if (source.equals(testButton)) {
		    	executeTest();
		    } else if (source.equals(clearButton)) {
		    	clearSQL();
		    } else if (source.equals(loginPane)) {
		    	if (e.getID() == LoginPanel.ACTION_EVENT_SELECTION) {
					connect();
		    	} else if (e.getID() == LoginPanel.ACTION_EVENT_CANCEL) {
					// Don't change the provider, just switch back to the SQL
					// panel.
					sPane.setTopComponent(topPane);
		    	}
		    } else {
				throw new Exception
			    	("This functionality is currently not implemented.");
		    }
		} catch (java.lang.Throwable E) {
		    handleException(E);
		}
	}
	
	void activateDataImport()
	{
		ImportFrame iFrame = new ImportFrame(getConnectionProvider());
		iFrame.setVisible(true);
	}

	/**
	 * Activates the search and replace dialog for the SQL text panel.
	 */
	void activateSearchAndReplace()
	{
		try {
		    SearchAndReplaceDialog sard =
			new SearchAndReplaceDialog(getFrame(), stmtPane);
		    sard.setVisible(true);
		} catch (java.lang.Throwable E) {
		    handleException(E);
		}
	}
	
	private class RepeatingChangeListener implements ChangeListener
	{
		RepeatingChangeListener(ChangeListener l)
		{
			super();
			innerListener = l;
		}
		ChangeListener innerListener;
		public void stateChanged(ChangeEvent ce) {
			innerListener.stateChanged(new ChangeEvent(ConnectionPanel.this));
		}
	}
	
	private Set changeListeners = new HashSet();
	
	public void addChangeListener(ChangeListener l)
	{
		Object o;
		for (Iterator iter = changeListeners.iterator(); iter.hasNext();) {
			o = iter.next();
			if (o instanceof RepeatingChangeListener) {
				if (((RepeatingChangeListener)o).innerListener.equals(l)) {
					return;
				}
			}
		}
		ChangeListener cl = new RepeatingChangeListener(l);
		changeListeners.add(cl);
		outputTabbedPane.addChangeListener(cl);
	}

	/**
	 * Adds a listener to the list of those to be notified of status events
	 * thrown by this class.
	 * @param listener The listener.
	 */
	public void addStatusListener(StatusListener listener)
	{
		if (statusListeners == null) {
			statusListeners = new Vector(1, 1);
		}
		if (! statusListeners.contains(listener)) {
			statusListeners.add(listener);
		}
	}
	
	public void cancelStatement()
	{
		if (worker instanceof SQLExecutionWorker) {
			((SQLExecutionWorker)worker).interrupt();
		}
	}

	/**
	 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
	 */
	public void caretUpdate(CaretEvent e) {
		String s = stmtPane.getText();
		int xPos = 0, yPos = 0;
		int pos = stmtPane.getSelectionStart();
		for (int i = s.indexOf('\n'); i < pos; i = s.indexOf('\n',++i))
		{
			if (i < 0) {
				break;
			} else {
				xPos++;
				yPos = i + 1;
			}
		}
		yPos = pos - yPos;
		StringBuffer sb = new StringBuffer();
		sb.append(++xPos);
		sb.append('/');
		sb.append(++yPos);
		positionLabel.setText(sb.toString());
	}
	
	/**
	 * Closes the connection to the database.
	 */
	void closeDatabaseConnection()
	{
		// Can't close what we do not have.
		if (db != null) {
			try {
				db.close();
			} catch (SQLException sqle) {}
			db = null;
			sPane.setTopComponent(loginPane);
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.DISCONNECTED));
		}
	}

	/**
	 *  Clear the SQL statement pane.
	 */	
	public void clearSQL()
	{
		setSQLText("");
	}

	/**
	 * Attemt to connect to a database using the given parameters.
	 */
	private void connect()
	{
		// Get the new provider and switch back to the SQL panel.
		this.provider = loginPane.getConnectionProvider();
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			notifyStatusListeners
				(new StatusEvent(this, StatusTypeEnum.BUSY));
			db = provider.getConnection();
			sPane.setTopComponent(topPane);
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.CONNECTED));
			setMessage("Connection Successful.", false);
			notifyStatusListeners
				(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		} catch (Throwable t) {
			// Unable to connect.
			displayDBConnectError(t);
			notifyStatusListeners
				(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	/**
	 * Notifies the user of an error connecting to a database.
	 * @param t The Throwable object describing the error.
	 */
	private void displayDBConnectError(Throwable t)
	{
		Debug.log(t);
		StringBuffer sb = new StringBuffer("Database Connection Error\n\n");
		sb.append(t.getMessage());
		setMessage(sb.toString(), true);
	}

	/**
	 * Evaluates the current status of the SQL statement and propogates a
	 * StatusEvent if necessary.
	 */
	private void evaluateDataStatus()
	{
		StatusTypeEnum ste = null;
		if (stmtPane.getText().length() > 0 && timer == null) {
			ste = StatusTypeEnum.DATA_AVAIL;
		} else {
			ste = StatusTypeEnum.DATA_NOT_AVAIL;
		}

		boolean b = (stmtPane.getText().length() > 0);
		cancelButton.setEnabled(! (worker instanceof SQLExecutionWorker));
		executeButton.setEnabled(b && timer == null);
		clearButton.setEnabled(b);
		testButton.setEnabled(Debug.on() && b);

		notifyStatusListeners(new StatusEvent(this, ste));
	}

	/**
	 *  Executes the current (selected) SQL statement or BeanShell script.
	 *  @param event ActionEvent The event that was sent.
	 */
	public void executeScript()
	{
		String sql = null;
		try {
		    if (! Preferences.ignoreSelectedText()) {
			    sql = stmtPane.getSelectedText();
		    }
		    if (sql == null || sql.length() == 0) {
		    	sql = stmtPane.getText();
		    }
		    sql = sql.trim();
		    if (sql.length() == 0) {
				getResultSetTableModel().setResultSet(null);
				msgArea.setText("There is no SQL statement or BeanShell Script to execute.");
		    } else {
		    	boolean isScript = isBshScript();
		    	executeButton.setEnabled(false);
				setMessage((isScript ? "Script" : "Statement") + " is being executed...", false);
				if (isScript) {
					worker = new BshExecutionWorker(getConnectionProvider(), sql);
					cancelButton.setEnabled(false);
				} else {
					worker = new SQLExecutionWorker(getDatabaseConnection(), sql);
					cancelButton.setEnabled(true);
				}
				worker.addStatusListener(this);
				timer = new Timer();
				ElapsedTimeEventTimer t = new ElapsedTimeEventTimer(this);
				timer.schedule(t, 500, 500);
				worker.start();
		    }
		} catch (java.lang.Throwable e) {
			UIManager.getLookAndFeel().provideErrorFeedback(msgArea);
			setMessage("ERROR: " + e.getMessage(), true);
		    Debug.log(e);
		}
		stmtPane.requestFocus();
	}

	/**
	 * Used to execute test code.  :-)
	 */
	private void executeTest()
	{
		javax.swing.JFrame frame = (javax.swing.JFrame)getTopLevelAncestor();
		ImageIcon icon = new ImageIcon(getClass().getResource
				("/us/pcsw/dbbrowser/resources/images/SQLPage.png"));
		frame.setIconImage(icon.getImage());
	}

	/**
	 * Takes a long value which is a number of milliseconds and returns a string
	 * representation in h:m:s.SSSS
	 * @param time the number of milliseconds to respresent.
	 * @return the string representation of the time value.
	 */
	private String formatExecuteTime(long time)
	{
		StringBuffer sb = new StringBuffer();
		// hours
		long l = time / 3600000;
		sb.append(l);
		sb.append(':');
		time = time - (l * 3600000);
		// minutes
		l = time / 60000;
		sb.append(l);
		sb.append(':');
		time = time - (l * 60000);
		// seconds
		l = time / 1000;
		sb.append(l);
		sb.append('.');
		time = time - (l * 1000);
		// milliseconds
		sb.append(time);
		return sb.toString();
	}

	/**
	 * Gets a description of the connection
	 */
	public String getConnectionDescription()
	{
		try {
		    Connection con = getDatabaseConnection();
		    StringBuffer sb = new StringBuffer("[");
			sb.append(provider.getServerName());
		    sb.append("].[");
		    sb.append(con.getCatalog());
		    sb.append(']');
		    return sb.toString();
		} catch (Throwable t) {
		    return "[].[]";
		}
	}

    /**
     * Gets the connection provider used by the panel.
     */
    ConnectionProvider getConnectionProvider()
    {
		return provider;
    }

    /**
     * Returns a connection to the database.
     */
    private Connection getDatabaseConnection()
    {
		return db;
    }

	/**
	 * Gets the parent Frame window object.
	 */
    private Frame getFrame()
    {
		Component anc = getTopLevelAncestor();
		if (anc != null && anc instanceof Frame) {
		    return (Frame)anc;
		} else {
		    return null;
		}
    }

    /**
     * Gets the table model containing the results of the SQL query.
     */
    ResultSetTableModel getResultSetTableModel()
    {
    	Component comp = getSelectedOutputPanel();
    	if (comp instanceof ResultSetPanel) {
    		return ((ResultSetPanel)comp).getResultSetTableModel();
    	} else {
    		return null;
    	}
    }
    
    Component getSelectedOutputPanel()
    {
    	return outputTabbedPane.getSelectedComponent();
    }
    
    private JFileChooser getScriptFileChooser()
    {
    	if (sqlChooser == null) {
    		sqlChooser = new JFileChooser();
    		sqlChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    		sqlChooser.addChoosableFileFilter(sqlFileFilter);
    		sqlChooser.addChoosableFileFilter(dbsFileFilter);
    		sqlChooser.setFileFilter(sqlFileFilter);
    	} else if (sqlChooser.getSelectedFile() != null) {
			File f = sqlChooser.getSelectedFile();
			FileFilter[] filters = sqlChooser.getChoosableFileFilters();
			boolean found = false;
			for (int i = 0; i < filters.length; i++) {
				if (filters[i].accept(f)) {
					found = true;
					break;
				}
			}
			if (! found) {
				String ext = BasicFileFilter.getExtension(f);
				sqlChooser.addChoosableFileFilter(
						new BasicFileFilter(ext, ext + " File")
					);
			}
    	}
    	return sqlChooser;
    }
    
    public boolean isBshScript()
    {
    	return stmtPane.getText().startsWith(SCRIPT_IDENTIFIER);
    }
    
    public boolean isSQLScript()
    {
    	return stmtPane.getText().length() > 0 && (! isBshScript());
    }

    /**
     * Gets the text of the SQL statement or BeanShell script displayed in the
     * text pane.
     */
    public String getScriptText()
    {
		return stmtPane.getText();
    }

	/**
	 * Gets the title text for the ConnectionPanel.  This is called from
	 * MainFrame to genereate the tab caption.
	 */
    public String getTitle()
    {
		try {
		    Connection con = getDatabaseConnection();
		    DatabaseMetaData dmd = con.getMetaData();
		    StringBuffer sb = new StringBuffer();
		    sb.append(dmd.getDatabaseProductName());
		    sb.append(" - ");
		    sb.append(getConnectionDescription());
		    return sb.toString();
		} catch (Exception e) {
		    return "[Not Connected]";
		}
    }

	/**
	 * True if there is a valid connection, otherwise false.
	 */
	boolean isConnected()
	{
		return getDatabaseConnection() != null;
	}

	/**
	 * True if an SQL statement is being executed.
	 */
	boolean isStatementExecuting()
	{
		return (timer != null);
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
		Debug.log(exception);
    }

	/**
	 * Indicates whether there are items in the history.
	 */
	public boolean hasHistory()
	{
		return (historyList.getSize() > 0);
	}

	/**
	 * Indicates that the panel is currently displaying a resultset.
	 */
	public boolean hasResultSet()
	{
		ResultSetTableModel model = getResultSetTableModel();
		return (model != null && model.getRowCount() > 0);
	}
	
	/**
	 * Indicates that the panel is currently displaying a script's output.
	 */
	public boolean hasScriptOutput()
	{
		Component comp = outputTabbedPane.getSelectedComponent();
		if (comp instanceof ResultSetPanel) {
			return false;
		} else if (comp == shellOutputScrollPane) {
			return shellOutputPane.getText().length() > 0;
		}
		return false;
	}

    /**
     * Clears the SQL statement history list.
     */
    void historyClear()
    {
		historyList.clear();
    }

    /**
     * Recalls the next item in the history.
     */
    void historyNext()
    {
		if (historyList.getSize() == 0) return;
		int i = historySelection.getAnchorSelectionIndex() + 1;
		if (i < historyList.getSize()) {
		    historySelection.setSelectionInterval(i, i);
		}
		stmtPane.setText
		    ((String)(historyList.getElementAt
		     (historySelection.getAnchorSelectionIndex())));
    }

    /**
     * Recall the previous item in the history.
     */
    void historyPrevious()
    {
		if (historyList.getSize() == 0) return;
		int i = historySelection.getAnchorSelectionIndex() - 1;
		if (i > -1) {
		    historySelection.setSelectionInterval(i, i);
		}
		stmtPane.setText
		    ((String)(historyList.getElementAt
		     (historySelection.getAnchorSelectionIndex())));
    }

    /**
     * Displays a dialog which lists the history.
     */
    void historyShow()
    {
		if (hld == null) {
		    hld = new HistoryListDialog(getFrame(), historyList,
                                        historySelection);
		    hld.addActionListener(this);
		}
		hld.setVisible(true);
    }

    /**
     * Initialize the GUI interface.
     * */
    private void initGUI()
    {
		// Frame Contents
		setLayout(new BorderLayout());
		sPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		sPane.setOneTouchExpandable(true);
		JPanel cPane = new JPanel();
		cPane.setLayout(new BorderLayout());
		
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		
		cPane.setLayout(gbLayout);
	
		// SQL statement edit area
		stmtPane = new JTextPane();
		
		HighlightDocument doc = new HighlightDocument(stmtPane , HighlightDocument.SQL_TYPE);
		
		stmtPane.setDocument(doc);
		
		stmtPane.setDragEnabled(true);
		stmtPane.addCaretListener(this);
		stmtPane.addKeyListener(this);
		stmtPane.getDocument().addUndoableEditListener(sqlUndoMgr);
		stmtPaneScrollPane = new JScrollPane();
		stmtPaneScrollPane.setVerticalScrollBarPolicy
		    (JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		stmtPaneScrollPane.setHorizontalScrollBarPolicy
		    (JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		stmtPaneScrollPane.setViewportView(stmtPane);
		stmtPaneScrollPane.setPreferredSize(new Dimension(450,150));
		
		// Added
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0; 
		c.weighty = 1.0; 
		c.gridwidth = 2;
		c.gridheight = 6;
		gbLayout.setConstraints(stmtPaneScrollPane, c);
		cPane.add(stmtPaneScrollPane);
		
		executeButton = new JButton("Execute");
		executeButton.setMnemonic('x');
		executeButton.setAlignmentX(CENTER_ALIGNMENT);
		executeButton.addActionListener(this);
		executeButton.setEnabled(false);
		executeButton.addKeyListener(this);
		
		c.weightx = 0.0;
		c.weighty = 0.0;
		c.gridx = 2;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.insets = new Insets(6, 5, 0, 5);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridwidth = GridBagConstraints.RELATIVE; 
		c.gridheight = 1; 
		gbLayout.setConstraints(executeButton, c);
		cPane.add(executeButton);

		// cancel button
		cancelButton = new JButton("Cancel");
		cancelButton.setEnabled(true);
		cancelButton.setMnemonic('a');
		cancelButton.setAlignmentX(CENTER_ALIGNMENT);
		cancelButton.addActionListener(this);
		cancelButton.setEnabled(false);
		cancelButton.addKeyListener(this);

		c.gridy = GridBagConstraints.RELATIVE;
		c.insets = new Insets(5, 5, 0, 5);
				
		gbLayout.setConstraints(cancelButton, c);
		cPane.add(cancelButton);

		// seperator
		c.gridy = GridBagConstraints.RELATIVE;
		c.insets = new Insets(5, 5, 0, 5);				
		cPane.add(new JSeparator(JSeparator.HORIZONTAL), c);
		
		// clear button
		clearButton = new JButton("Clear");
		clearButton.setEnabled(true);
		clearButton.setMnemonic('r');
		clearButton.setAlignmentX(CENTER_ALIGNMENT);
		clearButton.addActionListener(this);
		clearButton.setEnabled(false);
		clearButton.addKeyListener(this);

		c.gridy = GridBagConstraints.RELATIVE;
		c.insets = new Insets(5, 5, 0, 5);
				
		gbLayout.setConstraints(clearButton, c);
		cPane.add(clearButton);

		// test button
		testButton = new JButton("Test");
		testButton.setEnabled(false);
		testButton.setMnemonic('T');
		testButton.setVisible(Debug.on());
		testButton.setAlignmentX(CENTER_ALIGNMENT);
		testButton.addActionListener(this);
		testButton.addKeyListener(this);

		c.gridy = GridBagConstraints.RELATIVE;
		c.insets = new Insets(5, 5, 0, 5);

		gbLayout.setConstraints(testButton, c);
		cPane.add(testButton);
		
		// line indicator
		
		c.anchor = GridBagConstraints.SOUTHWEST;
		c.gridy = 5;
		positionLabel = new JLabel("1/1");
		positionLabel.setHorizontalAlignment(JLabel.CENTER);
		gbLayout.setConstraints(positionLabel, c);
		cPane.add(positionLabel);
				
		//	End Added
	
		topPane = cPane;
		//End Add
		
		loginPane = new LoginPanel(provider, false);
		loginPane.addActionListener(this);
		
		if (provider == null) {
			sPane.setTopComponent(loginPane);
		} else {
			try {
				db = provider.getConnection();
				sPane.setTopComponent(topPane);
			} catch (Throwable t) {
				// Unable to connect.
				displayDBConnectError(t);
				sPane.setTopComponent(loginPane);
			}
		}

		outputSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		outputSplitPane.setOneTouchExpandable(true);
	
		// Message area
		JPanel msgPanel = new JPanel();
		msgPanel.setLayout(new BorderLayout());
	
		msgIconLabel = new JLabel();
		msgIconLabel.setHorizontalAlignment(JLabel.CENTER);
		msgIconLabel.setVerticalAlignment(JLabel.CENTER);
		msgIconLabel.setPreferredSize(new Dimension(64, 64));
		msgPanel.add(msgIconLabel, BorderLayout.WEST);
	
		msgArea = new JTextArea();
		msgArea.setEditable(false);
		msgArea.addKeyListener(this);
		msgAreaPane = new JScrollPane();
		msgAreaPane.setViewportView(msgArea);
		msgPanel.add(msgAreaPane, BorderLayout.CENTER);
	
		outputSplitPane.setTopComponent(msgPanel);
		
		// Output
		outputTabbedPane = new JTabbedPane();
		outputTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
		outputTabbedPane.add(new ResultSetPanel(), RESULTS_TAB_PREFIX + "1");
		outputTabbedPane.setMnemonicAt(0, 'R');
		
		shellOutputPane = new JTextPane();
		shellOutputScrollPane = new JScrollPane(shellOutputPane);
		shellOutputScrollPane.setVerticalScrollBarPolicy
			(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		shellOutputScrollPane.setHorizontalScrollBarPolicy
			(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		shellOutputScrollPane.setPreferredSize(new Dimension(450,250));
		outputTabbedPane.add(shellOutputScrollPane, "Shell Output");
		outputTabbedPane.setMnemonicAt(1, 'O');
		
		outputSplitPane.setBottomComponent(outputTabbedPane);
		
		sPane.setBottomComponent(outputSplitPane);

		sPane.setDividerLocation(0.5);
		sPane.setResizeWeight(0.5);

		add(sPane, BorderLayout.CENTER);
    }

	/**
	 * Traps and handles the pressing of the F5 key for execution of the SQL
	 * statement.
	 * @see java.awt.event.KeyListener#keyPressed(KeyEvent)
	 */
	public void keyPressed(KeyEvent e)
	{
		int i = e.getKeyCode();
		
		switch (i) {
//		case KeyEvent.VK_F5 :
//			this.executeStatement();
//			break;
		case KeyEvent.VK_Z :
			// There is no definition for UNDO in the metal look and feel.
			// Therefore, this code will cause the key sequence Ctrl+Z to
			// perform an undo operation on the stmtPane under all look and
			// feels.
			if (e.getModifiersEx() == KeyEvent.CTRL_DOWN_MASK &&
			    stmtPane.hasFocus())
			{
			    sqlUndoMgr.undo();
			}
		}
	}

	/**
	 * Responds to key released events fired by the frame.
	 * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
	 */
	public void keyReleased(KeyEvent e) {
		if (e.getSource().equals(stmtPane)) {
			evaluateDataStatus();
		}
	}

	/**
	 * This method does nothing.  No key typed events are trapped.
	 * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
	 */
	public void keyTyped(KeyEvent e) {}

	/**
	 * If there is an open connection, it is disconnected.  Loads the saved
	 * connection parameters from a connection file.
	 * @param file The connection file.
	 */
	public void loadConnectionParameters(File file)
	{
		if (loginPane.loadConnectionParameters(file)) {
			closeDatabaseConnection();
		}
	}

	/**
	 * Reads an SQL statement or BeanShell script from file into the text display.
	 */
	public void loadFile()
	{
		try {
			BufferedReader inFile = null;
			int response;
			File file = null;
			StringBuffer buffer = null;
			
			JFileChooser sqlChooser = getScriptFileChooser();
	
		    // Get the path/name of the file to save the file to.
		    sqlChooser.setDialogTitle("Open File");
		    response = sqlChooser.showOpenDialog(this);
		    if (response == JFileChooser.APPROVE_OPTION) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.BUSY));
				file = sqlChooser.getSelectedFile();
				// Test for file's existance.
				if (! file.exists()) {
				    throw new IOException("The file " + file.getName() +
										  " does not exists.");
				}
				// Read the SQL script
				inFile = new BufferedReader(new FileReader(file));
			    buffer = new StringBuffer();
			    boolean newline = false;
				while (inFile.ready()) {
					// I could test buffer.length() each time, but why require
					// an expensive method call?
					if (newline) {
						buffer.append("\n");
					} else {
						newline = true;
					}
			    	buffer.append(inFile.readLine());
				}
				inFile.close();
				setSQLText(buffer.toString());
				setMessage("SQL script read from " + file.getName() +	".", false);
				boolean b = (stmtPane.getText().length()> 0);
				executeButton.setEnabled(b);
				cancelButton.setEnabled(! (worker instanceof SQLExecutionWorker));
				clearButton.setEnabled(b);
				testButton.setEnabled(Debug.on() && b);
	
				notifyStatusListeners
					(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		    }
		} catch (Throwable t) {
			notifyStatusListeners
				(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			handleException(t);
		}
	}

	/**
	 * Notifies all listeners of a status event.
	 * @param ae The event to notify the listeners of.
	 */
	private void notifyStatusListeners(StatusEvent se)
	{
		// Only report the data available status if it is different than the
		// last reported.  evaluateDataStatus fires a new event every
		// keystroke, but we do not want to drown the listeners in events.
		if (se.getType() == StatusTypeEnum.DATA_AVAIL &&
		    se.getType() == stmtDataStatus) {
			return;
		} else if (statusListeners != null) {
			stmtDataStatus = se.getType();

			// Clone the vector because we do not want to run into trouble if a
			// listener adds or removes itself while we are iterating through the
			// enumeration. 
			Vector clone = (Vector)statusListeners.clone();
			StatusListener listener;
			Enumeration e = clone.elements();
			while (e.hasMoreElements()) {
				listener = (StatusListener)e.nextElement();
				listener.statusChanged(se);
			}
		}
	}	
	
	public void removeChangeListener(ChangeListener l)
	{
		Object o;
		for (Iterator iter = changeListeners.iterator(); iter.hasNext();) {
			o = iter.next();
			if (o instanceof RepeatingChangeListener) {
				if (((RepeatingChangeListener)o).innerListener.equals(l)) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * Removes the status listener from those that should be notified of
	 * status events.
	 * @param listener The listener.
	 */
	public void removeStatusListener(StatusListener listener)
	{
		if (statusListeners != null) {
			statusListeners.remove(listener);
		}
	}

	/**
	 * Forces the ConnectionPanel to re-get preference settings from the
	 * us.pcsw.dbbrowser.Preferences object and reapply them.
	 */
	void reloadPreferences()
	{
		int ts = Preferences.getSQLTabSize();
		HighlightDocument doc = (HighlightDocument)stmtPane.getStyledDocument();
		doc.setTabs(ts);
	}

	/**
	 * Saves info about the current connection to a file.
	 */
	public void saveConnectionInfo()
	{
		if (getConnectionProvider() == null) {
			return;
		}
		try {
			JFileChooser fileChooser = new JFileChooser(); 
			fileChooser = new JFileChooser("Create DBBrowser Connection File");
			fileChooser.setFileFilter(new BasicFileFilter("dbc", "DBBrowser Connection Parameters"));	
			int response = fileChooser.showSaveDialog(this);
			if (response == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				// Test for file's existance and user's willingness to
				//  overwrite it.
				if (file.exists()) {
					response = JOptionPane.showConfirmDialog
						(null, 
						 "Overwrite the existing file " + file.getName() + "?",
						 "Overwrite Existing File",
						 JOptionPane.YES_NO_OPTION);
				} else {
					response = JOptionPane.YES_OPTION;
				}
				if (response == JOptionPane.YES_OPTION) {
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					notifyStatusListeners
						(new StatusEvent(this, StatusTypeEnum.BUSY));
					
					getConnectionProvider().save(file);
					
					notifyStatusListeners
						(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
					setCursor
						(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
					
		} catch (Throwable t) {
			notifyStatusListeners
				(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			handleException(t);
		}
	}

	/**
	 * Saves the results of the last statement to file.  Currently, only the
	 * CSV format is supported.
	 */
	public void saveResultSet()
	{
		try {
			if (hasResultSet()) {
				notifyStatusListeners
					(new StatusEvent(this, StatusTypeEnum.BUSY));
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				TableModelExport.saveTableModel(this, this.getResultSetTableModel(), "SQL Results");
			} else if (hasScriptOutput()) {
				JFileChooser fchooser = new JFileChooser();
				fchooser.setFileFilter(new BasicFileFilter("sql", "SQL Script"));
				fchooser.setFileFilter(new BasicFileFilter("dbs", "DBBrowser Script"));
				fchooser.setFileFilter(new BasicFileFilter("txt", "Text File"));
				fchooser.setDialogTitle("Save Script Output");
				fchooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fchooser.showSaveDialog(this);
				File f = fchooser.getSelectedFile();
				if (f != null) {
					notifyStatusListeners
						(new StatusEvent(this, StatusTypeEnum.BUSY));
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					Writer w = new FileWriter(f);
					shellOutputPane.write(w);
					w.close();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					notifyStatusListeners
						(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
				}
			}
		} catch (Throwable t) {
			notifyStatusListeners
				(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			handleException(t);	
		}
	}

	/**
	 * Saves the SQL statement or BeanShell script to file.
	 */
	public void saveFile()
	{
		try {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.BUSY));
			File file = null;
			FileWriter outFile = null;
			int response;
			
			JFileChooser sqlChooser = getScriptFileChooser();
			if (isBshScript()) {
				sqlChooser.setFileFilter(dbsFileFilter);
			} else if (isSQLScript()) {
				sqlChooser.setFileFilter(sqlFileFilter);
			} else {
				JOptionPane.showMessageDialog(
						this, "There is nothing to save.",
						"Cannot Save", JOptionPane.INFORMATION_MESSAGE
					);
				return;
			}
			
	    	// Get the path/name of the file to save the file to.
			sqlChooser.setDialogTitle("Save File");
		    response = sqlChooser.showSaveDialog(this);
	    	if (response == JFileChooser.APPROVE_OPTION) {
				file = sqlChooser.getSelectedFile();
				// Test for file's existance and user's willingness to
				//  overwrite it.
				if (file.exists()) {
				    response = JOptionPane.showConfirmDialog
						(null, 
						 "Overwrite the existing file " + file.getName() + "?",
						 "Overwrite Existing File",
						 JOptionPane.YES_NO_OPTION);
				} else {
				    response = JOptionPane.YES_OPTION;
				}
				if (response == JOptionPane.YES_OPTION) {
				    // Save the SQL script
		    		setCursor
		    			(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				    outFile = new FileWriter(file);
				    stmtPane.write(outFile);
				    outFile.close();
				    setMessage((isSQLScript() ? "SQL" : "DBBrowser") + " script saved to " + file.getName() + ".", false);
					notifyStatusListeners
						(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
				    setCursor
						(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				}
			}
		} catch (Throwable t) {
			notifyStatusListeners
				(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			handleException(t);
		}
	}

	/**
	 * If set to true, controls allowing the user to submit SQL queries are
	 * enabled.  If false, the same controls are disabled.
	 */
    protected void setActionEnabled(boolean enabled)
    {
		stmtPane.setEnabled(enabled);
    }

	/**
	 * Sets the default button for the provided root pane to the connect
	 * button of this panel.
	 * @param rootPane
	 */
	public void setDefaultButtonForRootPanel(JRootPane rootPane)
	{
		// If the loginPane is not displayed, it will not recieve the
		// default keystroke.
		loginPane.setDefaultButtonForRootPanel(rootPane);
	}

	/**
	 * Updates the message area of the panel with the given text.
	 * @param message The message to display.
	 * @param errorMessage True if the message is a result of an error,
	 *                      otherwise, the message is understood to be
	 *                      informational.
	 */
    void setMessage(String message, boolean errorMessage)
    {
		msgArea.setText(message);
		if (errorMessage) {
			msgIconLabel.setIcon(errorIcon);
			int i = outputSplitPane.getDividerLocation();
			// if i == 1, the message area has been collapsed.
			if (i == 1) { 
				outputSplitPane.resetToPreferredSizes();
			}
		} else {
			msgIconLabel.setIcon(infoIcon);
		}
    }

    /**
     * Sets the text of the SQL statement displayed in the SQL text pane.
     */
    public void setSQLText(String sql)
    {
		stmtPane.setText(sql);
		evaluateDataStatus();
    }

    /**
     * Changes the connection information used by the panel.
     */
    public void setConnectionProvider(ConnectionProvider provider)
    {
		this.provider = provider;
		db = null;
    }

	public void scriptExecuted(StatusEvent se) {
		// This is the result of a beanshell script being run.  Display the results.
		BshExecutionWorker worker = (BshExecutionWorker)se.getSource();
		BshExecutionResults results = (BshExecutionResults)worker.get();
		StringBuffer executeMsg = new StringBuffer
				("Beanshell script has been executed.  Execution time ");
		executeMsg.append(formatExecuteTime(results.getRunTimeMills()));
		executeMsg.append('.');

		int count = results.getResultSetModelList().size();
		
		// Delete any unneeded ResultSetPanels.  Always leave the first panel
		// and the last component, which will be the script output panel.
		while (outputTabbedPane.getComponentCount() > 2 && (outputTabbedPane.getComponentCount() - 1) > count) {
			outputTabbedPane.remove(outputTabbedPane.getComponentCount() - 2);
		}
		
		if (count == 0) {
			((ResultSetPanel)outputTabbedPane.getComponentAt(0)).setResultSetTableModel(null);
		} else {
			// Add any needed ResultSetPanels.  Make sure that the last component
			// is the script output panel.
			for (int i = outputTabbedPane.getComponentCount(); i < count + 1; i++) {
				outputTabbedPane.add(new ResultSetPanel(), RESULTS_TAB_PREFIX + String.valueOf(i), i - 1);
			}
			
			// Set resultset models
			List list = results.getResultSetModelList();
			for (int i = 0; i < count; i++) {
				((ResultSetPanel)outputTabbedPane.getComponentAt(i)).setResultSetTableModel((ResultSetTableModel)list.get(i));
			}
		}
		
		for (Iterator iter = results.getExceptionList().iterator(); iter.hasNext(); ) {
			executeMsg.append("\n" + createErrorOutput((Throwable)iter.next()));
		}
		
		shellOutputPane.setText(results.getOutput());
		
		setMessage(executeMsg.toString(), false);
		
		if (results.getExceptionList().size() > 0) {
			UIManager.getLookAndFeel().provideErrorFeedback(msgArea);
		}
	
		// Add the latest statement to the list and set it as the
		// selected one.
		historyList.addStatement(worker.getScript());
		historySelection.setSelectionInterval(
				historyList.getSize() - 1,
				historyList.getSize() - 1
			);
	}

	public void sqlExceptionThrown(Throwable t)
	{
		// An error was encountered running the statement.
		// Display the error.
		UIManager.getLookAndFeel().
			provideErrorFeedback(msgArea);
		setMessage(createErrorOutput(t), true);
		Debug.log(t.getMessage());
	}
	
	private String createErrorOutput(Throwable t)
	{
		StringBuffer executeMsg = new StringBuffer("ERROR: ");
		if (t instanceof EvalError) {
			executeMsg.append("LINE: ");
			try {
				executeMsg.append(((EvalError)t).getErrorLineNumber());
			} catch (NullPointerException e) {
				// There have been times where I get NPEs
			}
			if (t instanceof TargetError) {
				executeMsg.append("\t");
				try {
					executeMsg.append(((EvalError)t).getErrorText());				
				} catch (NullPointerException e) {
					// There have been times where I get NPEs
				}
				executeMsg.append("\n\t");
				try {
					executeMsg.append(((TargetError)t).getTarget().getMessage());
				} catch (Exception e) {
					executeMsg.append(t.getMessage());
				}
			} else {
				executeMsg.append("\n\t");
				try {
					executeMsg.append(((EvalError)t).getErrorText());				
				} catch (Exception e) {
					executeMsg.append(t.getMessage());
				}
			}
		} else {
			executeMsg.append(t.getMessage());
		}
		return executeMsg.toString();
	}

	public void sqlExecuted(StatusEvent se) {
		// This is the result of an SQL statement being run.  Display the results.
		SQLExecutionWorker worker = (SQLExecutionWorker)se.getSource();
		SQLExecutionResults results = (SQLExecutionResults)worker.get();
		StringBuffer executeMsg = new StringBuffer
				("SQL statement has been executed.  Execution time ");
		executeMsg.append(formatExecuteTime(results.getRunTimeMills()));
		executeMsg.append('.');

		int count = results.getResultSetModelList().size();
		
		// Delete any unneeded ResultSetPanels.  Always leave the first panel
		// and the last component, which will be the script output panel.
		while (outputTabbedPane.getComponentCount() > 2 && (outputTabbedPane.getComponentCount() - 1) > count) {
			outputTabbedPane.remove(outputTabbedPane.getComponentCount() - 2);
		}
		
		if (count == 0) {
			((ResultSetPanel)outputTabbedPane.getComponentAt(0)).setResultSetTableModel(null);
		} else {
			// Add any needed ResultSetPanels.  Make sure that the last component
			// is the script output panel.
			for (int i = outputTabbedPane.getComponentCount(); i < count + 1; i++) {
				outputTabbedPane.add(new ResultSetPanel(), RESULTS_TAB_PREFIX + String.valueOf(i), i - 1);
			}
			
			// Set resultset models
			List list = results.getResultSetModelList();
			for (int i = 0; i < count; i++) {
				((ResultSetPanel)outputTabbedPane.getComponentAt(i)).setResultSetTableModel((ResultSetTableModel)list.get(i));
			}
		}

		for (Iterator iter = results.getResultCountList().iterator(); iter.hasNext(); ) {
			executeMsg.append('\n');
			executeMsg.append(iter.next().toString());
			executeMsg.append(" records affected.");
		}
		
		for (Iterator iter = results.getExceptionList().iterator(); iter.hasNext(); ) {
			executeMsg.append('\n');
			executeMsg.append(((Throwable)iter.next()).getMessage());
		}
		
		setMessage(executeMsg.toString(), false);
		
		if (results.getExceptionList().size() > 0) {
			UIManager.getLookAndFeel().provideErrorFeedback(msgArea);
		}
	
		// Add the latest statement to the list and set it as the
		// selected one.
		historyList.addStatement(worker.getSQL());
		historySelection.setSelectionInterval(
				historyList.getSize() - 1,
				historyList.getSize() - 1
			);
	}

	/**
	 * Notification that of a status change.
	 * @param se The StateEvent object which holds status change information.
	 */
    public void statusChanged(StatusEvent se) {
    	if (se.getSource() instanceof ExecutionWorker)
    	{
    		if (se.getType() == StatusTypeEnum.NOT_BUSY) {
	    		try {
					if (se.getData() instanceof Throwable) {
						sqlExceptionThrown((Throwable)se.getData());
					} else if (
							(worker instanceof SQLExecutionWorker) &&
							((SQLExecutionWorker)worker).isCancelled()
						)
					{
						setMessage("SQL statement has been cancelled.", false);
					} else {
						if (worker instanceof SQLExecutionWorker) {
							sqlExecuted(se);
						} else {
							scriptExecuted(se);
						}
					}
	    		} catch (Throwable t) {
					UIManager.getLookAndFeel().provideErrorFeedback(msgArea);
					setMessage("ERROR: " + t.getMessage(), true);
					Debug.log(t);
					notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));
					notifyStatusListeners
						(new StatusEvent(this, StatusTypeEnum.NOT_BUSY));
	    		} finally {
	    			// Disable the timer.
	    			if (timer != null) {
	    				timer.cancel();
	    				timer = null;
	    			}
					worker = null;

	    			// Update the UI.
					evaluateDataStatus();
					repaint();	// Mark this component as "dirty".
					// Find the parent component and make it repaint.
					java.awt.Component com = this, last = this;
					while (com != null) {
						last = com;
						com = last.getParent();
					}
					last.repaint();
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	    		}
    		}
		} else if (se.getType() == StatusTypeEnum.TIME_ELAPSED) {
			setMessage("Statement is being executed... Elapsed time: " +
			           this.formatExecuteTime(((Long)se.getData()).longValue()),
			           false);
		}
		// Propogate the statement.
		notifyStatusListeners(new StatusEvent(this, se.getType(), se.getDescription(), se.getSource()));
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
