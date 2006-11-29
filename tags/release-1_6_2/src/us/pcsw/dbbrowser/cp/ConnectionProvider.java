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
package us.pcsw.dbbrowser.cp;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Enumeration;
import java.util.Vector;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import us.pcsw.dbbrowser.Preferences;
import us.pcsw.util.Debug;

/**
 * us.pcsw.dbbrowser.ConnectionProvider
 * -
 * This class is the abstract definition for classes to use to provide
 * dbbrowser with a JDBC database connection.  Even though the connect method
 * for java.sql.DriverManager.getConnection() is well-documented, various JDBC
 * implementations may use the getConnection() parameters differently.
 * Therefore, for dbbrowser to be able to use connections to various RDBMS's,
 * An appropriate subclass of ConnectionProvider must be written to pass the
 * correct parameters to getConnection() and return a java.sql.Connection.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>07/15/2001 This class was created.</LI>
 * <LI>10/18/2002 Code was added to use the new ConnectionParameter class.</LI>
 * <LI>03/12/2003 Depreciated the returnsResultset method.</LI>
 * <LI>03/24/2003 Finished code to save and load parameter info.  PAC </LI>
 * <LI>07/25/2003 Added the getProcedureText abstract method.  This is the
 *                first of several new methods to be added that will provide
 *                more database info.  PAC </LI>
 * <LI>05/05/2005 Fixed a bug in the load(File) method where an error was
 *                thrown for parameters with no value. PAC </LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public abstract class ConnectionProvider
	implements java.lang.Cloneable, java.io.Serializable
{
	private static String
		ELEM_ROOT = "DBBrowserConnection",
		ELEM_CONNECTION = "Connection",
		ELEM_ITEM = "item",
		ELEM_ITEMS = "items",
		ATTR_PROVIDER = "provider",
		ELEM_PARAM_LIST = "ParameterList",
		ELEM_PARAM = "Parameter",
		ATTR_NAME = "name",
		ATTR_VALUE = "value";

	/**
	 * Vector of parameters.  Vector is used to provide the enumerator.
	 */
	protected Vector parms = null;

    /**
     * Initializes the class.
     */
    protected ConnectionProvider()
    {
    	parms = getNewConnectionParameters();
    }

	/**
	 * Can a procedure's text be retrieved.
	 * @Return true if the text can be retrieved, else false.
	 */
	public abstract boolean canGetProcedureText();

	/**
	 * Can a procedure's text be retrieved.
	 * @Return true if the text can be retrieved, else false.
	 */
	public abstract boolean canGetViewText();

	/**
	 * Allows for the creation of a duplicate of this connection.  Connection
	 * parameters are copied to the clone.
	 */
	public Object clone()
	{
		// Create a new instance of the Connection Provider
		ConnectionProvider newCP = null;
		try {
			newCP = (ConnectionProvider)this.getClass().newInstance();
		} catch (Throwable t) {
			Debug.log(t, 10);
			return null;
		}
		// Copy the parameters for the connection.
		ConnectionParameter cParm = null;
		Enumeration e = getConnectionParameters();
		while (e.hasMoreElements()) {
			cParm = (ConnectionParameter)e.nextElement();
			newCP.getConnectionParameter(cParm.getName()).setValue(cParm.getValue());
		}
		// Return the result
		return newCP;
	}

    /**
     * Gets a connection to the database based on the provided parameters.
     * @exception ClassNotFoundException indicates that the JDBC driver classes
     *                                   could not be found/loaded.
     * @exception IllegalAccessException indicates that the JDBC driver classes
     *                                   could not be loaded.
     * @exception InstantiationException indicates that the JDBC driver classes
     *                                   could not be loaded.
     * @exception SQLException indicates there was an error connecting to the
     *                         database.
     */
    public abstract Connection getConnection()
		throws ClassNotFoundException, IllegalAccessException,
	           InstantiationException, SQLException;

	public ConnectionParameter getConnectionParameter(String name)
	{
		// I don't expect the number of parameters to be very large, so I
		// believe a serial search will be fine.
		ConnectionParameter cp = null;
		for (int i = 0; i < parms.size(); i++) {
			cp = ((ConnectionParameter)parms.elementAt(i));
			if (cp.getName().equals(name)) {
				return cp;
			}
		}
		return null;
	}

	/**
	 * Returns a ConnectionParameters object which contains the parameters
	 * to be set for a connection to a database.
	 */
	public Enumeration getConnectionParameters()
	{
		return parms.elements();
	}
	
	public DataType[] getDataTypes()
		throws
			ClassNotFoundException, IllegalAccessException,
			InstantiationException, SQLException
	{
		Connection con = getConnection();
		DatabaseMetaData dmd = con.getMetaData();
		ResultSet rst = dmd.getTypeInfo();
		DataType dt = null;
		Vector v = new Vector();
		while (rst.next()) {
			dt = new DataType(rst);
			dt.setPrecisionRequired(getPrecisionRequired(dt));
			dt.setScaleRequired(getScaleRequired(dt));
			v.add(dt);
		}
		DataType[] dataTypes = new DataType[v.size()];
		v.copyInto(dataTypes);
		return dataTypes;
	}

	/**
	 * Builds and returns the vector of needed connection parameters.
	 */
	protected abstract Vector getNewConnectionParameters();
	
	/**
	 * Indicates whether the precision parameter is required in a column
	 * creating statement, such as "create table".  Only the more common
	 * JDBC data types are defined here.  Subclasses of this class should
	 * override this method in order to handle other types. 
	 */
	public Boolean getPrecisionRequired(DataType type)
	{
		Boolean returnValue = null;
		
		switch (type.getJDBCType()) {
			case Types.BIGINT:
			case Types.BOOLEAN:
			case Types.DATE:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.INTEGER:
			case Types.SMALLINT:
			case Types.TIME:
			case Types.TIMESTAMP:
			case Types.TINYINT:
				returnValue = Boolean.FALSE;
				break;
			case Types.BIT:
			case Types.CHAR:
			case Types.DECIMAL:
			case Types.LONGVARBINARY:
			case Types.LONGVARCHAR:
			case Types.NUMERIC:
			case Types.REAL:
			case Types.VARBINARY:
			case Types.VARCHAR:
				returnValue = Boolean.TRUE;
				break;
			case Types.ARRAY:
			case Types.BINARY:
			case Types.BLOB:
			case Types.CLOB:
			case Types.DATALINK:
			case Types.JAVA_OBJECT:
			case Types.OTHER:
			case Types.REF:
			case Types.STRUCT:
				returnValue = null;
				break;
		}
		
		return returnValue;
	}
	
	/**
	 * Indicates whether the scale parameter is required in a column
	 * creating statement, such as "create table".  Only the more common
	 * JDBC data types are defined here.  Subclasses of this class should
	 * override this method in order to handle other types. 
	 */
	public Boolean getScaleRequired(DataType type)
	{
		Boolean returnValue = null;
		
		switch (type.getJDBCType()) {
			case Types.BIGINT:
			case Types.BIT:
			case Types.BOOLEAN:
			case Types.CHAR:
			case Types.DATE:
			case Types.DOUBLE:
			case Types.FLOAT:
			case Types.INTEGER:
			case Types.LONGVARBINARY:
			case Types.LONGVARCHAR:
			case Types.REAL:
			case Types.SMALLINT:
			case Types.TIME:
			case Types.TIMESTAMP:
			case Types.TINYINT:
			case Types.VARBINARY:
			case Types.VARCHAR:
				returnValue = Boolean.FALSE;
				break;
			case Types.DECIMAL:
			case Types.NUMERIC:
				returnValue = Boolean.TRUE;
				break;
			case Types.ARRAY:
			case Types.BINARY:
			case Types.BLOB:
			case Types.CLOB:
			case Types.DATALINK:
			case Types.JAVA_OBJECT:
			case Types.OTHER:
			case Types.REF:
			case Types.STRUCT:
				returnValue = null;
				break;
		}
		
		return returnValue;
	}

	/**
	 * Takes a name of a procedure and returns the text of the stored
	 * procedure.
     * @param con The connection to the database server.
     * @param catalog The catalog of the procedure.  This value may be null.
     * @param schema The schema of the procedure.  This value may be null.
     * @param pName The name of the procedure.  This value not be null.
	 * @return The text of the stored procedure.
	 * @throws SQLException indicates a problem retrieving the sp's text.
	 */
	public abstract String getProcedureText
		(Connection con, String catalog, String schema, String pName)
		throws SQLException;

	/**
	 * Gets a string representation of the server to which a connection
	 * is or will be made.
	 */
	public abstract String getServerName();

	/**
	 * Takes a name of a view and returns the text of the view.
     * @param con The connection to the database server.
     * @param catalog The catalog of the procedure.  This value may be null.
     * @param schema The schema of the procedure.  This value may be null.
     * @param vName The name of the procedure.  This value not be null.
	 * @return The text of the stored procedure.
	 * @throws SQLException indicates a problem retrieving the view's text.
	 */
	public abstract String getViewText
		(Connection con, String catalog, String schema, String vName)
		throws SQLException;

	/**
	 * Checks to determine that the parameter data is valid.  This only
	 * ensures that valid data is available for a connection attempt.  It
	 * does not ensure that a connection can be made using the parameters.
	 * @return True if the connection paramater data is valid, false otherwise.
	 */
	public boolean isValid()
	{
		ConnectionParameter cp = null;
		for (Enumeration e = getConnectionParameters(); e.hasMoreElements();) {
			cp = (ConnectionParameter)e.nextElement();
			if (! cp.isValid()) {
				return false;
			}
		}
		return true;
	}
	
	public static final ConnectionProvider load(File file)
		throws ClassNotFoundException, IllegalAccessException,
		        InstantiationException, IOException,
		        ParserConfigurationException, SAXException
	{
		if (file.exists()) {
			ConnectionProvider cp = null;
			
			// Attempt to load preferences
			DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(file); 

			// Instatiate the connection provider class
			Element conElem = (Element)doc.getElementsByTagName(ELEM_CONNECTION).item(0);
			String className = ((Attr)conElem.getAttributes().getNamedItem(ATTR_PROVIDER)).getValue();
			cp = (ConnectionProvider)Class.forName(className).newInstance();

			// Get parameter values
			Attr attr = null;
			Element elem = null;
			NamedNodeMap map = null;
			NodeList nl = doc.getElementsByTagName(ELEM_PARAM);
			int i = 0;
			int itmCnt = nl.getLength();

			Enumeration e = cp.getConnectionParameters();
			while (e.hasMoreElements()) {
				ConnectionParameter param = (ConnectionParameter)e.nextElement();
				for (i = 0; i < itmCnt; i++) {
					elem = (Element)nl.item(i);
					map = elem.getAttributes();
					attr = (Attr)map.getNamedItem(ATTR_NAME);
					if (attr.getValue().equals(param.getName())) {
						attr = (Attr)map.getNamedItem(ATTR_VALUE);
						if (param instanceof PicklistConnectionParameter) {
							Vector values = new Vector(1, 1);
							Node items = elem.getChildNodes().item(0);
							if (items.getNodeName().equals(ELEM_ITEMS)) {
								Node item;
								Text text;
								for (int j = 0; j < items.getChildNodes().getLength(); j++) {
									item = items.getChildNodes().item(j);
									if (item.getNodeName().equals(ELEM_ITEM)) {
										text = (Text)item.getChildNodes().item(0);
										values.add(text.getData());
									}
								}
							}
							String[] strings = new String[values.size()];
							for (int j = 0; j < strings.length; j++) {
								strings[j] = values.elementAt(j).toString();
							}
							param.setValue(strings);
						} else {
							if (attr != null) {
								// depreciated method of saving connection data
								param.setValue(attr.getValue());
							} else {
								Text text = (Text)elem.getChildNodes().item(0);
								param.setValue(text == null ? "" : text.getData());
							}
						}
						break;
					}
				}
			}

			return cp;
		} else {
			return null;
		}
	}

	/**
	 * Saves the preferences to the file .dbbrowser_rc in the user's home
	 * directory.
	 */
	public final void save(File file)
		throws IOException, ParserConfigurationException,
				TransformerConfigurationException, TransformerException
	{
		// First, build the document.
		Element elem = null;
		Element parentElem = null;
	    
		DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		parentElem = doc.createElement(ELEM_ROOT);
		doc.appendChild(parentElem);

		// Connection
		elem = doc.createElement(ELEM_CONNECTION);
		elem.setAttribute(ATTR_PROVIDER, getClass().getName());
		parentElem.appendChild(elem);
		parentElem = elem;
		
		// Parameter List
		elem = doc.createElement(ELEM_PARAM_LIST);
		parentElem.appendChild(elem);
		parentElem = elem;
		
		// Parameters
		ConnectionParameter cp = null;
		Enumeration e = getConnectionParameters();
		while (e.hasMoreElements()) {
			cp = (ConnectionParameter)e.nextElement();
			elem = doc.createElement(ELEM_PARAM);
			elem.setAttribute(ATTR_NAME, cp.getName());
			if (cp instanceof PicklistConnectionParameter) {
				Element item;
				String[] v = (String[])cp.getValue();
				Element items = doc.createElement(ELEM_ITEMS);
				for (int i = 0; v != null && i < v.length; i++) {
					item = doc.createElement(ELEM_ITEM);
					item.appendChild(doc.createTextNode(v[i]));
					items.appendChild(item);
				}
				elem.appendChild(items);
			} else if (cp instanceof StringConnectionParameter &&
			    ((StringConnectionParameter)cp).isSecret() &&
			    ! Preferences.storeConnectionSecrets())
			{
				// Do not store the secret.
			} else {
				elem.appendChild(doc.createTextNode(cp.getValue().toString()));
			}
			parentElem.appendChild(elem);
		}

	    
		// (Over)write the document to file
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(file);
		transformer.transform(source, result);
	}
}
