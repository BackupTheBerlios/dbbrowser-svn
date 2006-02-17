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

import us.pcsw.util.Debug;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

/**
 * us.pcsw.dbbrowser.ResultSetTableModel
 * -
 * A table model which can be used to supply data to a table from a
 * java.sqlResultSet.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>05/10/2001 Development began on this class.</LI>
 * <LI>10/18/2002 Code was added to allow a string to be substituted for null
 *                values, thus improving the readability of resultsets in a
 *                JTable control.</LI>
 * <LI>03/13/2003 Added option to disable lazy load of cache.</LI>
 * <LI>02/16/2006 This class was split into two classes, this abstract class
 *                and the CachingResultSetTableModel subclass.  This was so
 *                a new LoadedResultSetTableModel subclass could be created
 *                and common code could be reused. PAC </LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public abstract class ResultSetTableModel
    extends javax.swing.table.AbstractTableModel
{
	private static final long serialVersionUID = 1L;

    /**
     * Holds vectors which hold cached data.
     */
    protected List columns[] = null;

    /**
     * The names of the columns.
     */
    protected String columnNames[] = null;
    
    /**
     * The type of data in the columns.
     */
    protected int columnTypes[] = null;

    /**
     * Basic Constructor.
     */
    public ResultSetTableModel()
    {
		super();
    }

    /**
     * Constructor which initializes the new instance with a ResultSet.
     * @param rs The ResultSet from which table data is to be provided.
     * @exception SQLException Indicates an error reading data from the
     *                         resultset.
     */
    public ResultSetTableModel(ResultSet rs)
    	throws SQLException
    {
		super();
		setResultSet(rs);
    }

    /**
     * Called whenever an exception is thrown.
     * @param exception Throwable
     */
    protected void handleException(Throwable exception)
    {
		JOptionPane.showMessageDialog(null, exception.getMessage(),
		                              "Unexpected Error",
		                              JOptionPane.ERROR_MESSAGE);
		Debug.log(exception);
    }

    public int getColumnCount()
    {
		if (columns == null) {
		    return 0;
		} else {
		    return columns.length;
		}
    }

    public String getColumnName(int column)
    {
		if (columns == null || column < 0 || column > columns.length) {
		    return "Column " + String.valueOf(column + 1);
		} else {
		    return columnNames[column];
		}
    }

	/**
	 * Returns the type of the column's contents.  Values returned are one of
	 * the constants defined in java.sql.Types.
	 * @param column The column for which the type is to be returned.
	 * @return An integer value indicating the data type as defined in
	 *                  java.sql.Types.
	 */
	public int getColumnType(int column)
	{
		if (columnTypes == null || column < 0 || column > columns.length) {
			return -1;
		} else {
			return columnTypes[column];
		}
	}

	public Object getValueAt(int row, int column)
	{
		return getValueAt(row, column, false);
	}

	/**
	 * Gets the data in the cell indicated unless the cell's data is null in
	 * which case, an identifer may be substituted.
	 * @param row The row the cell is in.
	 * @param column The column the cell is in.
	 * @param returnNulls Whether the value NULL or a special user-friendly
	 *                    identifier should be returned if the cell's data is
	 *                    null;
	 * @return The cell's data.
	 */
    public abstract Object getValueAt(int row, int column, boolean returnNulls);

    /**
     * Sets/changes the ResultSet from which data is to be provided.
     * @exception IllegalArgumentException Indicats that the provided ResultSet
     *                                     is a forward only and does not
     *                                     support backward scolling.
     * @param rs The ResultSet from which table data is to be provided.  If
     *           null, the Model represents no data; thus an empty table.
     * @return The number of rows in the resultset or -1 if the resultset is to
     *          be cached lazily.
     */
    public void setResultSet(ResultSet rs) throws SQLException
    {
		int columnCount = 0;
		
		if (rs == null) {
		    columns = null;
		} else {
		    if (! isResultSetLoaded(rs)) {
				try {
				    columnCount = rs.getMetaData().getColumnCount();
				} catch (SQLException e) {}
				// Initialize the data cache.
				columns = new List[columnCount];
				for (int i = 0; i < columnCount; i++) {
				    columns[i] = new ArrayList();
				}
				// Get column headers
				columnNames = new String[columnCount];
				columnTypes = new int[columnCount];
				for (int i = 0; i < columnCount; i++) {
				    try {
						columnNames[i] =
					    	rs.getMetaData().getColumnLabel(i + 1);
					    columnTypes[i] =
					    	rs.getMetaData().getColumnType(i + 1);
				    } catch (SQLException e) {
				    	if (columnNames[i] == null) {
	 						columnNames[i] = "Column " + String.valueOf(i + 1);
				    	}
				    	columnTypes[i] = Types.VARCHAR;
				    }
				}
				
				loadResultSet(rs);

				fireTableStructureChanged();
				fireTableDataChanged();
		    }
		}
    }

    protected abstract boolean isResultSetLoaded(ResultSet rs);
    protected abstract void loadResultSet(ResultSet rs) throws SQLException;
}
