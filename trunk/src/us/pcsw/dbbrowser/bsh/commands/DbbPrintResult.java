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
package us.pcsw.dbbrowser.bsh.commands;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.List;

import us.pcsw.dbbrowser.CachingResultSetTableModel;
import us.pcsw.dbbrowser.LoadedResultSetTableModel;

import bsh.CallStack;
import bsh.Interpreter;

/**
 * us.pcsw.dbbrowser.bsh.commands.DbbPrintResult
 * -
 * Description for DbbPrintResult.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Feb 16, 2006 This class was created by pchapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public class DbbPrintResult
{
	/**
	 * This class only has static methods.
	 */
	private DbbPrintResult()
	{
		super();
	}
	
	private static List results;
	
	public static List getResultSetModels()
	{
		return results;
	}
	
	/**
	 * Implement dbbPrintResult( ResultSet resultSet ) command.  The
	 * resultset's entire contents will be read into memory.  This may be
	 * memory intensive and may take a long time.
	 */
	public static void invoke( 
			Interpreter env, CallStack callstack, ResultSet resultSet
		) 
	{
		invoke(env, callstack, resultSet, true);
	}
	
	/**
	 * Implement dir( String directory ) command.
	 */
	public static void invoke( 
			Interpreter env, CallStack callstack, ResultSet resultSet, boolean loadAll
		) 
	{
		try {
			if (loadAll) {
				results.add(new LoadedResultSetTableModel(resultSet));
			} else {
				results.add(new CachingResultSetTableModel(resultSet));
			}
		} catch (SQLException sqle) {
			results.add(sqle);
		}
	}
}
