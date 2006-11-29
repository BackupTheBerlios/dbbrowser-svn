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

import java.util.LinkedList;
import java.util.List;

/**
 * us.pcsw.dbbrowser.swing.SQLExecutionResults
 * -
 * An object that contains all the results of running SQL Statements.  This is
 * the output of the SQLExcecurtionWorker.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Feb 16, 2006 This class was created by pchapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public final class SQLExecutionResults
{
	// CONSTRUCTORS
	
	/**
	 * Creates a new instance.
	 */
	public SQLExecutionResults()
	{
		super();
		exceptionList = new LinkedList();
		resultCountList = new LinkedList();
		resultSetModelList = new LinkedList();
	}

	// MEMBERS
	
	private List exceptionList;
	public List getExceptionList()
	{
		return exceptionList;
	}
	
	private List resultCountList;
	public List getResultCountList()
	{
		return resultCountList;
	}
	
	private List resultSetModelList;
	public List getResultSetModelList()
	{
		return resultSetModelList;
	}
	
	private long runTimeMills;
	public long getRunTimeMills()
	{
		return runTimeMills;
	}
	void setRunTimeMills(long runTimeMills)
	{
		this.runTimeMills = runTimeMills;
	}
	
	// METHODS
}
