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
package us.pcsw.dbbrowser.cp.mssql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import us.pcsw.dbbrowser.cp.*;

/**
 * us.pcsw.dbbrowser.cp.mssql.ConnectionProvider
 * -
 * Class which provides connectivity into a Microsoft SQL server.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>08/13/2002 This class was created.</LI>
 * <LI>10/18/2002 Code was added to use the new ConnectionParameter class.</LI>
 * <LI>10/29/2002 Added min/max values to port parameter.</LI>
 * <LI>07/15/2003 Put this provider implementation into its own package.
 *                PAC</LI>
 * <LI>07/25/2003 Implemented the getProcedureText abstract method.  PAC </LI>
 * <LI>10/14/2003 Added the option to use the jtds driver instead of
 *                Microsoft's.  Not all the MetaData methods appear to work
 *                the same with the two though, so I may have to break the
 *                jtds driver into a seperate ConnectionProvider
 *                implementation soon.  The advantage of the jtds driver
 *                is the ability to connect to MSSQL 7 and earlier, though
 *                this has not yet been tested with this app.  PAC </LI>
 * <LI>01/02/2004 Temporarily commented out the use of the jts driver for
 *                the next release since it has not yet been thoroughly
 *                tested.  PAC </LI>
 * <LI>04/27/2005 Renabled the use of the jts driver, and set it as the
 *                default driver.  PAC </LI>
 * </UL><P>
 *
 * @author Philip A. Chapman
 */
public final class ConnectionProvider
	extends us.pcsw.dbbrowser.cp.ConnectionProvider
{
	private static final long serialVersionUID = 1L;
	
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
    	StringBuffer connect = new StringBuffer();
    	
    	if (((Boolean)getConnectionParameter("Use Microsoft Driver").getValue()).booleanValue()) {
			// Attempt to load the Microsoft driver
			Class.forName("com.microsoft.jdbc.sqlserver.SQLServerDriver").newInstance();
			connect.append("jdbc:microsoft:sqlserver://");
    	} else {
			// Attempt to load the jtds driver.
			// jdbc:jtds:<server_type>://<server>[:<port>][/<database>][;<property>=<value>[;...]]
			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
			connect.append("jdbc:jtds:sqlserver://");
    	}
		// Build the connect string
		connect.append(getConnectionParameter("Server").getValue());
		connect.append(':');
		connect.append(getConnectionParameter("Port").getValue());
		connect.append(";DatabaseName=");
		connect.append(getConnectionParameter("Database").getValue());
		// Connect
		String login = getConnectionParameter("Login").getValue().toString();
		String pass = getConnectionParameter("Password").getValue().toString();
		return DriverManager.getConnection(connect.toString(), login, pass);
    }
    
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
	 * Builds and returns the vector of needed connection parameters.
	 */
	protected Vector getNewConnectionParameters()
	{
		ConnectionParameter cp = null;
		Vector cpAry = new Vector(6,1);
		//Vector cpAry = new Vector(5,1);
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
		                                    new Integer(1433), 0,
		                                    Integer.MAX_VALUE);
		cpAry.add(4, cp);
		cp = new BooleanConnectionParameter("Use Microsoft Driver", true,
		                                    Boolean.FALSE);
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
		return getText(con, catalog, schema, pName); 
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
	private String getText
		(Connection con, String catalog, String schema, String oName)
		throws SQLException
	{
		StringBuffer spText = new StringBuffer();

		con.setCatalog(catalog);
		PreparedStatement stmt = con.prepareStatement("exec sp_helptext ?");
		
		stmt.setString(1, oName);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			spText.append(rs.getString(1));
		}
		
		return spText.toString(); 
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
		return getText(con, catalog, schema, vName); 
	}	
}
