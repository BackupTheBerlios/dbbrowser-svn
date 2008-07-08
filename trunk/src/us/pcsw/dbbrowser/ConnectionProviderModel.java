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

import java.util.Enumeration;
import java.util.Vector;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * us.pcsw.dbbrowser.ConnectionProviderModel
 * -
 * Contains a list of all available ConnectionProvider implementations.  This
 * class is a list model and a table model.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>07/23/2001 This class was created.</LI>
 * <LI>03/20/2003 The methods to read the list from a file and save it to a
 *                file were removed.  The list is now stored in the config
 *                file instead of its own seperate file.  PAC </LI>
 * <LI>04/22/2003 Fixed sort order bug.  PAC </LI>
 * </UL></P>
 *
 * @author Philip A. Chapman
 */
public final class ConnectionProviderModel
    implements javax.swing.ComboBoxModel, javax.swing.table.TableModel
{
	private static final Logger logger = LoggerFactory.getLogger(ConnectionProviderModel.class);
	
    /**
     * The index of the selected item.
     */
    private int selectedItem = -1;

    /**
     * Contains the ConnectionProvider list.
     */
    private Vector cpList = new Vector(1, 1);
    
    /**
     * Holds registered list model listeners.
     */
    private Vector listDataListeners = new Vector(1, 1);

    /**
     * Holds registered Table model listeners.
     */
    private Vector tableModelListeners = new Vector(1, 1);

    /**
     * Initializes the ConnectionProviderModel instance.
     */
    public ConnectionProviderModel() {
        super();
    }

    /**
     * Adds a connection provider to the list.  If an entry is already listed for
     * the indicated database, it's related classname will be updated.  Neither
     * dbName, nor className may be null.
     * @param dbName The name of database to which the provider connects.
     * @param className The classname of the ConnectionProvider implementation.
     * @return The postition into which the item is inserted.
     */
    public int addConnectionProvider(String dbName, String className)
    {
        ConnectionProviderModelItem cpmi = new ConnectionProviderModelItem(dbName, className);
        return addConnectionProvider(cpmi);
    }
	/**
	 * Adds a connection provider to the list.  If an entry is already listed for
	 * the indicated database, it's related classname will be updated.
	 * @param cpmi The connection provider list item to add.
	 * @return The postition into which the item is inserted.
	 */    
    public int addConnectionProvider(ConnectionProviderModelItem cpmi)
    {
    	int index, max;
    	
        index = cpList.indexOf(cpmi);
        if (index > -1) {
            // The item already exists, update the value
            ((ConnectionProviderModelItem)cpList.elementAt(index)).setClassName(cpmi.getClassName());
            notifyTableModelListeners(
                new TableModelEvent(
                    this,
                    index,
                    index,
                    1,
                    TableModelEvent.UPDATE));
        } else {
            // The item does not exist, insert it.
            max = cpList.size();
            // Determine which position to insert it into.
            for (index = 0; index < max; index++) {
                if (((ConnectionProviderModelItem)cpList.elementAt(index))
                    .compareTo(cpmi)
                    > 0) {
                    break;
                }
            }
            cpList.add(index, cpmi);
            notifyListDataListeners(
                new ListDataEvent(
                    this,
                    ListDataEvent.INTERVAL_ADDED,
                    index,
                    index));
            notifyTableModelListeners(
                new TableModelEvent(
                    this,
                    index,
                    index,
                    TableModelEvent.ALL_COLUMNS,
                    TableModelEvent.INSERT));
        }
        return index;
    }

    /**
     * Adds a ListDataListener to the list of listeners to be notified.
     * @param l The ListDataListener to be added.
     */
    public void addListDataListener(ListDataListener l)
    {
        if (!listDataListeners.contains(l)) {
            listDataListeners.addElement(l);
        }
    }

    /**
     * Adds a TableModelListener to the list of listeners to be notified.
     * @param l The ListModelListener to be added.
     */
    public void addTableModelListener(TableModelListener l)
    {
        if (!tableModelListeners.contains(l)) {
            tableModelListeners.addElement(l);
        }
    }

    public Class getColumnClass(int index)
    {
    	return String.class;
    }

    public int getColumnCount()
    {
        return 2;
    }

    public String getColumnName(int index)
    {
        if (index == 0) {
            return "Name";
        } else if (index == 1) {
            return "Class";
        } else {
            return "";
        }
    }

    /**
     * Gets the package and class name of the ConnectionProvider implementation
     * which connects to the given database.
     * @param dbName the name of the database to which the returned
     *               implementation should connect.
     * @return The package and name of the class, or null if no such
     *         implementation exists in the list.
     */
    public String getConnectionProviderClass(String dbName)
    {
        for (int index = 0; index < cpList.size(); index++) {
            if (cpList.elementAt(index).equals(dbName)) {
            	logger.debug("Connection Provider {} found.", dbName);
                return ((ConnectionProviderModelItem)cpList.elementAt(index))
                    .getClassName();
            }
        }
    	logger.debug("Connection Provider {} not found.", dbName);
        return null;
    }

    public Object getElementAt(int index)
    {
        return cpList.elementAt(index);
    }

    public int getRowCount() {
        return cpList.size();
    }

    public Object getSelectedItem()
    {
        if (selectedItem < 0 || selectedItem >= getSize()) {
            return null;
        } else {
            return cpList.elementAt(selectedItem);
        }
    }

    public int getSize()
    {
        return cpList.size();
    }

    public Object getValueAt(int rowIndex, int colIndex)
    {
        ConnectionProviderModelItem cpmi =
            (ConnectionProviderModelItem)cpList.elementAt(rowIndex);

        if (colIndex == 0) {
            return cpmi.toString();
        } else if (colIndex == 1) {
            return cpmi.getClassName();
        } else {
            return "";
        }
    }

    public boolean isCellEditable(int rowIndex, int colIndex)
    {
        return false;
    }

    /**
     * Notifies all registered ListDataListeners of a change.
     * @param event The ListDataEvent to notify the listeners with.
     */
    private void notifyListDataListeners(ListDataEvent lme)
    {
        if (listDataListeners != null) {
            Object o;
            int type = lme.getType();

            // Clone vector of listeners in case a listener's dataChanged
            // method removes the listener from the list.
            Vector copyOfListeners = (Vector) (listDataListeners.clone());

            // Notify each listener.
            Enumeration e = copyOfListeners.elements();
            while (e.hasMoreElements()) {
                o = e.nextElement();
                switch (type) {
                    case ListDataEvent.CONTENTS_CHANGED :
                         ((ListDataListener)o).contentsChanged(lme);
                        break;
                    case ListDataEvent.INTERVAL_ADDED :
                         ((ListDataListener)o).intervalAdded(lme);
                        break;
                    case ListDataEvent.INTERVAL_REMOVED :
                         ((ListDataListener)o).intervalRemoved(lme);
                }
            }
        }
    }

    /**
     * Notifies all registered TableModelListeners of a change.
     * @param event The TableModelEvent to notify the listeners with.
     */
    private void notifyTableModelListeners(TableModelEvent tme)
    {
        if (tableModelListeners != null) {
            Object o;

            // Clone vector of listeners in case a listener's dataChanged
            // method removes the listener from the list.
            Vector copyOfListeners = (Vector) (tableModelListeners.clone());

            // Notify each listener.
            Enumeration e = copyOfListeners.elements();
            while (e.hasMoreElements()) {
                o = e.nextElement();
                ((TableModelListener)o).tableChanged(tme);
            }
        }
    }

	/**
	 * Removes the connection provider item at the given index.
	 * @param index The index of the item to remove.
	 */
	public void removeConnectionProviderItem(int index)
	{
		if (index > -1 && index < cpList.size()) {
			cpList.remove(index);
			notifyTableModelListeners(
				new TableModelEvent(
					this,
					index,
					index,
					1,
					TableModelEvent.DELETE));
		}
	}

    /**
     * Removes a ListDataListener from the list of listeners to be notified.
     * @param l The ListDataListener to be removed.
     */
    public void removeListDataListener(ListDataListener l)
    {
        // There should only be once instance of l.  However, just to be safe,
        // I use a while loop here.
        while (listDataListeners.contains(l)) {
            listDataListeners.remove(l);
        }
    }

    /**
     * Removes a each ListDataListener in the array from the list of
     * listeners to be notified.
     * @param l[] The ListDataListeners to be removed.
     */
    public void removeListDataListeners(ListDataListener l[])
    {
        for (int i = 0; i < l.length; i++) {
            removeListDataListener(l[i]);
        }
    }

    /**
     * Removes a TableModelListener from the list of listeners to be notified.
     * @param l The TableModelListener to be removed.
     */
    public void removeTableModelListener(TableModelListener l)
    {
        // There should only be once instance of l.  However, just to be safe,
        // I use a while loop here.
        while (tableModelListeners.contains(l)) {
            tableModelListeners.remove(l);
        }
    }

    /**
     * Removes a each TableModelListener in the array from the list of
     * listeners to be notified.
     * @param l[] The TableModelListeners to be removed.
     */
    public void removeTableModelListeners(TableModelListener l[])
    {
        for (int i = 0; i < l.length; i++) {
            removeTableModelListener(l[i]);
        }
    }

    public void setSelectedItem(Object o)
    {
        selectedItem = cpList.indexOf(o);
    }

	/**
	 * Not implemented the individual cells are not editable.
	 */
    public void setValueAt(Object value, int rowIndex, int colIndex)
    {}
}
