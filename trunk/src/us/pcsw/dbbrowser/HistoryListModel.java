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

/**
 * us.pcsw.dbbrowser.HistoryListModel
 * -
 * Contains up to the last 50 SQL statement which have been executed.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>05/24/2001 Development began on this class.</LI>
 * <LI>10/18/2002 Removed the ability to persist the history list.  Now that
 *                multiple connections can be made at once and login data is
 *                not cached, this feature is not as usefull (or simple to
 *                implement).  It may come back in some form if I ever
 *                re-introduce a method of persisting connection
 *                information.</LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public final class HistoryListModel
	extends javax.swing.AbstractListModel
{
	private static final long serialVersionUID = 1L;
	
    /**
     * Contains the SQL statement list.
     */
    LinkedList historyList = null;

    /**
     * Initializes the HistoryListModel instance.
     */
    public HistoryListModel()
    {
		super();
		historyList = new LinkedList();
    }

    /**
     * Adds an SQL statement to the list.  If there are 25 statements in the
     * list, the oldest one is deleted so that there will still be 25 after the
     * add.  If the statement is equal to the latest statement, it is not
     * added.
     * @param sql The SQL statement.
     */
    public void addStatement(String sql)
    {
		if (sql != null) {
		    if (historyList.size() == 0) {
				historyList.addLast(sql);
				fireIntervalAdded(this, historyList.size() - 1,
								  historyList.size() - 1);
		    } else if (! sql.equals(historyList.getLast())) {
				if (historyList.size() == 25) {
				    historyList.removeFirst();
				    fireIntervalRemoved(this, 0, 0);
				}
				historyList.addLast(sql);
				fireIntervalAdded(this, historyList.size() - 1,
								  historyList.size() - 1);
		    }
		}
    }

    /**
     * Clears all SQL statements from the history.
     */
    public void clear()
    {
		int i = historyList.size() - 1;
		historyList.clear();
		fireIntervalRemoved(this, 0, i);
    }

    public Object getElementAt(int index)
    {
		return historyList.get(index);
    }

    public int getSize()
    {
		return historyList.size();
    }
}
