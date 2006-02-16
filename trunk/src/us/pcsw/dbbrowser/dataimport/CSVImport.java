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

import java.util.Vector;

import com.Ostermiller.util.CSVParser;

import us.pcsw.dbbrowser.cp.ConnectionProvider;

/**
 * us.pcsw.dbbrowser.dataimport.CSVImport
 * -
 * A description of this class.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>Mar 10, 2005 This class was created by pchapman.</LI>
 * </UL></P>
 */
public class CSVImport extends DataImport
{
	/**
	 * @see us.pcsw.dbbrowser.dataimport.DataImport#importData(us.pcsw.dbbrowser.cp.ConnectionProvider, java.lang.String, boolean, us.pcsw.dbbrowser.dataimport.ImportColumn[], java.io.InputStream, boolean)
	 */
	public void importData(
			ConnectionProvider provider, String tableName,
			boolean createTable, ImportColumn[] columns, InputStream iStream,
			boolean skipFirstRow
		)
	{
		CSVImportThread thread = new CSVImportThread(
				this, provider, tableName, createTable,
				columns, iStream, skipFirstRow
			);
		thread.start();
	}

	/**
	 * @see us.pcsw.dbbrowser.dataimport.DataImport#sampleData(java.io.InputStream)
	 */
	public String[][] sampleData(InputStream iStream, int rowCount)
		throws IOException
	{
		int colCount = 0;
		Vector rows = new Vector();
		String[] row;
		CSVParser parser = new CSVParser(iStream);
		rowCount++;
		for (
				row = parser.getLine(); rows.size() < rowCount &&
				row != null; row = parser.getLine()
			)
		{
			if (row.length > colCount) {
				colCount = row.length;
			}
			rows.add(row);
		}
		
		String[][] returnValue = new String[rows.size()][colCount];
		for (int i = 0; i < returnValue.length; i++) {
			row = (String[])rows.elementAt(i);
			for (int j = 0; j < row.length; j++) {
				if (j >= row.length) {
					returnValue[i][j] = null;  // Perhaps I should use empty String?
				} else {
					returnValue[i][j] = row[j];
				}
			}
		}
		
		return returnValue;
	}
}
