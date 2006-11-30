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
package us.pcsw.dbbrowser.swing;

import java.awt.Component;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import us.pcsw.dbbrowser.*;

/**
 * us.pcsw.dbbrowser.swing.DBViewTreeCellRenderer
 * -
 * Determines how to display nodes and leaves in the DBObjects tree view.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 23, 2002 This class was created.</LI>
 * </UL></P>
 */
public class DBViewTreeCellRenderer
	extends DefaultTreeCellRenderer
{
	private static final long serialVersionUID = 1L;

	Icon catalogIcon = null;
	Icon columnIcon = null;
	Icon procIcon = null;
	Icon sysTableIcon = null;
	Icon tableIcon = null;
	Icon viewIcon = null;
	
	/**
	 * Constructor for DBViewTreeCellRenderer.
	 */
	public DBViewTreeCellRenderer() {
		super();
	}

	/**
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(JTree, Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean selected,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus)
	{
		super.getTreeCellRendererComponent(tree, value, selected, expanded,
                                            leaf, row, hasFocus);
		if (value instanceof CatalogTreeNode) {
			setIcon(catalogIcon);
		} else if (value instanceof ColumnTreeNode) {
			setIcon(columnIcon);
		} else if (value instanceof ProcedureTreeNode) {
			setIcon(procIcon);
		} else if (value instanceof TableTreeNode) {
			
		}
		return this;
	}
}
