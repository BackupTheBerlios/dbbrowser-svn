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
package us.pcsw.dbbrowser;

import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder; 
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Attr; 
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * us.pcsw.dbbrowser.Preferences
 * -
 * Class which provides preference settings through static method calls.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Oct 18, 2002	This class was created by Philip A. Chapman.</LI>
 * <LI>Nov 07, 2002 Added the getSQLFontName and getSQLFontSize methods.</LI>
 * <LI>Mar 13, 2003 Added option to disable lazy load of cache.</LI>
 * <LI>Mar 14, 2003 Added setting for cache page size so that even if the
 *                  cache is loaded lazily, the user can control how many rows
 *                  are loaded into the cache at a time.</LI>
 * <LI>Mar 19, 2003 Changed the underlying file structure of persisted
 *                  preferences to a XML document.  Merged the connection
 *                  provider list into the single config file.  PAC </LI>
 * <LI>Mar 20, 2003 Fixed bug in code which loads preferences.  PAC </LI>
 * <LI>Mar 21, 2003 Changed the name of the root element because of conflicts
 *                  with the SQL spec. PAC </LI>
 * <LI>Mar 24, 2003 Added the storeConnectionSecrets methods.  PAC </LI>
 * <LI>Mar 28, 2003 Added the displayToolbarLabels methods.  PAC </LI>
 * <LI>May 29, 2003 Added the dbBrowserBounds and dbViewBounds properties and
 *                  related methods.  PAC </LI>
 * <LI>Jul 25, 2003 Corrected typo in the class names for default connection
 *                  providers in the getConnectionProviderModel() method.
 *                  PAC </LI>
 * <LI>Sep 24, 2003 Added the Result Font get and set methods.  PAC </LI>
 * <LI>Sep 25, 2003 Fixed a bug in the saving of result font size.  PAC </LI>
 * <LI>Jan 06, 2004 Made changes so that the look and feel defaults to
 *                  "Windows" on Microsoft platforms.  PAC </LI>
 * </UL></P>
 */
public final class Preferences
	extends Object
{
	/**
	 * Constants which define the perference keys.
	 */
	private static final String BOUNDS_HEIGHT = "Height";
	private static final String BOUNDS_LEFT = "Left";
	private static final String BOUNDS_TOP = "Top";
	private static final String BOUNDS_WIDTH = "Width";
	private static final String CACHE_PAGE_SIZE = "CachePageSize";
	private static final String CON_PROVIDER = "ConnectionProvider";
	private static final String CON_PROVIDER_ATTR_NAME = "name";
	private static final String CON_PROVIDER_ATTR_CLASS = "class";
	private static final String CON_PROVIDER_LIST = "ConnectionProviderList";
	private static final String DBBROWSER_BOUNDS = "DBBrowserBounds";
	private static final String DBVIEW_BOUNDS = "DBViewBounds";
	private static final String DISPLAY_TB_LABELS = "DisplayToolbarLabels";
	private static final String ELEM_ROOT = "DBBrowserPreferences";
	private static final String IGNORE_SEL_TEXT = "IgnoreSelectedText";
	private static final String LOOK_AND_FEEL = "LookAndFeel";
	private static final String REP_FOR_NULL = "RepresentationForNull";
	private static final String RES_FONT_NAME = "ResultsFontName";
	private static final String RES_FONT_SIZE = "ResultsFontSize";
	private static final String SQL_FONT_NAME = "SQLFontName";
	private static final String SQL_FONT_SIZE = "SQLFontSize";
	private static final String SQL_TAB_SIZE = "SQLTabSize";
	private static final String STORE_CON_SECRETS = "StoreConnectionSecrets";
	
	/**
	 * The paging size to be used for resultset caches.
	 */
	private static int cachePageSize = 0;
	
	/**
	 * The configuration file.
	 */
	private static File configFile = null;
	
	/**
	 * The list of connection providers.
	 */
	private static ConnectionProviderModel connectionProviders = null;
	
	/**
	 * The startup position for the main frame of the dbbrowser app.
	 */
	private static Rectangle dbBrowserBounds = null;
	
	/**
	 * The startup position for the DB view dialog.
	 */
	private static Rectangle dbViewBounds = null;
	
	/**
	 * Whether toolbar labels should be displayed.
	 */
	private static boolean dispToolbarLbl = true;
	
	/**
	 * Whether the parser should ignore selected text when running an SQL
	 * statement.
	 */
	private static boolean ignoreSelectedText = false;
	
	/**
	 * The prefered look and feel.
	 */
	private static String lookAndFeel = "";
	
	/**
	 * What string is to be used to represent null.
	 */
	private static String repForNull = "(null)";

	/**
	 * The name of the font to use for displaying results.
	 */
	private static String resFontName = "Monospaced";
	
	/**
	 * The font size to use for displaying results.
	 */
	private static int resFontSize = 10;
		
	/**
	 * The name of the font to use for displaying SQL statements.
	 */
	private static String sqlFontName = "Monospaced";
	
	/**
	 * The font size to use for displaying SQL statements.
	 */
	private static int sqlFontSize = 10;
	
	/**
	 * The number of spaces in a tab when displaying SQL statements.
	 */
	private static int sqlTabSize = 5;

	/**
	 * If true, indicates that "secret" fields (such as passwords) should
	 * be persisted along with other connection parameters.
	 */
	private static boolean storeConSecrets = false;

	/**
	 * Attempts to load the preferences from the file .dbbrowser_rc in the
	 * user's home directory.
	 * @return True If the initialization was good, false if there were errors
	 *               causing the app to fall back to defaults.
	 */
	static boolean initialize()
	{
    	StringBuffer rcPath;

	    // Look for the .rc file
	    rcPath = new StringBuffer(System.getProperty("user.home"));
	    rcPath.append(File.separator);
	    rcPath.append(".dbbrowser.cfg");
	    return initialize(new File(rcPath.toString()));
	}
	
	/**
	 * Attempts to load the preferences from the given file.
	 * @param file The configuration file from which preferences should be
	 *              loaded and to which they should be written. 
	 * @return True If the initialization was good, false if there were errors
	 *               causing the app to fall back to defaults.
	 */
	static boolean initialize(File file)
	{
		// Keep track of errors so that we can return an appropriate value.
		boolean loadError = false;
		boolean parseError = false;

		// Other needed variables
		Attr attr = null;
		NodeList nl = null;
		NamedNodeMap nnm = null;
		String nodeText = null;
				
		configFile = file;

		if (file.exists()) {
			try {
				// Attempt to load preferences
				DocumentBuilderFactory factory =
					DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document doc = builder.parse(file); 
	
				// Cache page size
				try {
					nodeText = getNodeText(doc, CACHE_PAGE_SIZE);
					cachePageSize = Integer.parseInt(nodeText);
				} catch (Throwable t) {
					parseError = true;
				}

				// DBBrowser's main frame's default dimensions
				nl = doc.getElementsByTagName(DBBROWSER_BOUNDS);
				if (nl != null && nl.getLength() > 0) {
					attr = null;
					nnm = null;
					
					nnm = nl.item(0).getAttributes();
					if (nnm == null) {
						parseError = true;
					} else {
						try {
							dbBrowserBounds = new Rectangle();
							
							attr = (Attr)nnm.getNamedItem(BOUNDS_HEIGHT);
							dbBrowserBounds.height = Integer.parseInt(attr.getValue());
							attr = (Attr)nnm.getNamedItem(BOUNDS_LEFT);
							dbBrowserBounds.x = Integer.parseInt(attr.getValue());
							attr = (Attr)nnm.getNamedItem(BOUNDS_TOP);
							dbBrowserBounds.y = Integer.parseInt(attr.getValue());
							attr = (Attr)nnm.getNamedItem(BOUNDS_WIDTH);
							dbBrowserBounds.width = Integer.parseInt(attr.getValue());
						} catch (Throwable t) {
							parseError = true;
							dbBrowserBounds = null;
						}
					}
				}
				
				// DBBrowser's database view dialog's default dimensions
				nl = doc.getElementsByTagName(DBVIEW_BOUNDS);
				if (nl != null && nl.getLength() > 0) {
					attr = null;
					nnm = null;
					
					nnm = nl.item(0).getAttributes();
					if (nnm == null) {
						parseError = true;
					} else {
						try {
							dbViewBounds = new Rectangle();
							
							attr = (Attr)nnm.getNamedItem(BOUNDS_HEIGHT);
							dbViewBounds.height = Integer.parseInt(attr.getValue());
							attr = (Attr)nnm.getNamedItem(BOUNDS_LEFT);
							dbViewBounds.x = Integer.parseInt(attr.getValue());
							attr = (Attr)nnm.getNamedItem(BOUNDS_TOP);
							dbViewBounds.y = Integer.parseInt(attr.getValue());
							attr = (Attr)nnm.getNamedItem(BOUNDS_WIDTH);
							dbViewBounds.width = Integer.parseInt(attr.getValue());
						} catch (Throwable t) {
							parseError = true;
							dbViewBounds = null;
						}
					}
				}
				
				// Display toolbar labels
				try {
					nodeText = getNodeText(doc, DISPLAY_TB_LABELS);
					dispToolbarLbl = Boolean.valueOf(nodeText).booleanValue();
				} catch (Throwable t) {
					parseError = true;
				}

				// Ignore selected text
				try {
					nodeText = getNodeText(doc, IGNORE_SEL_TEXT);
					ignoreSelectedText = Boolean.valueOf(nodeText).booleanValue();
				} catch (Throwable t) {
					parseError = true;
				}

				// Default look and feel
				nodeText = getNodeText(doc, LOOK_AND_FEEL);
				lookAndFeel = nodeText;
				if (lookAndFeel == null || lookAndFeel.length() == 0) {
					// Set reasonable defaults.
					if (java.lang.System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
						lookAndFeel = "windows";
					}
				}
				
				// Results font name
				nodeText = getNodeText(doc, RES_FONT_NAME);
				if (nodeText == null || nodeText.length() == 0) {
					parseError = true;
				} else {
					resFontName = nodeText;
				}

				// Results font size
				try {
					nodeText = getNodeText(doc, RES_FONT_SIZE);
					resFontSize = Integer.parseInt(nodeText);
				} catch (Throwable t) {
					parseError = true;
				}

				// SQL font name
				nodeText = getNodeText(doc, SQL_FONT_NAME);
				if (nodeText == null || nodeText.length() == 0) {
					parseError = true;
				} else {
					sqlFontName = nodeText;
				}

				// SQL font size
				try {
					nodeText = getNodeText(doc, SQL_FONT_SIZE);
					sqlFontSize = Integer.parseInt(nodeText);
				} catch (Throwable t) {
					parseError = true;
				}

				// SQL tab size
				try {
					nodeText = getNodeText(doc, SQL_TAB_SIZE);
					sqlTabSize = Integer.parseInt(nodeText);
				} catch (Throwable t) {
					parseError = true;
				}

				// Representation for null
				nodeText = getNodeText(doc, REP_FOR_NULL);
				if (nodeText == null) {
					parseError = true;
				} else {
					repForNull = nodeText;
				}
				
				// Store connection secrets
				try {
					nodeText = getNodeText(doc, STORE_CON_SECRETS);
					storeConSecrets = Boolean.valueOf(nodeText).booleanValue();
				} catch (Throwable t) {
					parseError = true;
				}
				
				// Connection providers
				nl = doc.getElementsByTagName(CON_PROVIDER);
				if (nl != null && nl.getLength() > 0) {
					Attr classAttr = null;
					Attr nameAttr = null;
					nnm = null;
					
					connectionProviders = new ConnectionProviderModel();
					int m = nl.getLength();
					
					for (int i = 0; i < m; i++) {
						nnm = nl.item(i).getAttributes();
						if (nnm == null) {
							parseError = true;
						} else {
							try {
								classAttr = (Attr)nnm.getNamedItem(CON_PROVIDER_ATTR_CLASS);
								nameAttr = (Attr)nnm.getNamedItem(CON_PROVIDER_ATTR_NAME);
								connectionProviders.addConnectionProvider
									(nameAttr.getValue(), classAttr.getValue());
							} catch (Throwable t) {
								parseError = true;
							}
						}
					}
				}
			} catch (Throwable t) {
				loadError = true;
			}
		} else {
			loadError = true;
		}
		return (! (loadError || parseError));
	}
	
	/**
	 * Indicates whether labels should be displayed on toolbars.
	 * @return True if labels should be displayed.
	 */
	public static boolean displayToolbarLabels()
	{
		return dispToolbarLbl;
	}
	
	/**
	 * Sets whether labels should be displayed on toolbars.
	 * @param display True if labels should be displayed.
	 */
	public static void displayToolbarLabels(boolean display)
	{
		dispToolbarLbl = display;
	}

	
	/**
	 * Gets the size of cache pages (in rows).  If the value is 0, the entire
	 * resultset is cached from the start.  The default is 0.
	 * @returns Number indicating how many rows at a time will be cached in
	 *          ResultSetTableModel.
	 */
	public static int getCachePageSize()
	{
		return cachePageSize;
	}
	
	/**
	 * Gets the list of ConnectionProviders to be used by the login dialog.
	 */
	public static ConnectionProviderModel getConnectionProviderModel()
	{
		if (connectionProviders == null) {
			connectionProviders = new ConnectionProviderModel();
			// Add the standard providers that come with dbbrowser by default.
			connectionProviders.addConnectionProvider("Generic JDBC", "us.pcsw.dbbrowser.cp.jdbc.ConnectionProvider");
			connectionProviders.addConnectionProvider("MS SQL Server", "us.pcsw.dbbrowser.cp.mssql.ConnectionProvider");
			connectionProviders.addConnectionProvider("MySQL", "us.pcsw.dbbrowser.cp.mysql.ConnectionProvider");
			connectionProviders.addConnectionProvider("Oracle", "us.pcsw.dbbrowser.cp.oracle.ConnectionProvider");
			connectionProviders.addConnectionProvider("PostgreSQL", "us.pcsw.dbbrowser.cp.postgresql.ConnectionProvider");
		}
		return connectionProviders;
	}

	/**
	 * Returns the startup position for the main frame of the dbbrowser app.
	 * Note that the Rectangle object that is returned is mutable.  Any
	 * changes to the values of the rectangle object will be saved to the
	 * configuration file.  Null may be returned if there is no default saved
	 * into the configuration file.
	 */
	public static Rectangle getDbBrowserBounds()
	{
		return dbBrowserBounds;
	}
	
	/**
	 * Returns the startup position for the DB view dialog.  Note that the
	 * Rectangle object that is returned is mutable.  Any changes to the
	 * values of the rectangle object will be saved to the configuration file.
	 * Null may be returned if there is no default saved into the
	 * configuration file.
	 */
	public static Rectangle getDbViewBounds()
	{
		return dbViewBounds;
	}

	/**
	 * Returns the name of the prefered look and feel; or a zero length string
	 * if there is no preferred.
	 */
	public static String getLookAndFeelName()
	{
		if (lookAndFeel == null || lookAndFeel.length() == 0) {
			if (java.lang.System.getProperty("os.name").toLowerCase().indexOf("windows") > -1) {
				lookAndFeel = "Windows";
			}
		}
		return lookAndFeel;
	}

	/**
	 * Returns the text contained within a node.  This code assumes that
	 * there is only one node with the given name and that it does not
	 * contain other nodes.
	 * @param doc The document being parsed.
	 * @param nodeName The name of the node from which the text is to be
	 *                  retrieved.
	 * @return String The text contains within the node, or null if the
	 *                 node does not exist, or there are errors.
	 */
	private static String getNodeText(Document doc, String nodeName)
	{
		try {
			Node node = null;
			NodeList nl = null;
			CharacterData cData = null;
			
			nl = doc.getElementsByTagName(nodeName);
			if (nl == null || nl.getLength() == 0) {
				return null;
			} else {
				node = nl.item(0);
				cData = (CharacterData)node.getChildNodes().item(0);
				return cData.getData();
			}
		} catch (Throwable t) {
			return null;
		}
	}

	/**
	 * Convenience method that uses the resFontName and resFontSize properties
	 * to build a Font object to be used when displaying results.
	 */
	public static Font getResultsFont()
	{
		return new Font(Preferences.getResultsFontName(), Font.PLAIN, Preferences.getResultsFontSize());
	}
	

	/**
	 * Gets the name of the font to be used when displaying results.
	 */
	private static String getResultsFontName()
	{
		return resFontName;
	}
	
	/**
	 * Gets the point size of the font to be used when displaying results.
	 */
	private static int getResultsFontSize()
	{
		return resFontSize;
	}

	/**
	 * Convenience method that uses the sqlFontName and sqlFontSize properties
	 * to build a Font object to be used when displaying SQL text.
	 */
	public static Font getSQLFont()
	{
		return new Font(Preferences.getSQLFontName(), Font.PLAIN, Preferences.getSQLFontSize());
	}
	

	/**
	 * Gets the name of the font to be used when displaying SQL text.
	 */
	private static String getSQLFontName()
	{
		return sqlFontName;
	}
	
	/**
	 * Gets the point size of the font to be used when displaying SQL text.
	 */
	private static int getSQLFontSize()
	{
		return sqlFontSize;
	}

	/**
	 * Gets the number of spaces to which tabs are expanded for SQL text.
	 */
	public static int getSQLTabSize()
	{
		return sqlTabSize;
	}

	/**
	 * Gets the string which is to be used to represent a null field value in
	 * the database.
	 */
	public static String getRepresentationForNull()
	{
		return repForNull;
	}

	/**
	 * Returns true if selected text should be ignored and the complete
	 * contents of the sql text pane is run as a SQL statement.  Returns false
	 * if selected text only is run as a complete SQL statement  If there is
	 * no selected text, the entire contents of the sql text pane is run as a
	 * SQL statement.
	 */
	public static boolean ignoreSelectedText()
	{
		return ignoreSelectedText;
	}

	/**
	 * Sets the property which indicates whether selected text is run
	 * individually as a complete SQL text, or whether the entire contents
	 * of the sql text pane is run as a SQL statement.
	 */
	public static void ignoreSelectedText(boolean ignoreSelected)
	{
		ignoreSelectedText = ignoreSelected;
	}

	/**
	 * Saves the preferences to the file .dbbrowser_rc in the user's home
	 * directory.
	 */
	public static void save()
		throws IOException, ParserConfigurationException,
		        TransformerConfigurationException, TransformerException
	{
	    // First, build the document.
	    Element elem = null;
	    Element rootElem = null;
	    Text text = null;
	    
		DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		rootElem = doc.createElement(ELEM_ROOT);
		doc.appendChild(rootElem);

		// Cache page size
		elem = doc.createElement(CACHE_PAGE_SIZE);
		text = doc.createTextNode(String.valueOf(cachePageSize));
		elem.appendChild(text);
		rootElem.appendChild(elem);
		
		// Connection providers
		ConnectionProviderModelItem cpmi = null;
		Element cpElement = doc.createElement(CON_PROVIDER_LIST); 
		int m = connectionProviders.getRowCount();
		for (int i = 0; i < m; i++) {
			cpmi = (ConnectionProviderModelItem)connectionProviders.getElementAt(i);
			elem = doc.createElement(CON_PROVIDER);
			elem.setAttribute(CON_PROVIDER_ATTR_NAME, cpmi.getConnectionProviderName());
			elem.setAttribute(CON_PROVIDER_ATTR_CLASS, cpmi.getClassName());
			cpElement.appendChild(elem);
		}
		rootElem.appendChild(cpElement);

		// DBBrowser's main frame's saved dimensions
		if (dbBrowserBounds != null) {
			elem = doc.createElement(DBBROWSER_BOUNDS); 
			elem.setAttribute(BOUNDS_HEIGHT, String.valueOf(dbBrowserBounds.height));
			elem.setAttribute(BOUNDS_LEFT, String.valueOf(dbBrowserBounds.x));
			elem.setAttribute(BOUNDS_TOP, String.valueOf(dbBrowserBounds.y));
			elem.setAttribute(BOUNDS_WIDTH, String.valueOf(dbBrowserBounds.width));
			rootElem.appendChild(elem);
		}

		// DBBrowser's database view dialog's saved dimensions
		if (dbViewBounds != null) {
			elem = doc.createElement(DBVIEW_BOUNDS); 
			elem.setAttribute(BOUNDS_HEIGHT, String.valueOf(dbViewBounds.height));
			elem.setAttribute(BOUNDS_LEFT, String.valueOf(dbViewBounds.x));
			elem.setAttribute(BOUNDS_TOP, String.valueOf(dbViewBounds.y));
			elem.setAttribute(BOUNDS_WIDTH, String.valueOf(dbViewBounds.width));
			rootElem.appendChild(elem);
		}
		
		// Display toolbar labels
		elem = doc.createElement(DISPLAY_TB_LABELS);
		text = doc.createTextNode(String.valueOf(dispToolbarLbl));
		elem.appendChild(text);
		rootElem.appendChild(elem);

		// Ignore selected text
		elem = doc.createElement(IGNORE_SEL_TEXT);
		text = doc.createTextNode(String.valueOf(ignoreSelectedText));
		elem.appendChild(text);
		rootElem.appendChild(elem);

		// Default look and feel
		elem = doc.createElement(LOOK_AND_FEEL);
		text = doc.createTextNode(lookAndFeel);
		elem.appendChild(text);
		rootElem.appendChild(elem);

		// Representation for null
		elem = doc.createElement(REP_FOR_NULL);
		text = doc.createTextNode(repForNull);
		elem.appendChild(text);
		rootElem.appendChild(elem);

		// Results font name
		elem = doc.createElement(RES_FONT_NAME);
		text = doc.createTextNode(resFontName);
		elem.appendChild(text);
		rootElem.appendChild(elem);

		// Results font size
		elem = doc.createElement(RES_FONT_SIZE);
		text = doc.createTextNode(String.valueOf(resFontSize));
		elem.appendChild(text);
		rootElem.appendChild(elem);
				
		// SQL font name
		elem = doc.createElement(SQL_FONT_NAME);
		text = doc.createTextNode(sqlFontName);
		elem.appendChild(text);
		rootElem.appendChild(elem);

		// SQL font size
		elem = doc.createElement(SQL_FONT_SIZE);
		text = doc.createTextNode(String.valueOf(sqlFontSize));
		elem.appendChild(text);
		rootElem.appendChild(elem);

		// SQL tab size
		elem = doc.createElement(SQL_TAB_SIZE);
		text = doc.createTextNode(String.valueOf(sqlTabSize));
		elem.appendChild(text);
		rootElem.appendChild(elem);

		// Store connection secrets
		elem = doc.createElement(STORE_CON_SECRETS);
		text = doc.createTextNode(String.valueOf(storeConSecrets));
		elem.appendChild(text);
		rootElem.appendChild(elem);
		
	    
	    // (Over)write the document to file
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(configFile);
		transformer.transform(source, result);
	}

	/**
	 * Sets the size of cache pages (in rows).  If the value is 0, the entire
	 * resultset is cached from the start.
	 * @parm cachePageSize Number indicating how many rows at a time will be
	 *                     cached in ResultSetTableModel.
	 */
	public static void setCachePageSize(int cachePSize)
	{
		cachePageSize = cachePSize;
	}

	/**
	 * Sets the list of ConnectionProviders to be used by the login dialog.
	 */
	public static void setConnectionProviderModel(ConnectionProviderModel cpm)
	{
		if (cpm != null) {
			connectionProviders = cpm;
		}
	}

	/**
	 * Sets the startup position for the main frame of the dbbrowser app.
	 * Setting the property to Null will remove the configuration option
	 * from the configuration file, resulting in default behavior.
	 */
	public static void setDbBrowserBounds(Rectangle bounds)
	{
		dbBrowserBounds = bounds;
	}
	
	/**
	 * Sets the startup position for the DB view dialog.  Setting the
	 * property to Null will remove the configuration option from the
	 * configuration file, resulting in default behavior.
	 */
	public static void setDbViewBounds(Rectangle bounds)
	{
		dbViewBounds = bounds;
	}
	/**
	 * Sets the name of the prefered look and feel.
	 */
	public static void setLookAndFeelName(String name)
	{
		lookAndFeel = name;
	}

	/**
	 * Sets the string which is to be used to represent null field values in
	 * the database.
	 */
	public static void setRepresentationForNull(String string)
	{
		repForNull = string;
	}

	/**
	 * Sets the font name and size to be used when displaying the results.
	 */
	public static void setResultsFont(Font font)
	{
		setResultsFontName(font.getName());
		setResultsFontSize(font.getSize());
	}

	/**
	 * Sets the name of the font to be used when displaying results.
	 */
	public static void setResultsFontName(String fontName)
	{
		resFontName = fontName;
	}
	
	/**
	 * Sets the point size of the font to be used when displaying results.
	 */
	private static void setResultsFontSize(int fontSize)
	{
		resFontSize = fontSize;
	}

	/**
	 * Sets the font name and size to be used when displaying SQL text from
	 * the given font.
	 */
	public static void setSQLFont(Font font)
	{
		setSQLFontName(font.getName());
		setSQLFontSize(font.getSize());
	}

	/**
	 * Sets the name of the font to be used when displaying SQL text.
	 */
	public static void setSQLFontName(String fontName)
	{
		sqlFontName = fontName;
	}
	
	/**
	 * Sets the point size of the font to be used when displaying SQL text.
	 */
	private static void setSQLFontSize(int fontSize)
	{
		sqlFontSize = fontSize;
	}

	/**
	 * Sets the number of spaces to which tabs are expanded for SQL text.
	 */
	public static void setSQLTabSize(int tabSize)
	{
		sqlTabSize = tabSize;
	}
	
	/**
	 * Indicates whether connections' "secret" fields should be persisted.
	 * @return If true, indicates that "secret" fields (such as passwords)
	 *          should be persisted along with other connection parameters.
	 */
	public static boolean storeConnectionSecrets()
	{
		return storeConSecrets;
	}
	
	/**
	 * Sets whether connections' "secret" fields should be persisted.
	 * @return If true, indicates that "secret" fields (such as passwords)
	 *          should be persisted along with other connection parameters.
	 */
	public static void storeConnectionSecrets(boolean store)
	{
		storeConSecrets = store;
	}
}
