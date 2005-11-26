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
 * us.pcsw.dbbrowser.enclosing_type
 * -
 * Description
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 20, 2002	This class was created by Philip A. Chapman.</LI>
 * <LI>Feb 15, 2003 Added support for the StatusEvent.</LI>
 * <LI>Aug 06, 2003 Added awareness of schemas.  PAC. </LI>
 * </UL></P>
 */
public final class IndicesTreeNode
	extends DBObjectsTreeNode
{
	/**
	 * Connection to the database.
	 */
	private Connection con = null;

	/**
	 * Constructor for IndicesTreeNode.
	 * @param parent
	 */
	public IndicesTreeNode(DBObjectsTreeNode parent, Connection con)
	{
		super(parent);
		this.con = con;
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#isLeaf()
	 */
	boolean isLeaf() {
		return false;
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#refresh()
	 */
	public void refresh()
		throws
			ClassNotFoundException,
			IllegalAccessException,
			InstantiationException,
			SQLException
	{
		String curIndex = null;
		IndexTreeNode curIndexNode = null;
		String lastIndex = null;
		String sortDirection = null;

		notifyParent(new StatusEvent(this, StatusTypeEnum.BUSY,
		                             "Loading the list of indices."));

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
			
			TableTreeNode parentNode = (TableTreeNode)getParent();
	
			DatabaseMetaData dmd = con.getMetaData();
			ResultSet rs = dmd.getIndexInfo(parentNode.getCatalog(),
											parentNode.getSchema(),
			                                parentNode.getName(),
			                                false,
			                                false);
			while (rs.next()) {
				curIndex = rs.getString("INDEX_NAME");
				if (rs.wasNull() || curIndex == null) {
					// A node cannot be added for an index with no name.
					continue;
				} else if (! curIndex.equals(lastIndex)) {
					sortDirection = rs.getString("ASC_OR_DESC");
					if ("A".equals(sortDirection)) {
						sortDirection = "Ascending";
					} else if ("D".equals(sortDirection)) {
						sortDirection = "Descending";
					} else {
						sortDirection = "N/A";
					}
					curIndexNode = new IndexTreeNode(this,
					                                 rs.getString("INDEX_NAME"),
					                                 rs.getShort("TYPE"),
					                                 sortDirection,
					                                 ! rs.getBoolean("NON_UNIQUE"));
					insertChildNode(curIndexNode);
					lastIndex = curIndex;
				}
				curIndexNode.addColumn
					(new ColumnTreeNode(this, rs.getString("COLUMN_NAME")),
	                    (int)rs.getShort("ORDINAL_POSITION"));
			}
			rs.close();
		} catch (Throwable t) {
			notifyParent(new StatusEvent(this, t));
		} finally {
			notifyParent(new StatusEvent(this, StatusTypeEnum.NOT_BUSY,
			                             "Finished loading the list of indeces."));
		}
	}

	/**
	 * A string representation of this object.	
	 */
	public String toString()
	{
		return "Indices";
	}
}
