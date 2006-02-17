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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Date;

import us.pcsw.dbbrowser.event.StatusEvent;
import us.pcsw.dbbrowser.event.StatusTypeEnum;

/**
 *  us.pcsw.dbbrowser.swing.ReportPrinter
 * -
 * This class creates a report based on a resultset.
 *
 * <P><B>Revision History:</B><UL>
 * <LI> Dec 31, 2003 This class was created by Philip A. Chapman.</LI>
 * <LI> Mar 11, 2005 PAC Added the ability to cancel a statement that is being
 *                   executed. </LI>
 * </UL></P>
 */
public final class SQLExecutionWorker extends ExecutionWorker
{
	/**
	 * Indicates that the query is being cancelled so that the user is not
	 * given an error message.
	 */
	private boolean cancel = false;
	
	/**
	 * A connection to the server on which the sql statement will be
	 * executed.
	 */
	private Connection con = null;

	/**
	 * The sql statement to execute.
	 */
	private String sql = null;

	/**
	 * The statement object used to run the query.
	 */
	private Statement stmt = null;
	
	/**
	 * Creates a new SQLExcecutionWorker.
	 * @param con The connection to the database on which the statement will
	 *            be run.
	 * @param sql The statement to execute.
	 */
	public SQLExecutionWorker(Connection con, String sql)
	{
		super();
		if (con == null) {
			throw new IllegalArgumentException("The connection object cannot be null.");
		} else {
			this.con = con;
		}
		setSQL(sql);
	}

	public void interrupt() {
		if (stmt != null) {
			cancel = true;
			try {
				stmt.cancel();
			} catch (SQLException sqle) {
				notifyStatusListeners(new StatusEvent(this, sqle));
			}
		}
		notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));
		super.interrupt();
	}
	
	/**
	 * @see us.pcsw.swing.SwingWorker#construct()
	 */
	public Object construct()
	{
		SQLExecutionResults execResults = new SQLExecutionResults();
		long time = 0;
		try {
			stmt = con.createStatement();
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.BUSY));
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_STARTED));
			time = new Date().getTime();
			stmt.execute(getSQL());
			execResults.setRunTimeMills(new Date().getTime() - time);
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));

			ResultSet rs = null;
			boolean handleMultipleResults = Preferences.getCachePageSize() < 1;
			int count = stmt.getUpdateCount();
			boolean hasMoreResults;
			do {
				if (count == -1) {
					// We have a resultset
					rs = stmt.getResultSet();
					ResultSetTableModel rstm = null;
					if (handleMultipleResults) {
						rstm = new LoadedResultSetTableModel(rs);
					} else {
						rstm = new CachingResultSetTableModel(rs);
					}
					execResults.getResultSetModelList().add(rstm);
				} else {
					// We have update results
					execResults.getResultCountList().add(Integer.valueOf(count));
				}
				// The order of stmt.getMoreResults(), then
				// stmt.getUpdateCount() is crucial.
				hasMoreResults = stmt.getMoreResults();
				count = stmt.getUpdateCount();
			} while (
					handleMultipleResults &&
					(hasMoreResults || count > -1)
				);
		} catch (SQLException e) {
			// Don't report the error if the statement is being cancelled.
			if (cancel) {
				execResults.setRunTimeMills(new Date().getTime() - time);
			} else {
				execResults.setRunTimeMills(new Date().getTime() - time);
				execResults.getExceptionList().add(e);
				notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));
			}
		} catch (Throwable t) {
			execResults.setRunTimeMills(0);
			execResults.getExceptionList().add(t);
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));
		}
		return execResults;
	}
	
	/**
	 * @see us.pcsw.swing.SwingWorker#finished()
	 */
	public void finished()
	{
		notifyStatusListeners
			(new StatusEvent(this, StatusTypeEnum.NOT_BUSY, this.getValue()));
	}
	
	/**
	 * Returns the string that was executed.
	 */
	public String getSQL()
	{
		return sql;
	}
	
	public boolean isCancelled()
	{
		return cancel;
	}
	
	/**
	 * Sets the SQL to execute.
	 */
	private void setSQL(String sql)
	{
		if (sql == null) {
			throw new IllegalArgumentException("The sql statement cannot be null.");
		} else {
			this.sql = sql;
		}
	}
}
