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
 * us.pcsw.dbbrowser.ColumnsTreeNode
 * -
 * Parent node for a display of the columns.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 20, 2002	This class was created by Philip A. Chapman.</LI>
 * <LI>Feb 15, 2003 Added support for the StatusEvent. </LI>
 * <LI>Aug 06, 2003 Made the class schema aware.  PAC.  </LI>
 * </UL></P>
 */
public final class ColumnsTreeNode
	extends DBObjectsTreeNode
{
	/**
	 * The connection to the database.
	 */
	private Connection con = null;

	/**
	 * Constructor for ColumnsTreeNode.
	 * @param parent The parent node.
	 */
	public ColumnsTreeNode(DBObjectsTreeNode parent, Connection con)
	{
		super(parent);
		this.con = con;
	}

	void addColumn(ColumnTreeNode node, int index)
	{
		insertChildNode(node, index);
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#isLeaf()
	 */
	boolean isLeaf()
	{
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
		notifyParent(new StatusEvent(this, StatusTypeEnum.BUSY,
			                         "Loading the list of columns."));
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
			
			DBObjectsTreeNode parentNode = getParent();
			
			Vector v;
			if (parentNode instanceof ProcedureTreeNode) {
				v = getProcedureColumns((ProcedureTreeNode)parentNode);
			} else if (parentNode instanceof TableTreeNode) {
				v = getTableColumns((TableTreeNode)parentNode);
			} else {
				return;
			}
	
			children = null;
			if (v.size() > 0) {
				insertChildNodes(v.toArray());
			}
		} catch (Throwable t) {
			notifyParent(new StatusEvent(this, t));
		} finally {
			notifyParent(new StatusEvent(this, StatusTypeEnum.NOT_BUSY,
			                             "Finished loading the list of columns."));
		}
	}

	/**
	 * Refreshes the list of columns using DatabaseMetaData.getProcedureColumns.
	 */
	private Vector getProcedureColumns(ProcedureTreeNode procNode)
		throws
			ClassNotFoundException,
			IllegalAccessException,
			InstantiationException,
			SQLException
	{
		DatabaseMetaData dmd = con.getMetaData();
		ResultSet rs = dmd.getProcedureColumns
							(procNode.getCatalog(), procNode.getSchema(),
							 procNode.getName(), "%");
		Vector v = new Vector();
		while (rs.next()) {	
			v.add(new ColumnTreeNode(this,
			                         rs.getString("COLUMN_NAME"),
			                         rs.getInt("DATA_TYPE"),
			                         rs.getString("TYPE_NAME"),
			                         rs.getInt("PRECISION"),
			                         rs.getInt("LENGTH"),
			                         rs.getInt("SCALE"),
			                         rs.getBoolean("NULLABLE")));
		}
		rs.close();
		return v;
	}
	
	/**
	 * Refreshes the list of columns using DatabaseMetaData.getColumns.
	 */
	private Vector getTableColumns(TableTreeNode tableNode)
		throws
			ClassNotFoundException,
			IllegalAccessException,
			InstantiationException,
			SQLException
	{
		DatabaseMetaData dmd = con.getMetaData();
		ResultSet rs = dmd.getColumns(tableNode.getCatalog(),
		                              tableNode.getSchema(),
									  tableNode.getName(), null);
		Vector v = new Vector();
		while (rs.next()) {
			v.add(new ColumnTreeNode(this,
			                         rs.getString("COLUMN_NAME"),
			                         rs.getInt("DATA_TYPE"),
			                         rs.getString("TYPE_NAME"),
			                         rs.getInt("COLUMN_SIZE"),
			                         rs.getInt("DECIMAL_DIGITS"),
			                         (rs.getInt("NULLABLE") == DatabaseMetaData.columnNullable)));
		}
		rs.close();
		return v;		
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString()
	{
		if (getParent() instanceof ProcedureTreeNode) {
			return "Parameters";
		} else {
			return "Columns";
		}
	}
}
