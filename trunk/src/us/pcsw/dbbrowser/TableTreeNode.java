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
import java.util.Stack;
import java.util.Vector;

/**
 * us.pcsw.dbbrowser.TableNode
 * -
 * Represents a table.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 8, 2002	This class was created by Philip A. Chapman.</LI>
 * </UL></P>
 */
public class TableTreeNode
	extends DBObjectsTreeNode
{
	protected String cat = null;
	protected String catSep = null;
	protected Connection con = null;
	protected String name = null;
	protected String schem = null;
	protected TablesTreeNodeType type = null;
	
	/**
	 * Constructor for TablesTreeNode.
     * @param parent The parent object in the tree.
     * @param con The connection to the database server.
     * @param catalog The catalog of the table.  This value may be null.
     * @param schema The schema of the table.  This value may be null.
     * @param tableName The name of the table.  This value may not be null.
     * @exception SQLException Indicates an error loading column names from
     *                         the database connection.
	 */
	public TableTreeNode
		(DBObjectsTreeNode parent, Connection con, String catalog,
		 String schema, String tableName)
		throws SQLException
	{
		super(parent);
		if (con == null) {
		    throw new IllegalArgumentException("Invalid Connection.");
		} else {
			this.cat = catalog;
			this.catSep = con.getMetaData().getCatalogSeparator();
			this.con = con;
			this.name = tableName;
			this.schem = schema;
			this.type = ((TablesTreeNode)parent).getType();
		}
	}
	public TableTreeNode
		(Connection con, String catalog, String schema, String tableName)
		throws SQLException
	{
		super(null);
		if (con == null) {
		    throw new IllegalArgumentException("Invalid Connection.");
		} else {
			this.cat = catalog;
			this.catSep = con.getMetaData().getCatalogSeparator();
			this.con = con;
			this.name = tableName;
			this.schem = schema;
			this.type = null;
		}
	}

	/**
	 * Returns the catalog of the table.
	 * @return The name of the catalog.  May be null.
	 */
	public String getCatalog()
	{
		return cat;
	}
	
	/**
	 * Returns the fully qualified name of the table.
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
	 * The string which should be used to seperate database, schema, and table
	 * name.
	 * @return The seperator string.
	 */
	private String getSeperator()
	{
		return catSep == null || catSep.length() == 0 ? "." : catSep;
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Returns the schema of the table.
	 * @return The schema of the table.  May be null.
	 */
	public String getSchema()
	{
		return schem;
	}

	/**
	 * Returns the type of tables which are displayed below this node.
	 */
	public TablesTreeNodeType getType()
	{
		return type;
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#isLeaf()
	 */
	boolean isLeaf() {
		return false;
	}

	/**
	 * (Re)loads a list of columns and indices for the table from the database.
     * @exception SQLException Indicates an error loading table names from
     *                         the database connection.
	 */
	public void refresh()
		throws SQLException
	{
		// Clear children
		if (children != null && children.size() > 0) {
			int indices[] = new int[children.size()];
			Object nodes[] = new Object[children.size()];
			for (int i = 0; i < indices.length; i++) {
				indices[i] = i;
				nodes[i] = children.elementAt(i);
			}
			notifyParent(NODES_REMOVED, new Stack(), indices, nodes);
			children = new Vector();
		}
		
		TablesTreeNode ttn = (TablesTreeNode)getParent();
		insertChildNode(new ColumnsTreeNode(this, con));
		if (ttn.getType() != TablesTreeNodeType.VIEWS) {
			insertChildNode(new IndicesTreeNode(this, con));
		}
	}

	/**
	 * Returns a string representation of this object.  Schema + seperator +
	 * name.
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