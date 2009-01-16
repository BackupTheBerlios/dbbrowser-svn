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
package us.pcsw.dbbrowser.cp.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import us.pcsw.dbbrowser.cp.*;

/**
 * us.pcsw.dbbrowser.cp.mysql.ConnectionProvider
 * -
 * Class which provides connectivity into a PostgreSQL server.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>07/23/2001 This class was created.</LI>
 * <LI>08/13/2002 Documentation was modified.</LI>
 * <LI>10/18/2002 Code was added to use the new ConnectionParameter class.<LI>
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
//		Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		// Build the connect string
		StringBuffer connect = new StringBuffer("jdbc:mysql://");
		connect.append(getConnectionParameter("Server").getValue());
		connect.append(':');
		connect.append(getConnectionParameter("Port").getValue().toString());
		connect.append('/');
		connect.append(getConnectionParameter("Database").getValue());
		Object o = getConnectionParameter("Parameters").getValue();
		if (o != null && o.toString().length() > 0) {
			connect.append('?');
			connect.append(o);
		}
		// Connect
		String login = getConnectionParameter("Login").getValue().toString();
		String pass = getConnectionParameter("Password").getValue().toString();
		return DriverManager.getConnection(connect.toString(), login, pass);
    }

	/**
	 * @see us.pcsw.dbbrowser.cp.ConnectionProvider#getDataTypes()
	 */
	public DataType[] getDataTypes()
		throws ClassNotFoundException, IllegalAccessException,
			InstantiationException, SQLException
	{
		DataType[] types = super.getDataTypes();
		
		// MySQL's driver lists all the numeric types as serial though
		// can be used to store an auto-increment value, they are not
		// necessarily used so.
		// Affected types:
		//		TINYINT, BIGINT, NUMERIC, DECIMAL, INTEGER, INT, MEDIUMINT
		//		SMALLINT, DOUBLE, FLOAT, DOUBLE, DOUBLE PRECISION, REAL
		//
		for (int i = 0; i < types.length; i++) {
			if (types[i].isAutoIncrement()) {
				types[i].setAutoIncrement(false);
			}
		}
		
		return types;
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
											new Integer(3306), 0,
											Integer.MAX_VALUE);
		cpAry.add(4, cp);
		cp = new StringConnectionParameter("Parameters", false, null);
		cpAry.add(5, cp);
		return cpAry;
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
	public String getProcedureText
		(Connection con, String catalog, String schema, String pName)
		throws SQLException
	{
		throw new SQLException("The getProcedureText functionallity is not " +
								"available through the MySQL driver."); 
		/*
		StringBuffer spText = new StringBuffer();
		PreparedStatement stmt = con.prepareCall("sp_helptext");
		
		stmt.setString(1, pName);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			spText.append(rs.getString(1));
		}
		
		return spText.toString();
		*/ 
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
	 * Takes a name of a view and returns the text.
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
		throw new SQLException("The getViewText functionallity is not " +
								"available through the MySQL driver."); 
	}
}
