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

import java.io.IOException;
import java.io.InputStream;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.LinkedList;

import javax.swing.SwingUtilities;

import com.Ostermiller.util.CSVParser;

import us.pcsw.dbbrowser.cp.ConnectionProvider;

/**
 * A class that imports data from a CSV file into the database.
 * 
 * <b>NOTE:</b> Most of this functionality needs to be re-factored into
 * either a base class or a helper class because most of this functionality
 * would be necessary for any type of data import and is not CSV specific.
 *
 * @author pchapman
 */
public class CSVImportThread extends Thread
{
//	private static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
	private static final DateFormat DATE_FORMAT2 = new SimpleDateFormat("MM/dd/yy");
	private static final DateFormat TIME_FORMAT = DateFormat.getTimeInstance();
//	private static DateFormat TIMESTAMP_FORMAT = DateFormat.getDateTimeInstance();
	private static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
	
	private ImportColumn[] columns;
	private boolean createTable;
	private CSVImport csvImport;
	private InputStream iStream;
	private ConnectionProvider provider;
	private boolean skipFirstRow;
	private String tableName;
	
	/**
	 * 
	 */
	public CSVImportThread(
			CSVImport csvImport, ConnectionProvider provider,
			String tableName, boolean createTable, ImportColumn[] columns,
			InputStream iStream, boolean skipFirstRow
		)
	{
		super();
		this.columns = columns;
		this.createTable = createTable;
		this.csvImport = csvImport;
		this.iStream = iStream;
		this.provider = provider;
		this.skipFirstRow = skipFirstRow;
		this.tableName = tableName;
	}
	
	private void notifyListener(ImportEvent event)
	{
		SwingUtilities.invokeLater(new NotifierThread(csvImport, event));
	}
	
	public void run()
	{
		// Connect to the database.
		notifyListener(
				new ImportEvent(
						this, 0, 0,
						"Import started",
						ImportEvent.EVENT_TYPE_STARTED
					)
			);
		Connection con = null;
		try {
			con = provider.getConnection();
		} catch (Exception e) {
			notifyListener(
					new ImportEvent(
							this, 0, 0,
							"Unable to connect to the database: " +
							e.getLocalizedMessage(),
							ImportEvent.EVENT_TYPE_MESSAGE
						)
				);
			notifyListener(
					new ImportEvent(
							this, 0, 0,
							"Unable to connect to the database: " +
							e.getLocalizedMessage(),
							ImportEvent.EVENT_TYPE_STOPPED
						)
				);
			return;
		}
		
		if (createTable) {
			// Create the table.
			try {
				String sql = csvImport.createTableCreationSQL(tableName, columns);
				Statement stmt = con.createStatement();
				stmt.execute(sql);
			} catch (SQLException sqle) {
				notifyListener(
						new ImportEvent(
								this, 0, 0, "Unable to create the table " +
								tableName + ": " + sqle.getLocalizedMessage(),
								ImportEvent.EVENT_TYPE_STOPPED
							)
					);
				return;
			}
		}
		
		// Set up the insert statement
		PreparedStatement stmt = null;
		try {
			stmt = csvImport.createInsertStatement(con, tableName, columns);
		} catch (SQLException sqle) {
			notifyListener(
					new ImportEvent(
							this, 0, 0,
							"Unable to create the insert script: " +
							sqle.getLocalizedMessage(),
							ImportEvent.EVENT_TYPE_STOPPED
						)
				);
			return;
		}
		
		// Get a list of columns without the auto-increment field(s).
		LinkedList ll = new LinkedList();
		for (int i = 0; i < columns.length; i++) {
			if (columns[i] == null) {
				ll.add(null);
			} else if (! columns[i].getDataType().isAutoIncrement()) {
				ll.add(columns[i]);
			}
		}
		ImportColumn[] insertColumns = new ImportColumn[ll.size()];
		ll.toArray(insertColumns);
		
		// Initialize the stream.
		Object data;
		String[] row;
		try {
			CSVParser parser = new CSVParser(iStream);
		
			if (this.skipFirstRow) {
				// Skip the first row.
				parser.getLine(); // Throw away the return value.
			}

			// Import the data.
			int paramIndex;
			for (row = parser.getLine(); row != null; row = parser.getLine()) {
				paramIndex = 0;
				try {
					for (int j = 0; j < insertColumns.length; j++) {
						if (insertColumns[j] != null) {
							paramIndex++;
							if (j < row.length) {
								data = row[j];
							} else {
								data = null;
							}
							
							// Try to parse the data
							if (data != null) {
								String s = data.toString().trim(); 
								if (s.length() == 0) {
									data = null;
								} else {
									try {
										switch (insertColumns[j].getDataType().getJDBCType()) {
										case Types.DATE:
											try {
												// Try to parse the date using locale formatting
												data = DATE_FORMAT.parse(s);
											} catch (ParseException pe) {
												try {
													data = DATE_FORMAT2.parseObject(s);
												} catch (ParseException pe2) {
													// Let the JDBC driver do what it can.
												}
											}
											break;
										case Types.TIMESTAMP:
											try {
												// Try to parse the date using locale formatting
												data = TIMESTAMP_FORMAT.parse(s);
											} catch (ParseException pe) {
												// Let the JDBC driver do what it can.
											}
											break;
										case Types.TIME:
											try {
												// Try to parse the date using locale formatting
												data = TIME_FORMAT.parse(s);
											} catch (ParseException pe) {
												// Let the JDBC driver do what it can.
											}
											break;
										default:
											// Let the JDBC driver do what it can.
										}
										stmt.setObject(
												paramIndex, data,
												insertColumns[j].getDataType().getJDBCType()
											);
									} catch (Exception e) {
										data = null;
										notifyListener(
												new ImportEvent(
														this, 0, parser.getLastLineNumber(),
														"Unable to parse the data in row " +
														parser.getLastLineNumber() +
														" and column " + String.valueOf(j + 1) +
														" NULL will be inserted: " +
														e.getLocalizedMessage(),
														ImportEvent.EVENT_TYPE_STOPPED
													)
											);
									}
								}
							}
							
							// Set the value
							if (data == null) {
								stmt.setNull(
										paramIndex,
										insertColumns[j].getDataType().getJDBCType()
									);
							}
						}
				    }
			    	try {
			    		stmt.executeUpdate();
			    		paramIndex = 1;
			    		for (int i = 0; i < insertColumns.length; i++) {
			    			if (insertColumns[i] != null) {
			    				stmt.setNull(paramIndex++, insertColumns[i].getDataType().getJDBCType());
			    			}
			    		}
			    	} catch (SQLException sqle) {
						notifyListener(
								new ImportEvent(
										this, 0, parser.getLastLineNumber(),
										"Unable to insert the data in row " +
										parser.getLastLineNumber() + ": " +
										sqle.getLocalizedMessage(),
										ImportEvent.EVENT_TYPE_STOPPED
									)
							);
						return;
			    	}
				} catch (SQLException sqle) {
					notifyListener(
						new ImportEvent(
								this, 0, parser.getLastLineNumber(),
								"Unable to set data for row " +
								parser.getLastLineNumber() + ": " +
								sqle.getLocalizedMessage(),
								ImportEvent.EVENT_TYPE_MESSAGE
							)
						);
				}
			}
		} catch (IOException ioe) {
			notifyListener(
					new ImportEvent(
							this, 0, 0, "Unable to read the data into " +
							tableName + ": " + ioe.getLocalizedMessage(),
							ImportEvent.EVENT_TYPE_STOPPED
						)
				);
			return;
		}
		notifyListener(
				new ImportEvent(
						this, 0, 0, "Import into " + tableName + " done.",
						ImportEvent.EVENT_TYPE_STOPPED
					)
			);
	}
	
	class NotifierThread extends Thread
	{
		private CSVImport csvImport;
		private ImportEvent event;
		
		NotifierThread(CSVImport csvImport, ImportEvent event)
		{
			this.csvImport = csvImport;
			this.event = event;
		}
		
		public void run()
		{
			csvImport.notifyListeners(event);
		}
	}
}
