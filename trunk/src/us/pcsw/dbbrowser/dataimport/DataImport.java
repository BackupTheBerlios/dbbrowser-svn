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

import java.io.InputStream;
import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.LinkedList;
import java.util.List;

import us.pcsw.dbbrowser.cp.ConnectionProvider;

/**
 * us.pcsw.dbbrowser.dataimport.DataImport
 * -
 * Imports data from into a database table.  What the source is will depend on
 * the subclass.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 10, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public abstract class DataImport extends Object
{
	protected List importListeners = new LinkedList();
	
	/**
	 * Adds a listener to the list of listeners to be notified of an
	 * ImportEvent.
	 * @param listener The listener.
	 */
	public void addImportListener(ImportListener listener)
	{
		synchronized (importListeners) {
			if (! importListeners.contains(listener)) {
				importListeners.add(listener);
			}
		}
	}
	
	/**
	 * Creates a row insert SQL statement and prepares it for use.
	 * @param con The connection to the database into which the data will be
	 *            inserted.
	 * @param tableName The name of the table into which the data will be
	 *                  inserted.
	 * @param columns The columns into which the data will be inserted.
	 * @return The prepared statement.
	 * @throws SQLException Indicates a problem preparing the statement.
	 */
	public PreparedStatement createInsertStatement(
			Connection con, String tableName, ImportColumn[] columns
		)
		throws SQLException
	{
		StringBuffer body = new StringBuffer("INSERT INTO ");
		StringBuffer parameters = new StringBuffer();
		body.append(tableName);
		body.append(" (");
		boolean first = true;
		for (int i = 0; i < columns.length; i++) {
			if (
					columns[i] != null &&
					(! columns[i].getDataType().isAutoIncrement())
				)
			{
				if (first) {
					first = false;
				} else {
					body.append(", ");
					parameters.append(", ");
				}
				body.append(columns[i].getColumnName());
				parameters.append('?');
			}
		}
		body.append(" ) values ( ");
		body.append(parameters.toString());
		body.append(" )");
		
		return con.prepareStatement(body.toString());
	}

	/**
	 * Creates a script which will create a table with the given name and
	 * columns.
	 * @param tableName The name of the table.
	 * @param columns The column that the table should have.
	 * @return The SQL script.
	 */
	public String createTableCreationSQL(
			String tableName, ImportColumn[] columns
		)
	{
		Boolean b;
		StringBuffer sb = new StringBuffer("CREATE TABLE ");
		sb.append(tableName);
		sb.append('(');
		for (int i = 0; i < columns.length; i++) {
			if (columns[i] != null) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(columns[i].getColumnName());
				sb.append(' ');
				sb.append(columns[i].getDataType().getTypeName());
				b = columns[i].getDataType().isPrecisionRequired();
				if (b != null && b.booleanValue()) {
					sb.append('(');
					sb.append(columns[i].getPrecision());
					b = columns[i].getDataType().isScaleRequired();
					if (b != null && b.booleanValue()) {
						sb.append(',');
						sb.append(columns[i].getScale());
					}
					sb.append(')');
				}
			}
		}
		sb.append(')');
		return sb.toString();
	}
	
	/**
	 * Imports the data into the table.
	 * @param provider The provider which will provide the database connection.
	 * @param tableName The name of the table into which the data will be
	 *                  inserted.
	 * @param createTable Whether the table should be created before the data
	 *                    is imported.  <b>Note:</b> If a table with the
	 *                    provided name already exists, an error will occur.
	 * @param columns The columns into which data will be stored.  The data
	 *                will be inserted into each column in turn until all
	 *                columns have data.  The one exeption is a data type
	 *                which is auto-incrementing.  No data is stored in an
	 *                auto-incrementing row.
	 * @param iStream The input data.
	 */
	public abstract void importData(
			ConnectionProvider provider, String tableName,
			boolean createTable, ImportColumn[] columns,
			InputStream iStream, boolean skipFirstRow
		);

	/**
	 * Notifies listeners of a change in the import status.
	 * @param event The event.
	 */
	void notifyListeners(ImportEvent event)
	{
		ImportEvent e2 = new ImportEvent(
				this, event.getRowCount(), event.getCurrentRow(),
				event.getMessage(), event.getEventType()
			);
		ImportListener[] listeners = null;
		synchronized (importListeners) {
			listeners = new ImportListener[importListeners.size()];
			importListeners.toArray(listeners);
		}
		for (int i = 0; listeners != null && i < listeners.length; i++) {
			listeners[i].importStatusChanged(e2);
		}
	}
	
	/**
	 * Removes the listener from the list to be notified of ImportEvents.
	 * @param listener The listner.
	 */
	public void removeImportListener(ImportListener listener)
	{
		synchronized (importListeners) {
			importListeners.remove(listener);
		}
	}
	
	/**
	 * Samples the data.  Up to 5 rows are returned.
	 * @param iStream The input data.
	 * @return The sample data.  The first dimension is the rows, the second
	 *         dimension is the columns.
	 */
	public abstract String[][] sampleData(InputStream iStream, int rowCount)
		throws IOException;
}
