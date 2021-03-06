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

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * us.pcsw.dbbrowser.ResultSetTableModel
 * -
 * A table model which can be used to supply data to a table from a
 * java.sqlResultSet.  As the postgresql jdbc driver, which this application is
 * ment to use, does not support backward scrolling, read values are cached.
 * The number of rows is always returned as 1 more than the number of known
 * (cached) rows until all rows have been read from the ResultSet.  When the
 * last row is read from the resultset, fireTableRowsDeleted is used to notify
 * listeners that the last row is not available.  Also, this instance's
 * reference to the ResultSet is released at this point because all data has
 * been cached.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>05/10/2001 Development began on this class.</LI>
 * <LI>10/18/2002 Code was added to allow a string to be substituted for null
 *                values, thus improving the readability of resultsets in a
 *                JTable control.</LI>
 * <LI>03/13/2003 Added option to disable lazy load of cache.</LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public final class CachingResultSetTableModel
    extends ResultSetTableModel
{
	private static final long serialVersionUID = 1L;

	/**
     * Indicates that all rows have been read and all data has been cached.
     */
    boolean cacheFull;

    /**
     * The resultset from which data is displayed.
     */
    ResultSet rs;

    /**
     * Basic Constructor.
     */
    public CachingResultSetTableModel()
    {
		super();
    }

    /**
     * Constructor which initializes the new instance with a ResultSet.
     * @param rs The ResultSet from which table data is to be provided.
     * @exception SQLException Indicates an error reading data from the
     *                         resultset.
     */
    public CachingResultSetTableModel(ResultSet rs)
    	throws SQLException
    {
		super(rs);
    }

	/**
	 * Indicates whether the intire resultset has been cached.
	 * @return boolean True if the entire resultset has been cached.
	 */
	public boolean getCacheFull()
	{
		return cacheFull;
	}

	/**
	 * Returns the number of rows that are cached.
	 * @return int The number of rows that are cached.
	 */
	public int getCachedRowCount()
	{
		if (columns == null) {
			return 0;
		} else {
			return columns[0].size();
		}
	}

    public int getRowCount()
    {
		int rowCount;
		if (columns == null) {
		    rowCount = 0;
		} else {
		    if (cacheFull) {
				rowCount = columns[0].size();
		    } else {
				// return 1 more than however many are cached until all are
				// cached and the number of rows is known.
				rowCount = columns[0].size() + 1;
		    }
		}
		return rowCount;
    }

    public Object getValueAt(int row, int column, boolean returnNulls)
    {
		// If the row has been cached, return the cached data.  Otherwise, make
		// an attempt to read more data.  If more data is cached, fire a
		// notification that table rows have been inserted, so that the model
		// stays 1 row ahead of the table.  If no more data is cached, mark
		// the cache as full and fire notification of the 1 extra row as
		// having been deleted.
		int currentPosition = 0;
		int rowsToRead = 0;
		Object obj;
	
		if (columns == null) {
		    return null;
		} else {
		    if (columns[0].size() < row + 1) {
			    // Attempt to read the row from the database.
			    currentPosition = columns[0].size();
			    int cachePageSize = Preferences.getCachePageSize();
				rowsToRead = row - currentPosition + cachePageSize;
				int i = -1;
			    while (++i < rowsToRead || cachePageSize == 0) {
					try {
						if ((! cacheFull) && rs.next()) {
							// The row exists, cache the data.
							for (int j = 0; j < columns.length; j++) {
							    try {
									obj = rs.getString(j + 1);
									if (rs.wasNull()) {
										obj = null;
									}
							    } catch (SQLException sqle2) {
									handleException(sqle2);
									obj = null;
							    }
							    columns[j].add(obj);
							}
					    } else {
							// No more data
							cacheFull = true;
							rs.close();
							rs = null;   // Release unneeded resources
							fireTableRowsDeleted(currentPosition + i, row);
							return null;
					    } 
					} catch (SQLException sqle) {
					    handleException(sqle);
					    return null;
					}
			    }
			    // Increase the number of rows the table thinks there are.
			    // Return the cell's data.
			    fireTableRowsInserted(row + 1, row + 1);
		    } // end if (columns[0].size() < row + 1)
		    obj = columns[column].get(row);
		    if (obj == null && ! returnNulls) {
		    	return Preferences.getRepresentationForNull();
		    } else {
		    	return obj;
		    }
		}
    }

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
    protected void loadResultSet(ResultSet rs) throws SQLException
    {
		this.rs = rs;
		cacheFull = false;
		// Pull first cached page of results
		getValueAt(0, 0, true);
    }
    
    protected boolean isResultSetLoaded(ResultSet rs)
    {
    	return rs.equals(this.rs);
    }
}
