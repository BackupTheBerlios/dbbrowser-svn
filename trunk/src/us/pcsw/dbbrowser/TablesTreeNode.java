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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;
import java.util.Vector;
import us.pcsw.dbbrowser.event.*;

/**
 * us.pcsw.dbbrowser.TablesTreeNode
 * -
 * A tree view node from which table nodes are attached.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 8, 2002	This class was created by Philip A. Chapman.</LI>
 * <LI>Feb 15, 2003 Added support for the StatusEvent.</LI>
 * </UL></P>
 */
public class TablesTreeNode
	extends DBObjectsTreeNode
{
	protected Connection con = null;
	protected TablesTreeNodeType type = null;

	/**
	 * Constructor for TablesTreeNode.
     * @param parent The parent object in the tree.
     * @param con The connection to the database server.
     * @param type The type of tables to be displayed under this node.  Views
     *         are considered by the DatabaseMetadata object to be a type of
     *         table.  For other types
     *         @see us.pcsw.dbbrowser.TablesTreeNodeType.
     * @exception SQLException Indicates an error loading table names from
     *                         the database connection.
	 */
	public TablesTreeNode
		(DBObjectsTreeNode parent, Connection con, TablesTreeNodeType type)
		throws SQLException
	{
		super(parent);
		// validate the connection
		if (con == null) {
		    throw new IllegalArgumentException("Invalid Connection.");
		} else {
			this.con = con;
		}
		// populate the data.
		this.type = type;	
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
	 * (Re)loads a list of tables in the catalog from the database.
     * @exception SQLException Indicates an error loading table names from
     *                         the database connection.
	 */
	public void refresh()
		throws SQLException
	{
		notifyParent(new StatusEvent(this, StatusTypeEnum.BUSY,
		                             "Loading the list of tables."));
		String cat = null;
		String schema = null;

		try {
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

			// Get a list of tables.
			CatalogTreeNode catNode = (CatalogTreeNode)getParent();
			DatabaseMetaData dmd = con.getMetaData();
			String tblTypes[] = {type.getQueryConstant()};
			ResultSet rs = dmd.getTables(catNode.getCatalogName(), null, "%", tblTypes);
			Vector v = new Vector();
			while (rs.next()) {
				cat = rs.getString("TABLE_CAT");
				if (rs.wasNull()) {
					cat = null;
				}
				schema = rs.getString("TABLE_SCHEM");
				if (rs.wasNull()) {
					schema = null;
				}
				v.add(new TableTreeNode(this, con, cat, schema, rs.getString("TABLE_NAME")));
			}
			rs.close();
			if (v.size() > 0) {
				insertChildNodes(v.toArray());
			}
		} catch (Throwable t) {
			notifyParent(new StatusEvent(this, t));
		} finally {
			notifyParent(new StatusEvent(this, StatusTypeEnum.NOT_BUSY,
			                             "Finished loading the list of tables."));
		}
	}

	/**
	 * Returns a string representation of this object.
	 */
    public String toString()
    {
		return type.toString();
    }
 }
