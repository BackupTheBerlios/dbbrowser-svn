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

import java.sql.Connection;
import java.sql.SQLException;

/**
 * us.pcsw.dbbrowser.ProcedureTreeNode
 * -
 * Represents a procedure in a tree view.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 11, 2002	This class was created by Philip A. Chapman.</LI>
 * </UL></P>
 */
public class ProcedureTreeNode extends DBObjectsTreeNode
{
	protected String cat = null;
	protected String catSep = null;
	protected Connection con = null;
	protected String name = null;
	protected String schem = null;
	
	/**
	 * Constructor for ProcedureTreeNode.
     * @param parent The parent object in the tree.
     * @param con The connection to the database server.
     * @param catalog The catalog of the procedure.  This value may be null.
     * @param schema The schema of the procedure.  This value may be null.
     * @param procName The name of the procedure.  This value not be null.
     * @exception SQLException Indicates an error loading column names from
     *                         the database connection.
	 */
	public ProcedureTreeNode
		(DBObjectsTreeNode parent, Connection con, String catalog,
		 String schema, String procName)
		throws SQLException
	{
		super(parent);
		if (con == null) {
		    throw new IllegalArgumentException("Invalid Connection.");
		} else if (procName == null) {
			throw new IllegalArgumentException("Invalid Procedure Name.");
		} else {
			this.con = con;
		}
		this.cat = catalog;
		this.cat = catalog;
		this.catSep = con.getMetaData().getCatalogSeparator();
		this.schem = schema;
		this.name = procName;
	}

	/**
	 * Returns the catalog of the Procedure.
	 * @return The name of the catalog.  May be null.
	 */
	public String getCatalog()
	{
		return cat;
	}
	
	/**
	 * Returns the fully qualified name of the procedure.
	 */
	public String getFullyQualifiedName()
	{
    	// For now, I am going to assume that catalog + sep + schema + procname
    	// is valid for all DBs.  This will probably not hold true and will
    	// require a rewrite.
    	String cs = getSeperator();
		String s = null;
		StringBuffer sb = new StringBuffer();
		s = getCatalog();
		if (s != null && s.length() > 0) {
			sb.append(s);
		}
		s = getSchema();
		if (s != null && s.length() > 0) {
			if (sb.length() > 0) {
				sb.append(cs);
			}
			sb.append(s);
		}
		s = getName();
		if (sb.length() > 0) {
			sb.append(cs);
		}
		sb.append(s);
		return sb.toString();
	}

	/**
	 * Returns the name of the Procedure that this object represents
	 */
	public String getName()
	{
		return name;	
	}

	/**
	 * The string which should be used to seperate database, schema, and table
	 * name.
	 * @return The seperator string.
	 */
	private String getSeperator()
	{
		return catSep == null || catSep.length() == 0 ? "." : catSep;
	}

	/**
	 * Returns the schema of the procedure.
	 * @return The schema of the procedure.  May be null.
	 */
	public String getSchema()
	{
		return schem;
	}
	 
	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#isLeaf()
	 */
	boolean isLeaf() {
		return false;
	}

	/**
	 * (Re)loads a list of columns in the procedure from the database.
     * @exception SQLException Indicates an error loading table names from
     *                         the database connection.
	 */
	public void refresh()
		throws SQLException
	{
		// Clear children
		if (children == null || children.size() == 0) {
			insertChildNode(new ColumnsTreeNode(this, con));
		} else {
			for (int i = 0; i < children.size(); i++) {
				try {
					((DBObjectsTreeNode)children.elementAt(i)).refresh();
				} catch (ClassNotFoundException cnfe) {
				} catch (InstantiationException ie) {
				} catch (IllegalAccessException iae) {}
			}
		}
	}

	/**
	 * Returns a string representation of this object.
	 */
    public String toString()
    {
    	String cs = getSeperator();
		String s = null;
		StringBuffer sb = new StringBuffer();
		s = getSchema();
		if (s != null && s.length() > 0) {
			sb.append(s);
		}
		s = getName();
		if (sb.length() > 0) {
			sb.append(cs);
		}
		sb.append(s);
		return sb.toString();
    }
}
