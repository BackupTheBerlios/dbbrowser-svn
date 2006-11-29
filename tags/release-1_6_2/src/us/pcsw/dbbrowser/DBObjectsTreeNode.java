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

import java.sql.SQLException;
import java.util.Stack;
import java.util.Vector;

import us.pcsw.dbbrowser.event.*;

/**
 * us.pcsw.dbbrowser.DBobjectsTreeNode
 * -
 * A node in a tree of available database objects.
 *
 * <P><B>Revision History:</B><UL>
 * <LI>08/06/2002 This class was created.</LI>
 * </UL></P>
 * 
 *
 * @author Philip A. Chapman
 */
public abstract class DBObjectsTreeNode
    extends java.lang.Object
{
    static final protected int NODES_CHANGED = 0;
    static final protected int NODES_INSERTED = 1;
    static final protected int NODES_REMOVED = 2;
    static final protected int STRUCTURE_CHANGED = 3;

    /**
     * The node's children.
     */
    protected Vector children = null;

    /**
     * The DBObjectsTreeNode 1 level above this node, null if this is the root
     * node.
     */
    protected DBObjectsTreeNode parent = null;

    /**
     * Initializes a new instance of DBobjectsTreeNode.
     * @param parent The DBObjectsTreeNode 1 level above this node, null if
     *               this is the root node.
     */
    protected DBObjectsTreeNode(DBObjectsTreeNode parent)
    {
		this.parent = parent;
    }

    /**
     * Returns the child at the indicated index.
     * @param index the index of the child.
     */
    Object getChild(int index)
    {
		if (isLeaf()) {
			return null;
		} else {
			Vector v = getChildren();
		    return v.elementAt(index);
		}
    }

    /**
     * Returns the number of children. Returns 0 if the node is a leaf or if it
     * has no children.
     * @return the number of children of the node.
     */
    int getChildCount()
    {
		Vector v = getChildren();
	    return v.size();
    }

	/**
	 * Gets the vector which holds the child nodes.
	 */
	protected Vector getChildren()
	{
		if (children == null) {
			try {
				refresh();
			} catch (Throwable t) {}
			// If no children were added by the refresh method, create an
			// empty Vector.
			if (children == null) {
				children = new Vector(1,1);
			}
		}
		return children;
	}

    /**
     * Returns the index of the child.
     * @param child one of the node's children..
     * @return the index of the child.
     */
    int getIndexOfChild(Object child)
    {
		Vector v = getChildren();
		return v.indexOf(child);
    }
    
    /**
     * Returns the parent node)
     */
    DBObjectsTreeNode getParent()
    {
    	return parent;
    }

    /**
     * Inserts the indicated node into the list of children and notifies
     * this object's parent of the change.
     * @param childNode the child node to insert.  If the child is not a
     *                   subclass of DBObjectsTreeNode, it will be treated as
     *                   a leaf node.  The child node MUST NOT already exist
     *                   in children.
     */
    protected void insertChildNode(Object childNode)
    {
		Object[] childNodes = new Object[1];
		childNodes[0] = childNode;
		insertChildNodes(childNodes);
    }

    /**
     * Inserts the indicated node into the list of children at the specified
     * position and notifies this object's parent of the change.
     * @param childNode the child node to insert.  If the child is not a
     *                   subclass of DBObjectsTreeNode, it will be treated as
     *                   a leaf node.  The child node MUST NOT already exist
     *                   in children.
     * @param index The position into which the child should be inserted.
     */
    protected void insertChildNode(Object childNode, int position)
    {
    	if (children == null || position > children.size() - 1) {
    		Object[] childNodes = new Object[1];
			childNodes[0] = childNode;
			insertChildNodes(childNodes);
    	} else {
    		children.add(position, childNode);
    		int[] indices = {position};
    		Object[] insertedChildren = {childNode};
    		notifyParent(NODES_INSERTED, new Stack(), indices, insertedChildren);
    	}
    }

    /**
     * Inserts the indicated nodes into the list of children and notifies this
     * object's parent of the change.
     * @param childNodes the child nodes to insert.  If the child is not a
     *                    subclass of DBObjectsTreeNode, it will be treated as
     *                    a leaf node.  The child nodes MUST NOT already exist
     *                    in children.
     */
    protected void insertChildNodes(Object[] childNodes)
    {
		if (children == null) {
		    children = new Vector();
		}
		int j = childNodes.length;
		int[] indices = new int[j];
		Object[] insertedChildren = new Object[j];
		for (int i = 0; i < j; i++) {
	    	children.add(childNodes[i]);
		    indices[i] = children.indexOf(childNodes[i]);
		    insertedChildren[i] = childNodes[i];
		}
		notifyParent(NODES_INSERTED, new Stack(), indices, insertedChildren);
    }

    /**
     * Returns true if the node is a leaf. It is possible for this method to
     * return false if the node could possibly have children, but doesn't. A
     * database will have a view node, but may not have any views.  The view
     * node is not a leaf, but has no children.
     * @return true if the node is a leaf
     */
    abstract boolean isLeaf();

	/**
	 * This method should be overridden by the top node in the hierarchy so
	 * that the event can be created and propogated out to listeners.  By
	 * default, the event info is pushed up the tree.
	 */
	protected void notified(int nodeEventType, Stack path)
	{
		notifyParent(nodeEventType, path);
	}
	
	/**
	 * This method should be overridden by the top node in the hierarchy so
	 * that the event can be created and propogated out to listeners.  By
	 * default, the event info is pushed up the tree.
	 */
	protected void notified(int nodeEventType, Stack path, int[] indices,
							Object[] children)
	{
		notifyParent(nodeEventType, path, indices, children);											
	}

	/**
	 * This method should be overridden by the top node in the hierarchy so
	 * that the event can be propogated out to listeners.  By default, the
	 * event is pushed up the tree.
	 */
	protected void notified(StatusEvent se)
	{
		notifyParent(se);
	}

    /**
     * Notifieds the parent of a NODES_CHANGED or STRUCTURE_CHANGED event.
     */
    protected void notifyParent(int nodeEventType, Stack path)
    {
		path.push(this);
		if (parent != null) {
	    	parent.notified(nodeEventType, path);
		}
    }

	/**
	 * Notifies the paretn of a StatusEvent.
	 */
	protected void notifyParent(StatusEvent se)
	{
		if (parent != null) {
			parent.notified(se);
		}
	}

    /**
     * Notifies the parent of a NODES_CHANGED or STRUCTURE_CHANGED event.
     */
    protected void notifyParent(int nodeEventType, Stack path, int[] indices,
				Object[] children)
    {
		path.push(this);
		if (parent != null) {
	    	parent.notified(nodeEventType, path, indices, children);
		}
    }

	/**
	 * Should cause the object to requery for its child nodes.
     * @exception ClassNotFoundException indicates there was an error
     *                       connecting to the database.
     * @exception IllegalAccessException indicates there was an error
     *                       connecting to the database.
     * @exception InstantiationException indicates there was an error
     *                       connecting to the database.
     * @exception SQLException indicates that an error occurred while querying
     *                         the database server.
	 */
	public abstract void refresh()
		throws ClassNotFoundException, IllegalAccessException,
			          InstantiationException, SQLException;

    /**
     * Removes the indicated DBObjectsTreeNode from the list of children and
     * notifies the parent of the change.
     * @param childNode the child node to remove.  The child node MUST exist in
     *                  children.
     */
    protected void removeChildNode(DBObjectsTreeNode childNode)
    {
		if (children != null) {
		    DBObjectsTreeNode[] childNodes = new DBObjectsTreeNode[1];
	    	childNodes[0] = childNode;
		    removeChildNodes(childNodes);
		}
    }

    /**
     * Removes the indicated DBObjectsTreeNodes from the list of children and
     * notifies the parent of the change.
     * @param childNodes the child nodes to remove.  The child nodes MUST exist
     *                   in children.  This is an array of Objects for
     *                   convenience, but the actual items MUST be an instance
     *                   of DBObjectsTreeNode.
     */
    protected void removeChildNodes(Object[] childNodes)
    {
		if (children != null) {
		    int j = childNodes.length;
	    	int[] indices = new int[j];
		    Object[] removedChildren = new Object[j];
		    for (int i = 0; i < j; i++) {
				indices[i] = children.indexOf(childNodes[i]);
		    }
		    for (int i = 0; i < j; i++) {
				children.remove(childNodes[i]);
				removedChildren[i] = childNodes[i];
		    }
		    notifyParent(NODES_REMOVED, new Stack(), indices,
						 removedChildren);
		}
    }
}
