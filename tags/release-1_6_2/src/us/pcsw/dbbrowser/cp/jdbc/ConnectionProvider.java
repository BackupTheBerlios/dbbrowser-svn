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
package us.pcsw.dbbrowser.cp.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;
import us.pcsw.dbbrowser.cp.*;

/**
 * us.pcsw.dbbrowser.cp.jdbc.ConnectionProvider
 * -
 * Generic JDBC Connection provider which allows the user to connect using any
 * JDBC driver.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Jun 8, 2003 Class Created By Philip A. Chapman.</LI>
 * <LI>Jul 15,2003 Put this provider implementation into its own package.
 *                 PAC</LI>
 * </UL></P>
 */
public class ConnectionProvider
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
     * @see us.pcsw.dbbrowser.ConnectionProvider#getConnection()
     */
    public Connection getConnection()
        throws
            ClassNotFoundException,
            IllegalAccessException,
            InstantiationException,
            SQLException
    {
		// Attempt to load the driver
		Class driver = Class.forName
			(getConnectionParameter("Driver Class").getValue().toString());
		driver.newInstance();
		// Connect
		String connect = getConnectionParameter("Url").getValue().toString();
		String login = getConnectionParameter("Login").getValue().toString();
		String pass = getConnectionParameter("Password").getValue().toString();
		if (login.length() > 0 || pass.length() > 0) {
			return DriverManager.getConnection(connect, login, pass);
		} else {
			return DriverManager.getConnection(connect);
		}
    }

    /**
     * @see us.pcsw.dbbrowser.ConnectionProvider#getNewConnectionParameters()
     */
    protected Vector getNewConnectionParameters()
    {
		ConnectionParameter cp = null;
		Vector cpAry = new Vector(4,1);
		cp = new StringConnectionParameter("Driver Class", true, null);
		cpAry.add(0, cp);
		cp = new StringConnectionParameter("Url", true, null);
		cpAry.add(1, cp);
		cp = new StringConnectionParameter("Login", false, null);
		cpAry.add(2, cp);
		cp = new StringConnectionParameter("Password", false, null, 0, 0,
										   true);
		cpAry.add(3, cp);
		return cpAry;
    }
    
	/**
	 * Not implemented.
	 */
	public String getProcedureText
		(Connection con, String catalog, String schema, String pName)
		throws SQLException
	{
		throw new SQLException("The getProcedureText functionallity is not " +
		                        "available through the generic JDBC driver."); 
	}
	
	/**
	 * Not implemented.
	 */
	public String getViewText
		(Connection con, String catalog, String schema, String pName)
		throws SQLException
	{
		throw new SQLException("The getViewText functionallity is not " +
		                        "available through the generic JDBC driver."); 
	}

    /**
     * @see us.pcsw.dbbrowser.ConnectionProvider#getServerName()
     */
    public String getServerName() {
        return getConnectionParameter("Url").getValue().toString();
    }

	/**
	 * Takes a name of a view and returns the text of the view.
	 * @return The text of the stored procedure.
	 * @throws SQLException indicates a problem retrieving the view's text.
	 */
	public String getViewText(Connection con, String vName)
		throws SQLException
	{
		throw new SQLException("The getViewText functionallity is not " +
								"available through the generic JDBC driver."); 
	}
}
