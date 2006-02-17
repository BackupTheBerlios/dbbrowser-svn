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

import us.pcsw.dbbrowser.CachingResultSetTableModel;
import us.pcsw.dbbrowser.LoadedResultSetTableModel;
import us.pcsw.dbbrowser.bsh.BeanShellSession;
import us.pcsw.dbbrowser.bsh.DbbBshConstants;

import bsh.CallStack;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * us.pcsw.dbbrowser.bsh.commands.dbbPrintResult
 * -
 * The DbbPrintResults command outputs the results of the resultset to
 * the dbbrowser resultset table.
 *
 * <P><STRONG>Revision History:</STRONG><UL>
 * <LI>Feb 16, 2006 This class was created by pchapman.</LI>
 * </UL></P>
 *
 * @author pchapman
 */
public class dbbPrintResult
{
	/**
	 * This class only has static methods.
	 */
	private dbbPrintResult()
	{
		super();
	}
	
	/**
	 * Implement dbbPrintResult( ResultSet resultSet ) command.  The
	 * resultset's entire contents will be read into memory.  This may be
	 * memory intensive and may take a long time.
	 */
	public static void invoke( 
			Interpreter env, CallStack callstack, ResultSet resultSet
		) 
		throws EvalError, SQLException
	{
		invoke(env, callstack, resultSet, true);
	}
	
	/**
	 * Implement dbbPrintResult( ResultSet resultSet ) command.  If loadAll is
	 * true, the resultset's entire contents will be read into memory.  This
	 * may be memory intensive and may take a long time.  If it is false, the
	 * resultset is held and data is read on demand.  The calling script must
	 * be careful that the resultset or its statement is not closed.
	 */
	public static void invoke( 
			Interpreter env, CallStack callstack, ResultSet resultSet, boolean loadAll
		)
		throws EvalError, SQLException
	{
		BeanShellSession sess = (BeanShellSession)env.get(DbbBshConstants.VAR_BEAN_SHELL_SESSION);
		if (sess != null) {
			if (loadAll) {
				sess.getResultList().add(new LoadedResultSetTableModel(resultSet));
			} else {
				sess.getResultList().add(new CachingResultSetTableModel(resultSet));
			}
		}
	}
}
