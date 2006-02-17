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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Date;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.SwingUtilities;

import us.pcsw.dbbrowser.CachingResultSetTableModel;

import us.pcsw.dbbrowser.ResultSetTableModel;

import us.pcsw.dbbrowser.event.StatusEvent;
import us.pcsw.dbbrowser.event.StatusListener;
import us.pcsw.dbbrowser.event.StatusTypeEnum;

import us.pcsw.swing.SwingWorker;

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
final class SQLExecutionWorker extends SwingWorker
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
	 * List of listeners to be notified of status events.
	 */
	private Vector statusListeners;
	
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

	/**
	 * Adds a listener to the list of those to be notified of status events
	 * thrown by this class.
	 * @param listener The listener.
	 */
	public void addStatusListener(StatusListener listener)
	{
		if (statusListeners == null) {
			statusListeners = new Vector(1, 1);
		}
		if (! statusListeners.contains(listener)) {
			statusListeners.add(listener);
		}
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
			int count = stmt.getUpdateCount();
			if (count == -1) {
			// The following code is commented out because though it would
			// allow multiple resultsets to be returned, it would mean that
			// CachingResultSetTableModels could not be used.
//			do {
				rs = stmt.getResultSet();
				if (rs != null) {
					ResultSetTableModel rstm = new CachingResultSetTableModel(rs);
					rstm.getValueAt(0,0); // Initialize the cache
					execResults.getResultSetModelList().add(rstm);
				}
//			} while (stmt.getMoreResults() && stmt.getUpdateCount() == -1);
			} else {
				execResults.setResultCount(count);
			}
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
	 * Notifies all listeners of a status event.
	 * @param ae The event to notify the listeners of.
	 */
	void notifyStatusListeners(StatusEvent se)
	{
		if (SwingUtilities.isEventDispatchThread()) {
			if (statusListeners != null) {
				// Clone the vector because we do not want to run into trouble if a
				// listener adds or removes itself while we are iterating through the
				// enumeration. 
				Vector clone = (Vector)statusListeners.clone();
				StatusListener listener;
				Enumeration e = clone.elements();
				while (e.hasMoreElements()) {
					listener = (StatusListener)e.nextElement();
					listener.statusChanged(se);
				}
			}
		} else {
			SwingUtilities.invokeLater(new NotifyWorker(this, se));
		}
	}
	
	/**
	 * Removes the status listener from those that should be notified of
	 * status events.
	 * @param listener The listener.
	 */
	public void removeStatusListener(StatusListener listener)
	{
		if (statusListeners != null) {
			statusListeners.remove(listener);
		}
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

/**
 * Helper class for SQLExceptionWorker.
 */
class NotifyWorker extends Object
	implements Runnable
{
	private StatusEvent se;
	private SQLExecutionWorker worker;
	
	NotifyWorker(SQLExecutionWorker worker, StatusEvent se)
	{
		this.se = se;
		this.worker = worker;
	}
	
	public void run()
	{
		worker.notifyStatusListeners(se);
	}
}