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
package us.pcsw.dbbrowser.bsh;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.util.Date;

import bsh.Interpreter;

import us.pcsw.dbbrowser.ExecutionWorker;

import us.pcsw.dbbrowser.bsh.commands.dbbPrintResult;
import us.pcsw.dbbrowser.cp.ConnectionProvider;

import us.pcsw.dbbrowser.event.StatusEvent;
import us.pcsw.dbbrowser.event.StatusTypeEnum;

/**
 * This class runs a beanshell script in a seperate thread from the GUI and
 * reports the results.
 *
 * <P><B>Revision History:</B><UL>
 * <LI> Feb 17, 2006 This class was created by Philip A. Chapman.</LI>
 * </UL></P>
 */
public final class BshExecutionWorker extends ExecutionWorker
{
	/**
	 * Indicates that the query is being cancelled so that the user is not
	 * given an error message.
	 */
	private boolean cancel = false;
	
	/**
	 * A provider for a connection to the server on which the sql statement
	 * will be executed.
	 */
	private ConnectionProvider cp = null;

	/**
	 * The beanshell script to execute.
	 */
	private String script = null;
	
	private BeanShellSession session;

	/**
	 * Creates a new SQLExcecutionWorker.
	 * @param con The provider used to get a connection to the which the
	 *            script can use.
	 * @param script The beanshell script to execute.
	 */
	public BshExecutionWorker(ConnectionProvider cp, String script)
	{
		super();
		if (cp == null) {
			throw new IllegalArgumentException("The connection provider object cannot be null.");
		} else {
			this.cp = cp;
		}
		setScript(script);
		session = new BeanShellSession();
	}

	public void interrupt() {
		cancel = true;
//		notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));
//		super.interrupt();
	}
	
	/**
	 * @see us.pcsw.swing.SwingWorker#construct()
	 */
	public Object construct()
	{
		BshExecutionResults bshResults = new BshExecutionResults();
		long time = 0;
		try {
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.BUSY));
			Interpreter bsh = new Interpreter();
			bsh.getNameSpace().importCommands(dbbPrintResult.class.getPackage().getName());
			bsh.set(DbbBshConstants.VAR_BEAN_SHELL_SESSION, session);
			bsh.set(DbbBshConstants.VAR_CONNECTION_PROVIDER, cp);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream output = new PrintStream(baos);
			bsh.setOut(output);
			bsh.setErr(output);
			
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_STARTED));
			time = new Date().getTime();
			bsh.eval(script);
			
			bshResults.setRunTimeMills(new Date().getTime() - time);
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));
			bshResults.setResultSetModelList(session.getResultList());
			output.flush();
			output.close();
			bshResults.setOutput(baos.toString());
		} catch (Exception e) {
			// Don't report the error if the statement is being cancelled.
			if (cancel) {
				bshResults.setRunTimeMills(new Date().getTime() - time);
			} else {
				bshResults.setRunTimeMills(new Date().getTime() - time);
				bshResults.getExceptionList().add(e);
				notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));
			}
		} catch (Throwable t) {
			bshResults.setRunTimeMills(0);
			bshResults.getExceptionList().add(t);
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.QUERY_ENDED));
		}
		return bshResults;
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
	 * Returns the beanshell script that was executed.
	 */
	public String getScript()
	{
		return script;
	}
	
	public boolean isCancelled()
	{
		return cancel;
	}
	
	/**
	 * Sets the beanshell script to execute.
	 */
	private void setScript(String script)
	{
		if (script == null) {
			throw new IllegalArgumentException("The script cannot be null.");
		} else {
			this.script = script;
		}
	}
}
