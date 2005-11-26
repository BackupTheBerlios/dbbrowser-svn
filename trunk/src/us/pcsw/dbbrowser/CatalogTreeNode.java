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

/**
 * us.pcsw.dbbrowser.CatalogTreeNode
 * -
 * A node in a tree representing a catalog (AKA Database) on the database
 * server.
 *
 * <P>Revision History:
 * <BR>08/07/2002 This class was created.
 *
 * @author Philip A. Chapman
 */
public final class CatalogTreeNode
    extends us.pcsw.dbbrowser.DBObjectsTreeNode
{
    /**
     * The name of the catalog
     */
    private String catalog = null;

	/**
	 * The connection to the database.
	 */
	private Connection con = null;

    /**
     * Constructs a new database catalog node.
     * @param parent The parent object in the tree.
     * @param con The connection to the database server.
     * @param catalog The name of the catalog.
     * @exception SQLException indicates that an error occurred while querying
     *                         the database server.
     */
    CatalogTreeNode(DBObjectsTreeNode parent,  Connection con, String catalog)
		throws SQLException
    {
		super(parent);
		this.catalog = catalog;
		if (parent == null) {
		    throw new IllegalArgumentException("Invalid parent tree node.");
		} else if (con == null) {
		    throw new IllegalArgumentException("Invalid Connection.");
//		} else if (catalog == null) {
//		    throw new IllegalArgumentException("Invalid catalog name.");
		} else {
			this.con = con;
		}
    }

	/**
	 * Returns the name of the catalog this node represents.
	 */
	String getCatalogName()
	{
		return catalog;
	}

    /**
     * Returns false.  This node is never a leaf.
     */
    boolean isLeaf()
    {
		return false;
    }

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
			children = null;
		}

	    // Create the children, procedures, system tables, user tables and
    	// views.
	    Object[] childNodes = new Object[4];
		childNodes[0] = new ProceduresTreeNode(this, con);
	    childNodes[1] = new TablesTreeNode(this, con,
	                                       TablesTreeNodeType.SYSTEM_TABLES);
	    childNodes[2] = new TablesTreeNode(this, con,
	                                       TablesTreeNodeType.USER_TABLES);
	    childNodes[3] = new TablesTreeNode(this, con,
	                                       TablesTreeNodeType.VIEWS);
	    insertChildNodes(childNodes);
	}

	/**
	 * Returns a string representation of this object.
	 */
    public String toString()
    {
    	if (catalog == null) {
    		// Some RDBMSs do not have a concept of catalogs
    		return "(default)";
    	} else {
			return catalog;
    	}
    }
}
