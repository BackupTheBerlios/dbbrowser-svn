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

import java.sql.Types;
import java.util.Vector;

/**
 * us.pcsw.dbbrowser.ColumnTreeNode
 * -
 * Represents a column in the DB tree view.  Among other things, this object
 * can be used to represent columns in tables and views as well as parameters
 * in stored procedures.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 19, 2002 This class was created.</LI>
 * </UL></P>
 */
public class ColumnTreeNode
	extends DBObjectsTreeNode
{
	private int jdbcType;
	private String name = null;
	private boolean nameOnly = false;
	private int precision;
	private int scale;
	private String typeName;
	
	/**
	 * Constructor for ColumnTreeNode for which no data beyond its name is
	 * known.
	 * @param parent The parent node.
	 * @param name The name of the column.
	 */
	public ColumnTreeNode(DBObjectsTreeNode parent, String name)
	{
		super(parent);
		this.name = name;
		this.nameOnly = true;
		children = new Vector();
	}

	/**
	 * Constructor for ColumnTreeNode using data returned from
	 * DatabaseMetadata.getColumns().
	 * @param parent The parent node.
	 * @param name The name of the column.
	 * @param type The type of the column.
	 * @param typeName A string description of the column type.
	 * @param size The size of the column.
	 * @param nullable Whether the column can contain null values.
	 */
	public ColumnTreeNode(DBObjectsTreeNode parent, String name, int type,
	                       String typeName, int size, int scale,
	                       boolean nullable)
	{
		super(parent);
		this.jdbcType = type;
		this.name = name;
		this.precision = size;
		this.scale = scale;
		this.typeName = typeName;

		Vector childNodes = new Vector();

		childNodes.add("Type: " + typeName);
		if ((type & Types.NUMERIC) == Types.NUMERIC) {
			childNodes.add("Precision: " + size);
			childNodes.add("Scale: " + scale);
		} else if (type != Types.TIME && type != Types.TIMESTAMP) {
			childNodes.add("Length: " + size);
		}
		childNodes.add("Nullable: " + nullable);
		insertChildNodes(childNodes.toArray());
	}

	/**
	 * Constructor for ColumnTreeNode using data returned from
	 * DatabaseMetadata.getProcedureColumns().
	 * @param parent The parent node.
	 * @param name The name of the column.
	 * @param type The type of the column.
	 * @param typeName A string description of the column type.
	 * @param typeName The precision of the data if the column is a numeric
	 *                  type.
	 * @param size The size of the column's data if the column is non-numeric.
	 * @param scale The scale of the data if the column is a numeric type.
	 * @param nullable Whether the column can contain null values.
	 */
	public ColumnTreeNode(DBObjectsTreeNode parent, String name, int type,
	                       String typeName, int precision, int length,
	                       int scale, boolean nullable)
	{
		super(parent);
		this.jdbcType = type;
		this.name = name;
		this.precision = precision;
		this.scale = scale;
		this.typeName = typeName;

		Vector childNodes = new Vector();

		childNodes.add("Type: " + typeName);
		if ((type & Types.NUMERIC) == Types.NUMERIC) {
			childNodes.add("Precision: " + precision);
			childNodes.add("Scale: " + scale);
		} else if (type != Types.TIME && type != Types.TIMESTAMP) {
			childNodes.add("Length: " + length);
		}
		childNodes.add("Nullable: " + nullable);
		insertChildNodes(childNodes.toArray());
	}
	
	public int getPrecision()
	{
		return precision;
	}
	
	public int getScale()
	{
		return scale;
	}
	
	public int getType()
	{
		return jdbcType;
	}
	
	public String getTypeName()
	{
		return typeName;
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#isLeaf()
	 */
	boolean isLeaf()
	{
		// If this column was created with the constructor
		// ColumnTreeNode(DBObjectsTreeNode, String), there will be no info
		// beyond name to display.
		return nameOnly;
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#refresh()
	 * Does nothing because the node is immutable and its children are static.
	 */
	public void refresh() {}

	/**
	 * Returns a string representation of the object.
	 */	
	public String toString()
	{
		return name;
	}
}
