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

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;
import java.util.WeakHashMap;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;

import us.pcsw.dbbrowser.cp.*;
import us.pcsw.dbbrowser.event.*;

/**
 * dbbrowser.DBobjectsTreeModel
 * -
 * Provides the data for a treeview which displays the available databases
 * and their objects.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>08/14/2002 This class was created.</LI>
 * <LI>02/15/2003 The ability to notify status listeners of status events was
 *                added.</LI>
 * </UL></P>
 * 
 * @author Philip A. Chapman
 */
public final class DBObjectsTreeModel
    extends DBObjectsTreeNode
    implements javax.swing.tree.TreeModel
{
    /**
     * The connection provider to the database server.
     */
    private ConnectionProvider conProv = null;

	/**
	 * Listeners for the status event.
	 */
	protected WeakHashMap statusListeners = new WeakHashMap();

    /**
     * Holds registered tree model listeners.
     */
    private WeakHashMap treeModelListeners = new WeakHashMap();
	
    /**
     * Creates a new instance of the tree model and loads the tree view data
     * from the given connection.
     * @param conProv The connection provider to the database server.
     * @exception ClassNotFoundException indicates there was an error
     *                       connecting to the database.
     * @exception IllegalAccessException indicates there was an error
     *                       connecting to the database.
     * @exception InstintiationException indicates there was an error
     *                       connecting to the database.
     * @exception SQLException indicates that an error occurred while querying
     *                         the database server.
     */
    public DBObjectsTreeModel(ConnectionProvider conProv)
		throws ClassNotFoundException, IllegalAccessException,
		              InstantiationException, SQLException
    {
		super(null);
		if (conProv == null) {
	    	throw new IllegalArgumentException("Invalid ConnectionProvider.");
		} else {
			this.conProv = conProv;
		}
    }

    /**
     * Adds a StatusListener to the list to be notified of StatusEvents.
     * @param l The StatusListener to be notified.
     */
    public void addStatusListener(StatusListener l)
    {
		// don't add the listener if it's already in the list
		if (! statusListeners.containsKey(l)) {
		    statusListeners.put(l, new WeakReference(l));
		}	
    }

    /**
     * Adds a TreeModelListener to the list of listeners to be notified.
     * @param l The TreeModelListener to be added.
     */
    public void addTreeModelListener(TreeModelListener l)
    {
		if (!treeModelListeners.containsKey(l)) {
		    treeModelListeners.put(l, new WeakReference(l));
		}
    }

	/**
	 * Builds a tree path from the stack of nodes.
	 */
	private TreePath buildTreePath(Stack s)
	{
		Object obj = null;
		TreePath tp = null;
		
		try {
			while (true) {
				obj = s.pop();
				if (tp == null) {
					tp = new TreePath(obj);
				} else {
					tp = tp.pathByAddingChild(obj);
				}
			}
		} catch (EmptyStackException epe) {
			// This is OK.  Eventually, I'll run out of nodes.
		}
		return tp;
	}

    /**
     * Returns the child of parent at index index in the parent's child array.
     * @param parent a node in the tree, obtained from this data source
     * @param index the index of the child in the parent.
     */
    public Object getChild(Object parent, int index)
    {
		return ((DBObjectsTreeNode)parent).getChild(index);
    }

    /**
     * Returns the number of children of parent. Returns 0 if the node is a
     * leaf or if it has no children. parent must be a node previously obtained
     * from this data source.
     * @param parent a node in the tree, obtained from this data source
     * @return the number of children of the node parent
     */
    public int getChildCount(Object parent)
    {
		return ((DBObjectsTreeNode)parent).getChildCount();
    }

	/**
	 * Returns the connection provider used to populate this model.
	 * @return the connection provider.
	 */
	public ConnectionProvider getConnectionProvider()
	{
		return conProv;
	}

    /**
     * Returns the index of child in parent.
     * @param parent a node in the tree, obtained from this data source
     * @param child a node in the tree, located 1 level below parent.
     * @return the index of the child in the parent.
     */
    public int getIndexOfChild(Object parent, Object child)
    {
		return ((DBObjectsTreeModel)parent).getIndexOfChild(child);
    }

    /**
     * Returns the root of the tree. Returns null only if the tree has no
     * nodes.
     * @return the root of the tree
     */
    public Object getRoot()
    {
		return this;
    }
    
    /**
     * Gets a title for the data presented in the tree node.
     */
    public String getTitle()
    {
		try {
		    Connection con = conProv.getConnection();
		    DatabaseMetaData dmd = con.getMetaData();
		    StringBuffer sb = new StringBuffer();
		    sb.append(dmd.getDatabaseProductName());
		    sb.append(" - ");
		    dmd = con.getMetaData();
			sb.append(conProv.getServerName());
		    return sb.toString();
		} catch (Exception e) {
		    return "[Not Connected]";
		}
    }

    /**
     * Returns true. if node is a leaf. It is possible for this method to
     * return false if the node could possibly have children, but doesn't. A
     * database will have a view node, but may not have any views.  The view
     * node is not a leaf, but has no children.
     * @param node a node in the tree, obtained from this data source
     * @return true if node is a leaf
     */
    public boolean isLeaf(Object node)
    {
    	if (node instanceof DBObjectsTreeNode) {
			return ((DBObjectsTreeNode)node).isLeaf();
    	} else {
    		return true;
    	}
    }

    /**
     * Returns false.  This node is never a leaf.
     */
    boolean isLeaf()
    {
		return false;
    }
    
	/**
	 * Builds and fires the appropriate TreeModelEvent.
	 */
	protected void notified(
		int nodeEventType,
		Stack path,
		int[] indices,
		Object[] children)
	{
		TreeModelEvent tme = new TreeModelEvent(this, buildTreePath(path),
		                                        indices, children);
		notifyTreeModelListeners(tme, nodeEventType);
	}

	/**
	 *  Builds and fires the appropriate TreeModelEvent.
	 */
	protected void notified(int nodeEventType, Stack path)
	{
		TreeModelEvent tme = new TreeModelEvent(this, buildTreePath(path));
		notifyTreeModelListeners(tme, nodeEventType);
	}

	/**
	 * Fires the StatusEvent.
	 */
	protected void notified(StatusEvent se) {
		notifyStatusListeners(se);
	}
    
	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#notifyParent(int, Stack, int[], Object[])
	 */
	protected void notifyParent(int nodeEventType,
	                              Stack path,
	                              int[] indices,
	                              Object[] children)
	{
		// There is no parent, so notify self, which causes events to be
		// raised.
		path.push(this);
		notified(nodeEventType, path, indices, children);
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#notifyParent(int, Stack)
	 */
	protected void notifyParent(int nodeEventType, Stack path)
	{
		// There is no parent, so notify self, which causes events to be
		// raised.
		path.push(this);
		notified(nodeEventType, path);
	}

	/**
	 * @see us.pcsw.dbbrowser.DBObjectsTreeNode#notifyParent(StatusEvent)
	 */
	protected void notifyParent(StatusEvent se)
	{
		// There is no parent, so notify self, which causes events to be
		// raised.
		notified(se);
	}

    /**
     * The StatusEvent is sent to all registered listeners.  This is done in
     * the current thread.
     * @param event The StatusEvent to notify the listeners with.
     */
    protected void notifyStatusListeners(StatusEvent e)
    {
		StatusListener sListener;
		Iterator iterator;
	
		// Notify each listener.  Since this class "echos" events recieved,
		// a check is made to be sure that the originating object is not
		// notified.  Such a thing may cause an endless loop if the source
		// object doesn't check the event's origin.
		iterator = statusListeners.values().iterator();
		while (iterator.hasNext()) {
		    sListener = ((StatusListener)((WeakReference)iterator.next()).get());
		    if (sListener != null) {
				sListener.statusChanged(e);
		    }
		}
    }

    /**
     * Notifies all registered TreeModelListeners of a change.
     * @param event The TreeModelEvent to notify the listeners with.
     * @param action The action that is being reported.
     */
    private void notifyTreeModelListeners(TreeModelEvent tme, int action)
    {
		if (treeModelListeners != null) {
		    TreeModelListener o;
			Iterator iterator;

		    // Notify each listener.
		    iterator = treeModelListeners.values().iterator();
	    	while (iterator.hasNext()) {
				o = ((TreeModelListener)((WeakReference)iterator.next()).get());
				switch (action) {
					case NODES_CHANGED :
		    			((TreeModelListener)o).treeNodesChanged(tme);
					    break;
					case NODES_INSERTED :
		    			((TreeModelListener)o).treeNodesInserted(tme);
					    break;
					case NODES_REMOVED :
		    			((TreeModelListener)o).treeNodesRemoved(tme);
					    break;
					case STRUCTURE_CHANGED :
		    			((TreeModelListener)o).treeStructureChanged(tme);
				}
	    	}
		}
    }

    /**
     * Refreshes the tree view with data from the server.
     * @exception ClassNotFoundException indicates there was an error
     *                       connecting to the database.
     * @exception IllegalAccessException indicates there was an error
     *                       connecting to the database.
     * @exception InstantiationException indicates there was an error
     *                       connecting to the database.
     * @exception SQLException indicates that an error occurred while querying
     *                         the database server.
     */
    public void refresh()
		throws ClassNotFoundException, IllegalAccessException,
			          InstantiationException, SQLException
    {
		notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.BUSY,
		                                      "Loading the list of catalogs."));
		try {
			// Clear children
			if (children != null && children.size() > 0) {
				int indices[] = new int[children.size()];
				Object nodes[] = new Object[children.size()];
				for (int i = 0; i < indices.length; i++) {
					indices[i] = i;
					nodes[i] = children.elementAt(i);
				}
				notifyParent(NODES_REMOVED, new Stack(), indices, nodes);
				children = new Vector();
			}

			Connection con = conProv.getConnection();

			// Get a list of all catalogs unless we are connected to
			// Postgresql.
			// <<HACK>>Postgresql lists all available catalogs, but
			// returns metadata for only the currently connected catalog.
			// Therefore, showing all catalogs has only a little value.
			if (! (conProv instanceof us.pcsw.dbbrowser.cp.postgresql.ConnectionProvider)) {
				DatabaseMetaData dmd = con.getMetaData();
				ResultSet rs = dmd.getCatalogs();
				while (rs.next()) {
					insertChildNode(new CatalogTreeNode(this, con, rs.getString("TABLE_CAT")));
				}
				rs.close();
			}
			if (children == null || children.size() == 0) {
				// Some RDBMSs do not have a concept of catalogs
				insertChildNode(new CatalogTreeNode(this, con, null));
			}
		} catch (Throwable t) {
			notifyStatusListeners(new StatusEvent(this, t));
		} finally {
			notifyStatusListeners(new StatusEvent(this, StatusTypeEnum.NOT_BUSY,
			                                      "Finished loading the list of catalogs."));
		}
    }
    
    /**
     * Removes a StatusListener from the list to be notified of StatusEvents.
     * @param l The StatusListener to be removed.
     */
    public void removeStatusListener(StatusListener l)
    {
    	statusListeners.remove(l);
    }
    
    /**
     * Removes a TreeModelListener from the list of listeners to be notified.
     * @param l The TreeModelListener to be removed.
     */
    public void removeTreeModelListener(TreeModelListener l)
    {
		treeModelListeners.remove(l);
    }

    public String toString()
    {
    	return conProv.getServerName();
    }

    /**
     * Messaged when the user has altered the value for the item identified by
     * path to newValue. If newValue signifies a truly new value the model
     * should post a treeNodesChanged event.
     *
     * <B>This method is not implemented as all items are immutable.</B>
     *
     * @param path path to the node that the user has altered.new
     * @param newValue the new value from the TreeCellEditor.
     */
    public void valueForPathChanged(TreePath path, Object newValue)
    {
    }
}
