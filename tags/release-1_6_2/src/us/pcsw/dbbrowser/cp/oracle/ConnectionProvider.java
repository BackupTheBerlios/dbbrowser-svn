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
package us.pcsw.dbbrowser.cp.oracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import us.pcsw.dbbrowser.cp.*;

/**
 * us.pcsw.dbbrowser.cp.oracle.ConnectionProvider
 * -
 * Class which provides connectivity into an Oracle RDBMS server using the
 * JDBC thin client.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>10/01/2002 This class was created.</LI>
 * <LI>10/18/2002 Code was added to use the new ConnectionParameter class.</LI>
 * <LI>10/29/2002 Added min/max values to port parameter.</LI>
 * <LI>08/03/2003 Implemented canGetProcedureText(), canGetViewText(),
 *                getProcedureText, and getViewText methods.  PAC </LI>
 * </UL></P>
 * 
 * @author Philip A. Chapman
 */
public final class ConnectionProvider
    extends us.pcsw.dbbrowser.cp.ConnectionProvider
{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Can a procedure's text be retrieved.
	 * @Return true if the text can be retrieved, else false.
	 */
	public boolean canGetProcedureText()
	{
		return true;
	}

	/**
	 * Can a procedure's text be retrieved.
	 * @Return true if the text can be retrieved, else false.
	 */
	public boolean canGetViewText()
	{
		return true;
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
		Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
		// Build the connect string
		StringBuffer connect = new StringBuffer("jdbc:oracle:thin:@//");
		connect.append(getConnectionParameter("Server").getValue());
		connect.append(':');
		connect.append(getConnectionParameter("Port").getValue()); // 1521
		connect.append('/');
		connect.append(getConnectionParameter("Database").getValue());
		// Connect
		String login = getConnectionParameter("Login").getValue().toString();
		String pass = getConnectionParameter("Password").getValue().toString();
		return DriverManager.getConnection(connect.toString(), login, pass);
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
		                                    new Integer(1521), 0,
		                                    Integer.MAX_VALUE);
		cpAry.add(4, cp);
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
		StringBuffer spText = new StringBuffer();
		PreparedStatement stmt = con.prepareStatement("select Text " +
		                                              "from dba_source " +
			                                          "where Owner = ? and " +
			                                                "Name = ? " +
			                                          "order by Line");
		stmt.setString(1, schema.toUpperCase());
		stmt.setString(2, pName.toUpperCase());
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			spText.append(rs.getString(1));
		}
		
		return spText.toString();
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
	 * Takes a name of a view and returns the text description.
     * @param con The connection to the database server.
     * @param catalog The catalog of the view.  This value may be null.
     * @param schema The schema of the view.  This value may be null.
     * @param vName The name of the view.  This value not be null.
	 * @return The text of the view.
	 * @throws SQLException indicates a problem retrieving the view's text.
	 */
	public String getViewText
		(Connection con, String catalog, String schema, String vName)
		throws SQLException
	{
		StringBuffer vwText = new StringBuffer();
		PreparedStatement stmt = con.prepareStatement("select TEXT " +
													  "from SYS.ALL_VIEWS " +
													  "where OWNER = ? and " +
															"VIEW_NAME = ?");
		stmt.setString(1, schema.toUpperCase());
		stmt.setString(2, vName.toUpperCase());
		ResultSet rs = stmt.executeQuery();
		vwText.append("CREATE OR REPLACE VIEW\n\t");
		vwText.append(vName);
		vwText.append("\nAS\n");
		while (rs.next()) {
			vwText.append(rs.getString(1));
		}
		
		return vwText.toString();
	}
}
