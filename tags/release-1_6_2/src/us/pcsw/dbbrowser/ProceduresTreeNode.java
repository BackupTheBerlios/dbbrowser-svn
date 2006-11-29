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
 * us.pcsw.dbbrowser.ProceduresTreeNode
 * -
 * The node in a tree which lists stored procedures.
 * 
 * <P><B>Revision History:</B><UL>
 * <LI>Nov 11, 2002	This class was created by Philip A. Chapman.</LI>
 * <LI>Feb 15, 2003 Added support for the StatusEvent.</LI>
 * <LI>Aug 06, 2003 Added awareness of schemas.  PAC. </LI>
 * <LI>Sep 05, 2003 Fixed a bug where oracle SPs were being shown twice.  Once
 * <LI>             as an SP, the second as a packaged item.  PAC. </LI>
 * </UL></P>
 */
public class ProceduresTreeNode
	extends DBObjectsTreeNode
{
	protected Connection con = null;
	
	/**
	 * Constructor for ProceduresTreeNode.
     * @param parent The parent object in the tree.
     * @param con The connection to the database server.
     * @exception SQLException Indicates an error loading procedure names from
     *                         the database connection.
	 */
	public ProceduresTreeNode(DBObjectsTreeNode parent, Connection con)
		throws SQLException
	{
		super(parent);
		// validate the connection
		if (con == null) {
		    throw new IllegalArgumentException("Invalid Connection.");
		} else {
			this.con = con;
		}
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#isLeaf()
	 */
	boolean isLeaf() {
		return false;
	}

	/**
	 * (Re)loads a list of procedures in the catalog from the database.
     * @exception SQLException Indicates an error loading table names from
     *                         the database connection.
	 */
	public void refresh()
		throws SQLException
	{
		notifyParent(new StatusEvent(this, StatusTypeEnum.BUSY,
		                             "Loading the list of procedures."));
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

			// Get a list of procedures
			CatalogTreeNode catNode = (CatalogTreeNode)getParent();
			DatabaseMetaData dmd = con.getMetaData();
			cat = catNode.getCatalogName();
			// If you pass null in for Oracle, you get procedures and packages.
			// So far, sending in "" which brings back SPs not belonging to a
			// catalog works OK.  The only other driver which returns null for
			// catalog name is PostgreSQL.  
			cat = cat == null ? "" : cat;
			ResultSet rs = dmd.getProcedures(cat, null, "%");
			Vector v = new Vector();
			while (rs.next()) {
				cat = rs.getString("PROCEDURE_CAT");
				if (rs.wasNull()) {
					cat = null;
				}
				schema = rs.getString("PROCEDURE_SCHEM");
				if (rs.wasNull()) {
					schema = null;
				}
				v.add(new ProcedureTreeNode(this, con, cat, schema, rs.getString("PROCEDURE_NAME")));
			}
			rs.close();
			if (v.size() > 0) {
				insertChildNodes(v.toArray());
			}
		} catch (Throwable t) {
			notifyParent(new StatusEvent(this, t));
		} finally {
			notifyParent(new StatusEvent(this, StatusTypeEnum.NOT_BUSY,
			                             "Finished loading the list of procedures."));
		}
	}
	
	/**
	 * Returns a string representation of this object.
	 */
    public String toString()
    {
		return "Procedures";
    }
}
