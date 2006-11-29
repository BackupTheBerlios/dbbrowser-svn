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
package us.pcsw.dbbrowser.dataimport;

import us.pcsw.dbbrowser.cp.DataType;

/**
 * us.pcsw.dbbrowser.dataimport.ImportColumn
 * -
 * A description of this class.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 10, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class ImportColumn
{
	// CONSTRUCTORS
	
	/**
	 * 
	 */
	public ImportColumn() {
		super();
	}
	
	public ImportColumn(String columnName, DataType type, int precision, int scale)
	{
		setColumnName(columnName);
		setDataType(type);
		setPrecision(precision);
		setScale(scale);
	}
	
	// MEMBERS
	
	private String columnName;
	public String getColumnName()
	{
		return columnName;
	}
	public void setColumnName(String name)
	{
		this.columnName = name;
	}
	
	private DataType dataType;
	public DataType getDataType()
	{
		return dataType;
	}
	public void setDataType(DataType type)
	{
		this.dataType = type;
	}
	
	private int precision;
	public int getPrecision()
	{
		return precision;
	}
	public void setPrecision(int precision)
	{
		this.precision = precision;
	}
	
	private int scale;
	public int getScale()
	{
		return scale;
	}
	public void setScale(int scale)
	{
		this.scale = scale;
	}
	
	// METHODS	
}
