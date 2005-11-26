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
package us.pcsw.dbbrowser.cp.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import us.pcsw.dbbrowser.cp.*;

/**
 * us.pcsw.dbbrowser.cp.postgresql.ConnectionProvider
 * -
 * Class which provides connectivity into a PostgreSQL server.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>07/15/2001 This class was created.</LI>
 * <LI>08/13/2002 Documentation was modified.</LI>
 * <LI>10/18/2002 Code was added to use the new ConnectionParameter class.<LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public final class ConnectionProvider
	extends us.pcsw.dbbrowser.cp.ConnectionProvider
{	
	private static final String[] SSL_OPTIONS = {
			"Do Not Use SSL",
			"Use SSL And Verify Certificate",
			"Use SSL And Do Not Verify Certificate"
	};

	/**
	 * Can a procedure's text be retrieved.
	 * @Return true if the text can be retrieved, else false.
	 */
	public boolean canGetProcedureText()
	{
		return false;
	}

	/**
	 * Can a procedure's text be retrieved.
	 * @Return true if the text can be retrieved, else false.
	 */
	public boolean canGetViewText()
	{
		return false;
	}

    /**
     * Gets a connection to the database based on the provided parameters.
     * @exception ClassNotFoundException indicates that the JDBC driver classes
     *                                   could not be found.
     * @exception IllegalAccessException indicates that the JDBC driver classes
     *                                   could not be loaded.
     * @exception InstantiationException indicates that the JDBC driver classes
     *                                   could not be loaded.
     * @exception SQLException indicates there was an error connecting to the
     *                         database.
     */
    public Connection getConnection()
		throws ClassNotFoundException, IllegalAccessException,
		       InstantiationException, SQLException
    {
		// Attempt to load the driver
		Class.forName("org.postgresql.Driver").newInstance();
		// Build the connect string
		StringBuffer connect = new StringBuffer("jdbc:postgresql://");
		connect.append(getConnectionParameter("Server").getValue());
		connect.append(':');
		connect.append(getConnectionParameter("Port").getValue());
		connect.append('/');
		connect.append(getConnectionParameter("Database").getValue());
		Properties props = new Properties();
		props.setProperty("password", getConnectionParameter("Password").getValue().toString());
		props.setProperty("user", getConnectionParameter("Login").getValue().toString());
		if (isSSLUseSelected()) {
			props.setProperty("ssl","true");
			if (! isVerifySSLCertificateSelected()) {
				props.setProperty("sslfactory","org.postgresql.ssl.NonValidatingFactory");
			}
		}
		return DriverManager.getConnection(connect.toString(), props);
    }

	/**
	 * Builds and returns the vector of needed connection parameters.
	 */
	protected Vector getNewConnectionParameters()
	{
		ConnectionParameter cp = null;
		Vector cpAry = new Vector(5,1);
		cp = new StringConnectionParameter("Server", true, null);
		cpAry.add(0, cp);
		cp = new StringConnectionParameter("Database", true, null);
		cpAry.add(1, cp);
		cp = new StringConnectionParameter("Login", true, null);
		cpAry.add(2, cp);
		cp = new StringConnectionParameter("Password", true, null, 0, 0,
		                                   true);
		cpAry.add(3, cp);
		cp = new IntegerConnectionParameter("Port", true,
		                                    new Integer(5432), 0,
		                                    Integer.MAX_VALUE);
		cpAry.add(4, cp);
		cp = new PicklistConnectionParameter(
				"SSL Options", true,
				SSL_OPTIONS, false,
				new String[] {SSL_OPTIONS[0]}
			);
		cpAry.add(5, cp);
		return cpAry;
	}

	/**
	 * Takes a name of a procedure and returns the text of the stored
	 * procedure.
     * @param con The connection to the database server.
     * @param catalog The catalog of the procedure.  This value may be null.
     * @param schema The schema of the procedure.  This value may be null.
     * @param pname The name of the procedure.  This value not be null.
	 * @return The text of the stored procedure.
	 * @throws SQLException indicates a problem retrieving the sp's text.
	 */
	public String getProcedureText
		(Connection con, String catalog, String schema, String pName)
		throws SQLException
	{
		throw new SQLException("The getProcedureText functionallity is not " +
								"available through the Postgresql driver."); 
	}

	/**
	 * Gets a string representation of the server to which a connection
	 * is or will be made.
	 */
	public String getServerName()
	{
		return getConnectionParameter("Server").getValue().toString();
	}

	/**
	 * Takes a name of an object and returns the text description.
     * @param con The connection to the database server.
     * @param catalog The catalog of the object.  This value may be null.
     * @param schema The schema of the object.  This value may be null.
     * @param oName The name of the object.  This value not be null.
	 * @return The text of the object.
	 * @throws SQLException indicates a problem retrieving the object's text.
	 */
	public String getViewText
		(Connection con, String catalog, String schema, String oName)
		throws SQLException
	{
		throw new SQLException("The getViewText functionallity is not " +
								"available through the Postgresql driver."); 
	}
	
	private boolean isSSLUseSelected()
	{
		PicklistConnectionParameter parm =
			(PicklistConnectionParameter)getConnectionParameter("SSL Options");
		return ! parm.isItemSelected(0);
	}
	
	private boolean isVerifySSLCertificateSelected()
	{
		PicklistConnectionParameter parm =
			(PicklistConnectionParameter)getConnectionParameter("SSL Options");
		return parm.isItemSelected(1);
	}
}
