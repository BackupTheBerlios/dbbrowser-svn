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

import us.pcsw.util.BaseEnum;

/**
 * us.pcsw.dbbrowser.TablesTreeNodeType
 * -
 * Indicates the type for a TablesTreeNode.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 9, 2002 This class was created.</LI>
 * </UL></P>
 */
public final class TablesTreeNodeType
	extends BaseEnum
{

	public static final TablesTreeNodeType SYSTEM_TABLES = new TablesTreeNodeType(0);
	public static final TablesTreeNodeType USER_TABLES = new TablesTreeNodeType(1);
	public static final TablesTreeNodeType VIEWS = new TablesTreeNodeType(2);

	private static final String[] QUERY_CONST =
		{"SYSTEM TABLE", "TABLE", "VIEW"};
	private static final String[] DESCRIPTIONS =
		{"System Tables", "User Tables", "Views"};

	/**
	 * Constructor for TablesTreeNodeType.
	 * @param value
	 */
	private TablesTreeNodeType(int value) {
		super(value);
	}

	/**
	 * Returns the type value which will be used by TablesTreeNode when
	 * querying for a list of tables.
	 */
	String getQueryConstant()
	{
		return QUERY_CONST[value];
	}

	/**
	 * Gives a string representatin of this object.
	 */
	public String toString()
	{
		return DESCRIPTIONS[value];
	}
}
