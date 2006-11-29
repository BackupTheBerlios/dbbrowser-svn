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

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * us.pcsw.dbbrowser.IndexTreeNode
 * -
 * Node representing a table index.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 20, 2002	This class was created by Philip A. Chapman.</LI>
 * <LI>Feb 15, 2003 A few minor bugs were fixed.</LI>
 * </UL></P>
 */
public class IndexTreeNode
	extends DBObjectsTreeNode
{
	/**
	 * The child node which lists columns.
	 */
	private ColumnsTreeNode columnsNode = null;

	/**
	 * The name of the index.
	 */
	private String name = null;

	/**
	 * Constructor for IndexTreeNode.
	 * @param parent The parent node.
	 * @param name Name of the index.
	 * @param type type of index.
	 * @param direction The direction in which the index is sorted.
	 * @param unique indicates that indexed values are unique.
	 */
	public IndexTreeNode(DBObjectsTreeNode parent, String name, short type,
	                      String direction, boolean unique)
	{
		super(parent);
		this.name = name;
		insertChildNode("Sort Direction: " + direction);
		insertChildNode("Unique: " + unique);
		StringBuffer typeDesc = new StringBuffer("Type: ");
		switch (type) {
			case DatabaseMetaData.tableIndexClustered:
				typeDesc.append("Clustered");
				break;
			case DatabaseMetaData.tableIndexHashed:
				typeDesc.append("Hashed");
				break;
			case DatabaseMetaData.tableIndexStatistic:
				typeDesc.append("Statistic");
				break;
			default:
				typeDesc.append("Other");
		}
		insertChildNode(typeDesc.toString());
		columnsNode = new ColumnsTreeNode(this, null);
		insertChildNode(columnsNode);
	}

	void addColumn(ColumnTreeNode node, int position)
	{
		columnsNode.addColumn(node, position);
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#isLeaf()
	 */
	boolean isLeaf() {
		return false;
	}

	/**
	 * Does nothing.  This class is immutable and its children are static.
	 */
	public void refresh()
		throws
			ClassNotFoundException,
			IllegalAccessException,
			InstantiationException,
			SQLException
	{
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString()
	{
		return name;
	}
}
